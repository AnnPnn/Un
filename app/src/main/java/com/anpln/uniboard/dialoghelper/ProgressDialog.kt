package com.anpln.uniboard.dialoghelper

import android.app.Activity
import android.app.AlertDialog
import com.anpln.uniboard.databinding.ProgressDialogLayoteBinding
import com.anpln.uniboard.databinding.SignDialogBinding

object ProgressDialog {
    fun createProgressDialog(act: Activity): AlertDialog {
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = ProgressDialogLayoteBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCancelable(false)
        //возможность остановки диалога только напрямую из кода
        // чтобы дождаться полной загрузки изображения
        dialog.show()
        return dialog
    }
}//класс для отображения загрузки изображения