package com.yongdd.covid_map.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.yongdd.covid_map.R
import com.yongdd.covid_map.databinding.ActivityMapBinding
import com.yongdd.covid_map.utils.DisplayUtils
import com.yongdd.covid_map.utils.SendToView
import com.yongdd.covid_map.utils.ShowAlert
import com.yongdd.covid_map.utils.eventObserve
import com.yongdd.covid_map.viewModel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import kotlin.concurrent.schedule

@AndroidEntryPoint
class MapActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private val vm: MapViewModel by viewModels()

    private lateinit var fusedClient : FusedLocationProviderClient  // 현재 위치에 사용
    private lateinit var nMap:NaverMap // 네이버 맵

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullscreenStatusBar(blackStatusBar = true)
        initMap()
        initVM()
        addObserver()

        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initVM() {
        binding.vm = vm
        binding.lifecycleOwner = this
    }

    private fun addObserver() {
        vm.viewEvent.eventObserve(this) {
            when (it) {
                is SendToView.SendData -> {
                    when(it.dataName) {
                        vm.GO_TO_CALL -> {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.data as String}"))
                            startActivity(intent)
                        }
                        vm.MOVE_CURRENT_LOCATION -> {
                            locationPermissionCheck()
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun initMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {

        // 지도 기본 세팅
        val uiSetting = naverMap.uiSettings
        uiSetting.isCompassEnabled = false
        uiSetting.isScaleBarEnabled = false
        uiSetting.isZoomControlEnabled = false
        uiSetting.logoGravity = Gravity.BOTTOM

        // 로고 위치 세팅 (30 이하는 margin 주지 않아도 navigation bar와 겹치지 않음)
        if(Build.VERSION.SDK_INT >= 30) {
            val margin10DpToPx = DisplayUtils.dpToPx(this@MapActivity, 10)
            uiSetting.setLogoMargin(margin10DpToPx,0,0,getNavigationBarHeight()+margin10DpToPx)
        }

        vm.getListAndAddMarkers(naverMap)

        nMap = naverMap
    }

    private fun locationPermissionCheck() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        // 권한이 있는 경우
        if(permission == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
            return
        }

        // 이전에 거절한 권한
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            permissionReRequest()

        // 거절한 적 없는 경우
        } else {
            val permissionListener: PermissionListener = object : PermissionListener {
                override fun onPermissionGranted() { // 권한 요청 성공
                    getCurrentLocation()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { // 권한 요청 실패
                    permissionReRequest()
                }

            }

            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setRationaleMessage("현재 위치를 가져오기 위하여 권한이 필요합니다.")
                .setPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check()
        }

    }

    private fun permissionReRequest() {
        val ok = {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        ShowAlert(this@MapActivity)
            .twoChoiceAlert(
                title = "위치 권한",
                message = "현재 위치를 가져오기 위하여 권한이 필요합니다.\n" +
                        "권한을 설정하시겠습니까?",
                positiveText = "설정하러 가기",
                negativeText = "취소",
                positive = ok,
                negative = {}
            )
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,null)
            .addOnSuccessListener { location ->
                if(!this::nMap.isInitialized) return@addOnSuccessListener
                if(location==null) {
                    getLastLocation()
                    return@addOnSuccessListener
                }

                vm.moveCameraUpdate(nMap,location.latitude,location.longitude)
            }
            .addOnFailureListener {
                getLastLocation()
            }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if(!this::nMap.isInitialized) return@addOnSuccessListener
                if(location==null) {
                    Snackbar.make(binding.root,"현재 위치를 가져오지 못했습니다😥\n잠시 후 다시 시도해 주세요!",Snackbar.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                vm.moveCameraUpdate(nMap,location.latitude,location.longitude)
            }
            .addOnFailureListener {
                Snackbar.make(binding.root,"현재 위치를 가져오지 못했습니다😥\n잠시 후 다시 시도해 주세요!",Snackbar.LENGTH_SHORT).show()
            }
    }


    // 방향 전환 시 navigation bar 높이 만큼 margin 변경
    override fun changeNavigationBarHeight() {
        super.changeNavigationBarHeight()
        val margin10DpToPx = DisplayUtils.dpToPx(this@MapActivity, 10)
        val margin20DpToPx = DisplayUtils.dpToPx(this@MapActivity, 20)

        // 마커 정보
        ViewCompat.setOnApplyWindowInsetsListener(binding.markerInfoCardView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom + margin10DpToPx
                rightMargin = insets.right + margin10DpToPx
            }
            WindowInsetsCompat.CONSUMED
        }

        // 센터 상세 정보
        ViewCompat.setOnApplyWindowInsetsListener(binding.centerInfoLL) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                rightMargin = insets.right + margin20DpToPx
                leftMargin = insets.left + margin20DpToPx
                topMargin = insets.top + margin10DpToPx // status bar
            }
            WindowInsetsCompat.CONSUMED
        }

    }

    var isExit = false
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 정보 안내창이 떠있는 경우 안보이도록 설정
            if(vm.clickedCenter.value!=null) {
                vm.clickedCenterChange(null)
                return
            }


            if(isExit) {
                ActivityCompat.finishAffinity(this@MapActivity)
            }

            isExit = true
            Toast.makeText(this@MapActivity, "\'뒤로\' 한번 더 누르면, 종료됩니다.", Toast.LENGTH_SHORT).show()

            Timer("SettingUp", false).schedule(3000) {
                isExit = false
            }
        }
    }


}