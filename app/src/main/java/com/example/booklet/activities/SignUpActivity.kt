package com.example.booklet.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.booklet.databinding.ActivitySignUpBinding
import com.example.booklet.firebase.Firestore
import com.example.booklet.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding : ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.tvSignin?.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding?.signupButton?.setOnClickListener{
            signUpUser()
        }




    }

    private fun validFormat(name:String,email:String,password:String) : Boolean{
        return when {
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("pleas enter the name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("pleas enter the email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("pleas enter the password")
                false
            }
            else->true
        }
    }

    fun userRegisterSuccess()
    {
        hideLoadingDialog()
        startActivity(Intent(this,MainActivity::class.java))
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun signUpUser()
    {
        val name = binding?.etName?.text.toString().trim{it<=' '}
        val email = binding?.etEmail?.text.toString().trim { it<= ' ' }
        val password = binding?.etPassword?.text.toString().trim { it<= ' ' }
        if(validFormat(name,email,password))
        {
            showLoadingDialog("Loading")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                task->
                run {
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(firebaseUser.uid,name,firebaseUser.email!!)
                        Firestore().registerUser(this,user)
                    }
                }

            }
        }
    }



}