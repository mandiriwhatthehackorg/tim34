package com.mandiri.whatthehack.audiovideo.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.api.API
import com.mandiri.whatthehack.audiovideo.api.ResponseHandler
import com.mandiri.whatthehack.audiovideo.model.User

import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_user_list.*

class UserListFragment : Fragment() {

    private var mListener: OnUserListListener? = null
    internal var users: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnUserListListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnUserListListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        users = mListener!!.realm.where(User::class.java).equalTo("myself", false).findAll()
        recyclerView!!.adapter = UserListAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(activity)

    }

    override fun onResume() {
        super.onResume()

        API.getUsers(object : ResponseHandler<List<User>> {
            override fun response(success: Boolean, data: List<User>?, errorMessage: String?) {
                if (!success) return
                mListener!!.realm.executeTransaction { realm ->
                    realm.insertOrUpdate(data)
                    users = data!!
                    recyclerView!!.adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private inner class UserListAdapter : RecyclerView.Adapter<UserListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_row, parent, false)

            return UserListViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
            holder.textView.text = users[position].phone
            holder.itemView.setOnClickListener { mListener!!.onClickUser(users[position].phone) }
        }

        override fun getItemCount(): Int {
            return users.size
        }
    }

    private inner class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textView: TextView

        init {
            textView = itemView.findViewById(R.id.textView)
        }
    }

    interface OnUserListListener {
        val realm: Realm
        fun onClickUser(userId: String?)
    }
}// Required empty public constructor
