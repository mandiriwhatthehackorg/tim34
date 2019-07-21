package com.mandiri.whatthehack.audiovideo.registration

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.api.API
import com.mandiri.whatthehack.audiovideo.api.ResponseHandler
import com.mandiri.whatthehack.audiovideo.model.User
import com.mandiri.whatthehack.audiovideo.other.Util

import java.util.ArrayList

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_profile_registration.*

class ProfileRegistrationActivity : AppCompatActivity() {

    internal var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_registration)

        userImageButton.setOnClickListener { changeUserImage() }
        nextButton.setOnClickListener { next() }
    }

    internal fun changeUserImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                Util.showConfirmationWithTitle("We need you to confirm camera permission", this, DialogInterface.OnClickListener { dialogInterface, i -> ActivityCompat.requestPermissions(this@ProfileRegistrationActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION) })
            } else {
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            imageBitmap = extras!!.get("data") as Bitmap
            userImageButton!!.setImageBitmap(imageBitmap)
        }
    }
    
    internal operator fun next() {

        val name = userNameEditText!!.text.toString()
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please Input your name", Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = Util.showProgressDialogWithTitle("Processing...", this)
        API.postProfile(name, imageBitmap, object : ResponseHandler<User> {
            override fun response(success: Boolean, data: User?, errorMessage: String?) {
                if (success) {
                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction { realm ->
                        data?.myself = true
                        realm.insertOrUpdate(data)
                    }
                    realm.close()
                }
                dialog.dismiss()
                startActivity(Intent(this@ProfileRegistrationActivity, InitializationActivity::class.java))
                finish()
            }

        })
    }

    companion object {
        val CAMERA_PERMISSION = 1
        private val REQUEST_IMAGE_CAPTURE = 100
    }

}
