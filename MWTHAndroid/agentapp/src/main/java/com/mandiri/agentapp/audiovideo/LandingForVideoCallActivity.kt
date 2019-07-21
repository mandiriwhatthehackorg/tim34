package com.mandiri.agentapp.audiovideo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LandingForVideoCallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, AudioCallActivity::class.java))
    }
}
