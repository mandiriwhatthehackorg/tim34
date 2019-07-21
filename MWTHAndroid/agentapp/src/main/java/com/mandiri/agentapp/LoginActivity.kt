package com.mandiri.agentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mandiri.agentapp.audiovideo.AudioCallActivity
import com.mandiri.agentapp.audiovideo.LandingForVideoCallActivity
import com.mandiri.agentapp.audiovideo.registration.RegistrationActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            startActivity(Intent(this, AudioCallActivity::class.java))
        }
    }
}
