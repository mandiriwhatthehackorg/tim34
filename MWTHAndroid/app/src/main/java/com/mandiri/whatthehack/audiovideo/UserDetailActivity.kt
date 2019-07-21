package com.mandiri.whatthehack.audiovideo

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.mandiri.whatthehack.R

import com.mandiri.whatthehack.audiovideo.model.User

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : AppCompatActivity() {


    private var user: User? = null
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        setSupportActionBar(toolbar)

        realm = Realm.getDefaultInstance()

        val userId = intent.getStringExtra(USER_ID)
        user = realm!!.where(User::class.java).equalTo("phone", userId).findFirst()

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(user!!.name)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {

        val USER_ID = "userId"
    }
}
