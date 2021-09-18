package my.edu.tarc.okuappg11.progressdialog

import android.app.AlertDialog
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.ProfileSettingActivity

class SavingDialog(val activity: ProfileSettingActivity){

    private lateinit var isDialog: AlertDialog
    fun startLoading(){
        //Set View
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.saving_dialog, null)
        //Set Dialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
        //isDialog.setCanceledOnTouchOutside(true)
        isDialog.window?.setLayout(700,550) //Controlling width and height.
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}