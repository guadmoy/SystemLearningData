package com.guadmoy.systemlearning.dialoghelper

import android.app.Activity
import android.app.AlertDialog
import com.guadmoy.systemlearning.databinding.ProgressDialogBinding


object ProgressDialog {

    fun createProgressDialog(act:Activity):AlertDialog{
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = ProgressDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        val dialog = builder.create()

        dialog.setCancelable(false)
        dialog.show()

        return dialog
    }
}