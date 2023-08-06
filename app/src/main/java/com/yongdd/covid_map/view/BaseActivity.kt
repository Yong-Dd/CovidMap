package com.yongdd.covid_map.view

import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

open class BaseActivity : AppCompatActivity(){

    // status bar fullscreen
    fun fullscreenStatusBar(blackStatusBar:Boolean) {
       // status bar text 검은색 변경
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = blackStatusBar

       // full screen
       if (Build.VERSION.SDK_INT >= 30) {
           // navigation 영역 부분 까지 full screen 이라서 필요에 따라 margin 설정을 해줘야 함
           WindowCompat.setDecorFitsSystemWindows(window, false)
           changeNavigationBarHeight()

       } else {
           // status bar 만 full screen
           window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
       }

    }

    // 방향 전환 시 navigation bar 높이 만큼 margin 변경
    open fun changeNavigationBarHeight() {}

    // 네비게이션 바 높이 구하기
    @RequiresApi(Build.VERSION_CODES.R)
    fun getNavigationBarHeight() : Int{
        val inset = windowManager.currentWindowMetrics.windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.navigationBars())
        return inset.bottom
    }


}