package com.yongdd.covid_map.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.yongdd.covid_map.databinding.ActivitySplashBinding
import com.yongdd.covid_map.utils.SendToView
import com.yongdd.covid_map.utils.eventObserve
import com.yongdd.covid_map.viewModel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private val vm : SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transparentStatusBar()
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
                is SendToView.ChangeData -> {
                    when(it.dataName) {
                        vm.INTERNET_CHECK -> {
                            // todo : 인터넷 체크 확인
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

    // status bar까지 풀스크린
    private fun transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

}

