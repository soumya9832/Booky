package com.example.booklet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
//import com.denzcoskun.imageslider.ImageSlider
//import com.denzcoskun.imageslider.models.SlideModel
import com.example.booklet.R
import com.example.booklet.databinding.ActivityMainBinding
import com.example.booklet.firebase.Firestore
import com.example.booklet.model.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    private var binding:ActivityMainBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setActionBar()

        // issue detected here
       // Firestore().loadUserData(this)

        binding?.navView!!.setNavigationItemSelectedListener (this)

        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years."))
        imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that."))


        val imageSlider = findViewById<ImageSlider>(R.id.imageSlider)
        imageSlider.setImageList(imageList)


    }

    private fun setActionBar()
    {
        setSupportActionBar(binding?.appBarMainLayout!!.mainToolbar)
        binding?.appBarMainLayout!!.mainToolbar.setNavigationIcon(R.drawable.ic_baseline_dehaze_24)
        binding?.appBarMainLayout!!.mainToolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
        binding?.appBarMainLayout!!.mainToolbar.setTitle(R.string.app_name)
        binding?.appBarMainLayout!!.mainToolbar.setTitleMargin(10,2,50,2)

    }

    private fun toggleDrawer()
    {
        if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        else
        {
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
            startActivity(Intent(this,MyProfileActivity::class.java))
            }
            R.id.nav_Sign_out->
            {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else->{
                binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }

    fun updateProfileWithUserData(user:User)
    {
        binding?.navView!!.findViewById<TextView>(R.id.tvUsername).text = user.name
    }


}