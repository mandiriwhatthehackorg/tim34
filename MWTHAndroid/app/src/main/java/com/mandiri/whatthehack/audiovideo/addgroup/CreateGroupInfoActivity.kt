package com.mandiri.whatthehack.audiovideo.addgroup

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.api.API
import com.mandiri.whatthehack.audiovideo.api.ResponseHandler
import com.mandiri.whatthehack.audiovideo.model.GroupResponse
import com.mandiri.whatthehack.audiovideo.model.User
import com.mandiri.whatthehack.audiovideo.other.Util

import java.util.ArrayList

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_group_info.*

class CreateGroupInfoActivity : AppCompatActivity() {


    lateinit internal var realm: Realm
    internal var selectedParticipants: List<User> = ArrayList()
    internal var selectedUserIds: Array<String>? = null
    lateinit internal var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group_info)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("")

        selectedUserIds = intent.getStringArrayExtra(SELECTED_USER_ID)
        if (selectedUserIds == null) {
            finish()
        }

        realm = Realm.getDefaultInstance()

        selectedParticipants = realm.where(User::class.java).`in`("phone", selectedUserIds).findAll()
        participantRecyclerView!!.adapter = ParticipantAdapter()
        participantRecyclerView!!.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)

        groupImageButton.setOnClickListener { selectGroupImage() }
        okButton.setOnClickListener { ok() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    internal fun selectGroupImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                Util.showConfirmationWithTitle(
                    "We need you to confirm camera permission",
                    this,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        ActivityCompat.requestPermissions(
                            this@CreateGroupInfoActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION
                        )

                    }
                )
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            imageBitmap = (extras!!.get("data") as Bitmap?)!!
            groupImageButton!!.setImageBitmap(imageBitmap)
        }
    }


    internal fun ok() {
        val subject = groupSubjectEditText!!.text.toString()
        if (TextUtils.isEmpty(subject)) {
            Toast.makeText(this, "Please fill the subject", Toast.LENGTH_SHORT).show()
            return
        }


        val dialog = Util.showProgressDialogWithTitle("Processing...", this)
        API.postCreateGroup(subject, imageBitmap, selectedUserIds, object : ResponseHandler<GroupResponse> {
            override fun response(success: Boolean, data: GroupResponse?, errorMessage: String?) {
                if (success) {
                    realm.executeTransaction { realm ->
                        var mySelf: User? = realm.where(User::class.java).equalTo("myself", true).findFirst()
                        if (mySelf != null) {
                            mySelf = realm.copyFromRealm(mySelf)
                        }
                        realm.insertOrUpdate(data?.group!!)
                        realm.insertOrUpdate(data?.notifChat!!)
                        assert(mySelf != null)
                        realm.insertOrUpdate(mySelf!!)
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@CreateGroupInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

        })
    }

    private inner class ParticipantAdapter : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.selected_participant_row, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return selectedParticipants.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    companion object {

        val SELECTED_USER_ID = "selectedUserId"

        val CAMERA_PERMISSION = 1
        private val REQUEST_IMAGE_CAPTURE = 100
    }

}
