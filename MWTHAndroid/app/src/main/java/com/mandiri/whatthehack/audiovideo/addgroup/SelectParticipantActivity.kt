package com.mandiri.whatthehack.audiovideo.addgroup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.model.User
import com.mandiri.whatthehack.audiovideo.other.RecyclerItemClickListener

import java.util.ArrayList

import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_select_participant.*

class SelectParticipantActivity : AppCompatActivity() {


    private var users: RealmResults<User>? = null
    private val selectedUsers = ArrayList<User>()
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_participant)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        searchButton.setOnClickListener { search() }
        searchBackButton.setOnClickListener { backFromSearch() }
        nextButton.setOnClickListener { next() }

        realm = Realm.getDefaultInstance()

        users = realm!!.where(User::class.java).equalTo("myself", false).findAll()

        contactRecyclerView.setAdapter(SelectionAdapter())
        contactRecyclerView.setLayoutManager(LinearLayoutManager(this))
        contactRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                contactRecyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        contactRecyclerView.getAdapter()?.notifyItemChanged(position)
                        val selectedUserIndex = selectedUsers.indexOf(users!![position])
                        if (selectedUserIndex >= 0) {
                            selectedUsers.removeAt(selectedUserIndex)
                            selectedRecyclerView.getAdapter()?.notifyItemRemoved(selectedUserIndex)
                        } else {
                            selectedUsers.add(users!![position])
                            selectedRecyclerView.getAdapter()?.notifyItemInserted(selectedUsers.size - 1)
                        }
                        selectionStatusTextView.setText(selectedUsers.size.toString() + " of " + users!!.size)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {

                    }
                })
        )

        selectedRecyclerView.setAdapter(SelectedAdapter())
        selectedRecyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        selectedRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                selectedRecyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        selectedUsers.removeAt(position)
                        selectedRecyclerView.getAdapter()?.notifyItemRemoved(position)
                        selectionStatusTextView.setText(selectedUsers.size.toString() + " of " + users!!.size)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {

                    }
                })
        )

        selectionStatusTextView.setText("Add Participants")

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                val inputted = editable.toString()
                users = realm!!.where(User::class.java).contains("name", inputted, Case.INSENSITIVE).or()
                    .contains("phone", inputted, Case.INSENSITIVE).findAll()
                contactRecyclerView.getAdapter()?.notifyDataSetChanged()

            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        realm!!.close()
    }


    internal fun search() {
        searchLayout.setVisibility(View.VISIBLE)
    }

    internal fun backFromSearch() {
        searchLayout.setVisibility(View.GONE)
    }

    internal operator fun next() {
        if (selectedUsers.size == 0) {
            Toast.makeText(this, "At least 1 contact must be selected", Toast.LENGTH_SHORT).show()
            return
        }

        val userIds = arrayOfNulls<String>(selectedUsers.size)
        for (i in selectedUsers.indices) {
            userIds[i] = selectedUsers[i].phone
        }

        val intent = Intent(this, CreateGroupInfoActivity::class.java)
        intent.putExtra(CreateGroupInfoActivity.SELECTED_USER_ID, userIds)
        startActivityForResult(intent, GROUP_CREATION_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GROUP_CREATION_REQUEST && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    private inner class SelectionAdapter : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.selection_participant_row, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.userNameTextView.text = users!![position].phone
            holder.selectionStatusImageVieqw.visibility =
                if (selectedUsers.indexOf(users!![position]) >= 0) View.VISIBLE else View.GONE
        }

        override fun getItemCount(): Int {
            return users!!.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var userNameTextView: TextView
            var userStatusTextView: TextView
            var userImageView: ImageView
            var selectionStatusImageVieqw: ImageView

            init {
                userNameTextView = itemView.findViewById(R.id.userNameTextView)
                userStatusTextView = itemView.findViewById(R.id.userStatusTextView)
                userImageView = itemView.findViewById(R.id.userImageView)
                selectionStatusImageVieqw = itemView.findViewById(R.id.selectionStatusImageVieqw)
            }
        }
    }

    private inner class SelectedAdapter : RecyclerView.Adapter<SelectedAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.selected_participant_row, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.userNameTextView.text = selectedUsers[position].phone
        }

        override fun getItemCount(): Int {
            return selectedUsers.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var userImageView: ImageView
            var userClearImageView: ImageView
            var userNameTextView: TextView

            init {

                userImageView = itemView.findViewById(R.id.userImageView)
                userClearImageView = itemView.findViewById(R.id.userClearImageView)
                userNameTextView = itemView.findViewById(R.id.userNameTextView)
            }
        }
    }

    companion object {

        private val GROUP_CREATION_REQUEST = 100
    }
}
