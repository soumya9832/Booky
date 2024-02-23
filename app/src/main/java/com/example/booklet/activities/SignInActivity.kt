package com.example.booklet.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.booklet.databinding.ActivitySignInBinding
import com.example.booklet.firebase.Firestore
import com.example.booklet.model.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private var binding : ActivitySignInBinding? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.tvSignUp?.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


        setSupportActionBar(binding?.loginToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding?.loginToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.loginButton!!.setOnClickListener {
            loginUser()
        }

    }

    private fun validFormat(email:String,password:String) : Boolean{
        return when {
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("email should not be empty")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("password should not be empty")
                false
            }
            else->true
        }
    }


    fun userLoginSuccess(user:User)
    {
       startActivity(Intent(this,MainActivity::class.java))
        finish()
    }




    private fun loginUser()
    {
        val email = binding?.etEmail?.text.toString().trim { it<=' ' }
        val password = binding?.etPassword?.text.toString().trim { it<=' ' }
        if(validFormat(email,password))
        {
            showLoadingDialog("Please Wait")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {
                task->
                hideLoadingDialog()
                if(task.isSuccessful) {
                    run {
                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                        Firestore().loadUserData(this)
                    }
                }
                else
                {
                    Toast.makeText(this,task.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }







}