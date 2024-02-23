package com.example.booklet.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.booklet.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    private lateinit var loadingDialog : Dialog
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)


    }

    fun showLoadingDialog(text:String)
    {
        loadingDialog= Dialog(this)

        loadingDialog.setContentView(R.layout.dialog_progress)

        loadingDialog.show()

    }

    fun hideLoadingDialog()
    {
        loadingDialog.dismiss()
    }

    fun getCurrentUserId() : String
    {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

   fun doubleBackToExit()
    {
        if(doubleBackToExitPressedOnce)
        {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,"please double click to exit",Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed({doubleBackToExitPressedOnce=false},2000)
    }

    fun showErrorSnackBar(message:String)
    {
        val snackbar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        snackbar.show()
    }





}