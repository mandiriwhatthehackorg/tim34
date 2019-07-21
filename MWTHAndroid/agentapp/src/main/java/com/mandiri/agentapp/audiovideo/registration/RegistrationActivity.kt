package com.mandiri.agentapp.audiovideo.registration

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.mandiri.agentapp.audiovideo.main.MainActivity
import com.mandiri.agentapp.R
import com.mandiri.agentapp.audiovideo.model.Chat
import com.mandiri.agentapp.audiovideo.model.RegisterResponse
import com.mandiri.agentapp.audiovideo.model.Token
import com.mandiri.agentapp.audiovideo.model.User

import io.realm.Realm
import io.realm.RealmResults

class RegistrationActivity : AppCompatActivity(), RegisterPhoneFragment.OnRegisterPhoneListener, VerifyCodeFragment.OnVerifyListener {

    internal var realm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Remove all active notification
        val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()

        if (realm.where(Token::class.java).findAll().size > 0 && realm.where(User::class.java).equalTo("myself", true).isNotNull("name").count() > 0) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, RegisterPhoneFragment.getInstance(this)).commit()

    }

    override fun onRegister(response: RegisterResponse) {
        realm.executeTransaction { realm ->
            if (response.token != null) {
                val token = Token()
                token.token = response.token
                realm.insertOrUpdate(token)
            }

            if (response.user != null) {
                val user = User()
                user.phone = response.user?.phone
                user.myself = true
                realm.insertOrUpdate(user)
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.container, VerifyCodeFragment.getInstance(this)).commit()
    }

    override fun onVerifyResponse(response: User?) {
        realm.executeTransaction { realm ->
            if (response != null) {
                val user = User()
                user.phone = response.phone
                user.myself = true
                realm.insertOrUpdate(user)
            }
        }
        startActivity(Intent(this, ProfileRegistrationActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}
