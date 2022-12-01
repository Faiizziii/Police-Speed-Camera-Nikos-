package com.example.policespeedcamera

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ProgressBar

class AdLoadingDialog(private var context: Context){

    private var dialog: Dialog? = null

    fun showDialog() {
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_layout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val progressBar = dialog!!.findViewById(R.id.progresssBar) as ProgressBar

        progressBar.progressTintList = ColorStateList.valueOf(Color.WHITE)

        dialog?.show()

    }

    fun dismissDialog(){
        if (dialog!=null && dialog?.isShowing!!){
            dialog?.dismiss()
        }
    }
}