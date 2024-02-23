package com.example.booklet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.booklet.databinding.ActivityWelcomeBinding
import com.example.booklet.firebase.Firestore

class WelcomeActivity : AppCompatActivity() {
    private var binding : ActivityWelcomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        splashScreen.setKeepOnScreenCondition { false }

        binding?.btnNext?.setOnClickListener {
            var currentUserId = Firestore().getCurrentUserId()
            if(currentUserId.isNotEmpty())
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
           else{
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }
}