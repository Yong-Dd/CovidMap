package com.yongdd.covid_map.utils

import android.content.Context
import kotlin.math.roundToInt

object DisplayUtils {

    fun dpToPx(context: Context, dp:Int):Int{
        val density = context.resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }
}