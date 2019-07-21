package com.mandiri.whatthehack.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mandiri.whatthehack.R
import kotlinx.android.synthetic.main.activity_card_detail.*

class CardDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        lostYourCard.setOnClickListener {

        }
    }
}
