package com.yongdd.covid_map.utils

import android.app.AlertDialog
import android.content.Context

class ShowAlert(val context: Context) {
    fun twoChoiceAlert(
        title: String,
        message:String,
        positiveText: String,
        negativeText: String,
        positive: () -> Unit,
        negative: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText) { _, _ ->
            positive()
        }
        builder.setNegativeButton(negativeText) { _, _ ->
            negative()
        }

        builder.show()

    }
}