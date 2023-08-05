package com.yongdd.covid_map.view

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.yongdd.covid_map.databinding.ActivityMapBinding
import com.yongdd.covid_map.utils.SendToView
import com.yongdd.covid_map.utils.eventObserve

class MapActivity : BaseActivity()  {

    private lateinit var binding : ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transparentStatusBar()
    }

}