package com.example.booklet.firebase

import android.app.Activity
import android.widget.Toast
import com.example.booklet.activities.*
import com.example.booklet.model.User
import com.example.booklet.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Firestore  {

    private val myFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity,userInfo:User){
        myFirestore.collection(Constants.USERS).document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnCompleteListener {
                activity.userRegisterSuccess()
            }.addOnFailureListener {
                Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
            }

    }

    fun updateUserProfileData(activity:MyProfileActivity,userHashMap: HashMap<String,Any>){
        myFirestore.collection(Constants.USERS).document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity,"Profile Updated Successfully",Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                Toast.makeText(activity,it.message,Toast.LENGTH_LONG).show()
            }
    }

    fun loadUserData(activity: Activity){
        myFirestore.collection(Constants.USERS).document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document->
                val loggedInUser = document.toObject(User::class.java)!!
                when(activity)
                {
                    is SignInActivity->{
                        activity.userLoginSuccess(loggedInUser)
                    }
                    is MainActivity->{
                        activity.updateProfileWithUserData(loggedInUser)
                    }
                    is MyProfileActivity->{
                        activity.setProfileWithUserData(loggedInUser)
                    }
                }

            }.addOnFailureListener {
                it->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideLoadingDialog()
                    }
                    is MainActivity -> {
                        activity.hideLoadingDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideLoadingDialog()
                        Toast.makeText(activity,"problem",Toast.LENGTH_LONG).show()
                    }
                }
                Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
            }
    }


    fun getCurrentUserId():String
    {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser!=null)
        {
             currentUserId = currentUser.uid
        }
        return currentUserId
    }

}