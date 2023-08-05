package com.yongdd.covid_map.utils

sealed class ShowAlert {
    data class ShowToast(val message: String) : ShowAlert()
    data class ShowSnackBar(val message: String) : ShowAlert()
    data class ShowBasicDialog(val title:String, val message: String?,val ok : () -> Unit) : ShowAlert()
}
