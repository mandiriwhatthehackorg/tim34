package com.mandiri.whatthehack.audiovideo.registration

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.api.API
import com.mandiri.whatthehack.audiovideo.api.ResponseHandler
import com.mandiri.whatthehack.audiovideo.main.MainActivity
import com.mandiri.whatthehack.audiovideo.model.User

import java.util.ArrayList

import io.realm.Realm

class InitializationActivity : AppCompatActivity() {


    internal var contacts: ArrayList<Contact>? = null
    internal var name: String? = null
    internal var phonenumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initialization)


        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@InitializationActivity,
                Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(this@InitializationActivity, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show()
            GetContactsIntoArrayList()
            sendContactToGetAllUser()

        } else {

            ActivityCompat.requestPermissions(this@InitializationActivity, arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_PERMISSION)

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CONTACT_PERMISSION ->


                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetContactsIntoArrayList()
                    sendContactToGetAllUser()
                } else {


                }
        }
    }

    fun GetContactsIntoArrayList() {

        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)!!

        contacts = ArrayList()

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            contacts!!.add(Contact(name, phonenumber))
        }

        cursor.close()
    }

    fun sendContactToGetAllUser() {
        if (contacts != null && contacts!!.size > 0) {
            val phones = ArrayList<String>()

            for (contact in contacts!!) {
                contact.phone?.let { phones.add(it) }
            }
            API.getAllUsersWithContactPhones(phones, object : ResponseHandler<List<User>> {
                override fun response(success: Boolean, data: List<User>?, errorMessage: String?) {
                    if (success) {
                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction { realm -> realm.insertOrUpdate(data) }
                        realm.close()
                    }
                    startActivity(Intent(this@InitializationActivity, MainActivity::class.java))
                    finish()
                }
            })
        } else {
            startActivity(Intent(this@InitializationActivity, MainActivity::class.java))
            finish()
        }
    }

    inner class Contact internal constructor(internal var name: String?, internal var phone: String?)

    companion object {

        val CONTACT_PERMISSION = 2
    }
}
