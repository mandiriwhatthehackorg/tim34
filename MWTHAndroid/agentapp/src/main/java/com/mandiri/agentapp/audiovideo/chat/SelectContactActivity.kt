package com.mandiri.agentapp.audiovideo.chat

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.mandiri.agentapp.R
import com.mandiri.agentapp.audiovideo.api.API
import com.mandiri.agentapp.audiovideo.api.ResponseHandler
import com.mandiri.agentapp.audiovideo.model.User
import com.mandiri.agentapp.audiovideo.other.RecyclerItemClickListener
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

import com.mandiri.agentapp.audiovideo.api.API.getUsers
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_select_contact.*

class SelectContactActivity : AppCompatActivity() {


    lateinit internal var realm: Realm
    lateinit internal var users: RealmResults<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contact)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setTitle("Select Contact")
            actionBar.setSubtitle("55 Contacts")
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        realm = Realm.getDefaultInstance()

        getUsers(object : ResponseHandler<List<User>> {
            override fun response(success: Boolean, data: List<User>?, errorMessage: String?) {
                if (!success) return
                realm.executeTransaction { realm ->
                    realm.insertOrUpdate(data)

                    users = realm.where(User::class.java).notEqualTo("myself", true).findAll()
                    recyclerView!!.adapter!!.notifyDataSetChanged()
                }
            }
        })

        users = realm.where(User::class.java).notEqualTo("myself", true).findAll()

        recyclerView!!.adapter = ContactAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (position == 0) {

                        } else {
                            val intent = Intent(this@SelectContactActivity, ChatActivity::class.java)
                            intent.putExtra(ChatActivity.USER_ID, users[position - 1].phone)

                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onLongItemClick(view: View?, position: Int) {

                    }
                })
        )
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
            }
            R.id.action_new_contact -> {
            }
            R.id.action_invite_friend -> {
            }
            R.id.action_contacts -> {
            }
            R.id.action_refresh -> {
            }
            R.id.action_help -> {
            }
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.select_contact_menu, menu)
        return true
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    private inner class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_row, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == 0) {
                holder.userNameTextView.text = "New Group"
                return
            }
            val user = users[position - 1]
            holder.userNameTextView.text = user.name + " " + user.phone
            holder.userStatusTextView.text = user.status

            user.profilePictUrl?.let {

                ImageLoader.getInstance()
                    .displayImage(it, holder.userImageView, object : ImageLoadingListener {
                        override fun onLoadingStarted(s: String, view: View) {

                        }

                        override fun onLoadingFailed(s: String, view: View, failReason: FailReason) {
                            holder.userImageView.setImageResource(R.drawable.profile)
                        }

                        override fun onLoadingComplete(s: String, view: View, bitmap: Bitmap?) {

                        }

                        override fun onLoadingCancelled(s: String, view: View) {
                            holder.userImageView.setImageResource(R.drawable.profile)
                        }
                    })
            }
        }

        override fun getItemCount(): Int {
            return users.size + 1
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var userNumberTypeTextView: TextView
            var userNameTextView: TextView
            var userStatusTextView: TextView
            var userImageView: ImageView

            init {
                userNumberTypeTextView = itemView.findViewById(R.id.userNumberTypeTextView)
                userNameTextView = itemView.findViewById(R.id.userNameTextView)
                userStatusTextView = itemView.findViewById(R.id.userStatusTextView)
                userImageView = itemView.findViewById(R.id.userImageView)
            }
        }
    }
}
