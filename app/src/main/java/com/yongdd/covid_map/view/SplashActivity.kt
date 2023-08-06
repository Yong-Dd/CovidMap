package com.yongdd.covid_map.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.yongdd.covid_map.databinding.ActivitySplashBinding
import com.yongdd.covid_map.utils.NetworkControl
import com.yongdd.covid_map.utils.SendToView
import com.yongdd.covid_map.utils.ShowAlert
import com.yongdd.covid_map.utils.eventObserve
import com.yongdd.covid_map.viewModel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding : ActivitySplashBinding
    private val vm : SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullscreenStatusBar(blackStatusBar = true)
        initVM()
        addObserver()
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
                        vm.INTERNET_CHECK -> {
                            if(!NetworkControl.isNetworkConnected(this)) {
                                showNetWorkErrorAlert()
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        // progressBar progress 값
        val progressObserver : Observer<Int> =
            Observer { progress ->
                if(progress >= 100) {
                    val intent = Intent(this@SplashActivity,MapActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        vm.progress.observe(this,progressObserver)


        // 성공 API 개수
        val receivedPageCountObserver : Observer<Int> =
            Observer { count ->
                if(count==10) vm.checkProcessing()
            }
        vm.receivedPageCount.observe(this,receivedPageCountObserver)
    }

    private var showAlert = false
    private fun showNetWorkErrorAlert() {
        if(showAlert) return
        showAlert = true

        ShowAlert(this)
            .oneChoiceAlert(
                title = "인터넷에 연결되어 있지 않습니다😥",
                message = "네트워크 연결 상태를 확인하고 다시 실행해주세요",
                positiveText = "확인"
            ) {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

    }

}

