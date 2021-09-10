package my.edu.tarc.okuappg11.progressdialog

import android.app.AlertDialog
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.SignInFragment
import my.edu.tarc.okuappg11.activities.AddEventActivity

class AddEventDialog(val fragment: AddEventActivity) {
    private lateinit var isDialog: AlertDialog
    fun startLoading(){
        //Set View
        val inflater = fragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.signin_dialog, null)
        //Set Dialog
        val builder = AlertDialog.Builder(fragment)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
        //isDialog.setCanceledOnTouchOutside(true)
        isDialog.window?.setLayout(990,600) //Controlling width and height.
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}