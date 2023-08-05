package com.yongdd.covid_map.view

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.OnAuthFailedListener
import com.naver.maps.map.OnMapReadyCallback
import com.yongdd.covid_map.R
import com.yongdd.covid_map.databinding.ActivityMapBinding
import com.yongdd.covid_map.viewModel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : BaseActivity() , OnMapReadyCallback {

    private lateinit var binding : ActivityMapBinding
    private val vm : MapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transparentStatusBar()
        initMap()
        initVM()
        addObserver()
    }

    private fun initVM() {
        binding.vm = vm
        binding.lifecycleOwner = this
    }

    private fun addObserver() {

    }

    private lateinit var mapFragment : MapFragment
    private lateinit var mMap : NaverMap

    private fun initMap() {
        // todo : viewModel과 나눌 부분 생각하기
        val fm = supportFragmentManager
        mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(naverMap: NaverMap) {
        mMap = naverMap
        // todo : 마커 꼽기

        // todo : 정보창  custom 고려하기

    }


}