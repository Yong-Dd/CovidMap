package com.yongdd.covid_map.view

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.yongdd.covid_map.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity()  {

    private lateinit var binding : ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}