package com.yongdd.covid_map.utils

sealed class SendToView {
    object Loading : SendToView()
    object LoadingOff : SendToView()
    object Finish : SendToView()
    data class SendData(val dataName : String, val data : Any?) : SendToView()
}
