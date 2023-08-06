package com.yongdd.covid_map.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkControl {
    @SuppressLint("MissingPermission")
    fun getConnectWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetWork = cm.activeNetwork?:return false
        val actNc = cm.getNetworkCapabilities(activeNetWork) ?: return false
        return actNc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    @SuppressLint("MissingPermission")
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.let {
            val network = it.activeNetwork
            val networkCapabilities = it.getNetworkCapabilities(network)
            return networkCapabilities != null && networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }
}