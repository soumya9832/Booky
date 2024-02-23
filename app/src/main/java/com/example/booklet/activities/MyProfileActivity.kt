package com.example.booklet.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.booklet.R
import com.example.booklet.databinding.ActivityMyProfileBinding
import com.example.booklet.firebase.Firestore
import com.example.booklet.model.User
import com.example.booklet.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MyProfileActivity : BaseActivity() {
    private var binding:ActivityMyProfileBinding? = null
    private var selectedImageUri : Uri? = null
    private var profileImageUrl:String = ""
    private lateinit var userDetails : User


    val galleryContract = registerForActivityResult(ActivityResultContracts.GetContent())
    {
        selectedImageUri = it

        Glide.with(this)
            .load(selectedImageUri)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(binding!!.myProfilePhoto)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.myProfileToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding?.myProfileToolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }
        // problem detected

//        Firestore().loadUserData(this)

        binding?.myProfilePhoto!!.setOnClickListener {
            selectProfilePhotoFromGallery()
        }

        binding?.btnUpdate!!.setOnClickListener {
            if(selectedImageUri!=null)
            {
                uploadUserImage()
            }
            else
            {
                updateUserProfileData()
            }

        }

    }



    fun setProfileWithUserData(user:User)
    {
        Glide.with(this)
            .load(user.image)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(binding!!.myProfilePhoto)

        binding?.etProfileName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
    }

//      THIS CODE BLOCK IS ONLY FOR PERMISSION PURPOSES :
     private fun selectProfilePhotoFromGallery()
    {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                   if(report!!.areAllPermissionsGranted()){
                        galleryContract.launch("image/*")
                   }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions()
    {
        AlertDialog.Builder(this).setMessage("it looks like you denied all the permissions" +
                "don't worry you can easily change it at application settings")
            .setPositiveButton("GO TO SETTINGS")
            {
                    _,_ -> try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",packageName,null)
                intent.data = uri
                startActivity(intent)
            }
            catch (e: ActivityNotFoundException)
            {
                e.printStackTrace()
            }
            }
            .setNegativeButton("CANCEL")
            {
                    dialog,which ->
                dialog.dismiss()
            }.show()

    }
//    TILL NOW

    //Storing User Image in Firebase Storage:

    private fun getFileExtension(uri:Uri?):String?
    {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(contentResolver.getType(uri!!))
    }

    private fun uploadUserImage()
    {
        showLoadingDialog("Please Wait")

        if(selectedImageUri!=null)
        {
             val sRef:StorageReference = FirebaseStorage.getInstance().reference.child(
                 "USER_IMAGE" + System.currentTimeMillis() +"." + getFileExtension(selectedImageUri))

             sRef.putFile(selectedImageUri!!).addOnSuccessListener {
                 taskSnapshot->
                 Log.i("Firebase Image Uri",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                     )
                 taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                     uri->
                     Log.i("Downloadable image uri",uri.toString())
                     profileImageUrl = uri.toString()
                    // updateUserProfileData()
                 }
             }.addOnFailureListener {
                 exception->
                 Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
                 hideLoadingDialog()
             }


        }
    }



    //Till Now

   private fun updateUserProfileData()
    {
        val userHashMap = HashMap<String,Any>()

        if(profileImageUrl.isNotEmpty() && profileImageUrl!= userDetails.image){
            userHashMap[Constants.IMAGE] = profileImageUrl
        }
        if(binding?.etProfileName?.text.toString()!=userDetails.name)
        {
            userHashMap[Constants.NAME] = binding?.etProfileName?.text.toString()
        }

            Firestore().updateUserProfileData(this,userHashMap)

    }


    fun profileUpdateSuccess()
    {
        hideLoadingDialog()
    }
}


