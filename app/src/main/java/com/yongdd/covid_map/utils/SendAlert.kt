package com.yongdd.covid_map.utils

sealed class SendAlert {
    data class ShowToast(val message: String) : SendAlert()
    data class ShowSnackBar(val message: String) : SendAlert()
    data class ShowTwoChoiceAlert(
        val title: String,
        val message: String,
        val positiveText: String,
        val negativeText: String,
        val positive: () -> Unit,
        val negative: () -> Unit
    ) : SendAlert()

    data class ShowOneChoiceAlert(
        val title: String,
        val message: String,
        val positiveText: String,
        val positive: () -> Unit
    ) : SendAlert()
}