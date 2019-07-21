package com.mandiri.agentapp.audiovideo.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.mandiri.agentapp.R
import com.mandiri.agentapp.audiovideo.model.Chat
import com.mandiri.agentapp.audiovideo.model.ChatType
import com.mandiri.agentapp.audiovideo.model.Group
import com.mandiri.agentapp.audiovideo.model.User
import com.mandiri.agentapp.audiovideo.other.Util

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_info.*

class ChatInfoActivity : AppCompatActivity() {


    internal var realm: Realm = Realm.getDefaultInstance()
    internal var chat: Chat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_info)

        setSupportActionBar(infoChatToolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val chatId = intent.getStringExtra(CHAT_ID)
        if (chatId == null) {
            finish()
            return
        }


        chat = realm.where(Chat::class.java).equalTo("_id", chatId).findFirst()
        val user = realm.where(User::class.java).equalTo("myself", true).findFirst()


        if (chat?.from ?: false == user.phone) {
            // Outcoming chat
            val outcomingView = layoutInflater.inflate(R.layout.chat_outcoming_view, chatLayout)

            val textView = outcomingView.findViewById<TextView>(R.id.messageTextView)
            textView.text = chat?.content?.text ?: ""
            val timeTextView = outcomingView.findViewById<TextView>(R.id.timeTextView)
            timeTextView.text = Util.formattedDate(chat?.dateTime ?: "")

            val statusImageView = outcomingView.findViewById<ImageView>(R.id.statusImageView)
            if (chat?.type ?: ChatType.GROUP == ChatType.PRIVATE) {
                statusImageView.setImageResource(
                        if (chat?.readTime != null)
                            R.drawable.message_got_read_receipt_from_target
                        else if (chat?.deliveredTime != null)
                            R.drawable.message_got_receipt_from_target
                        else if (chat?.arrivedTime != null)
                            R.drawable.message_got_receipt_from_server
                        else
                            R.drawable.waiting)
            } else if (chat?.type == ChatType.GROUP) {
                val group = realm.where(Group::class.java).equalTo("_id", chat?.to).findFirst()!!
                statusImageView.setImageResource(
                        if (chat?.groupChatReadTimes?.size ?: 0 >= (group.users?.size ?: 0))
                            R.drawable.message_got_read_receipt_from_target
                        else if (chat?.groupChatDeliveredTimes?.size ?: 0 >= group.users?.size ?: 0)
                            R.drawable.message_got_receipt_from_target
                        else if (chat?.arrivedTime != null)
                            R.drawable.message_got_receipt_from_server
                        else
                            R.drawable.waiting)
            }
        } else {
            // Incoming chat
            val incomingView = layoutInflater.inflate(R.layout.chat_incoming_view, chatLayout)

            val textView = incomingView.findViewById<TextView>(R.id.messageTextView)
            textView.text = chat?.content?.text ?: ""
            val timeTextView = incomingView.findViewById<TextView>(R.id.timeTextView)
            timeTextView.text = Util.formattedDate(chat?.dateTime ?: "")
        }

        statusRecyclerView!!.adapter = ChatInfoAdapter()
        statusRecyclerView!!.layoutManager = LinearLayoutManager(this)
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ChatInfoAdapter : RecyclerView.Adapter<ChatInfoAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.chat_info_row, parent, false)


            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (position) {
                0 -> {
                    holder.statusTextView.text = "Read"
                    holder.statusImageView.setImageResource(R.drawable.message_got_read_receipt_from_target)
                    holder.timeTextView.text = Util.formattedDate(chat?.readTime ?: "")
                }
                1 -> {
                    holder.statusTextView.text = "Delivered"
                    holder.statusImageView.setImageResource(R.drawable.message_got_receipt_from_target)
                    holder.timeTextView.text = Util.formattedDate(chat?.deliveredTime ?: "")
                }
            }
        }

        override fun getItemCount(): Int {
            return 2
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var statusTextView: TextView
            var timeTextView: TextView
            var statusImageView: ImageView

            init {

                statusTextView = itemView.findViewById(R.id.statusTextView)
                timeTextView = itemView.findViewById(R.id.timeTextView)
                statusImageView = itemView.findViewById(R.id.statusImageView)
            }
        }
    }

    companion object {
        val CHAT_ID = "chatId"
    }
}
