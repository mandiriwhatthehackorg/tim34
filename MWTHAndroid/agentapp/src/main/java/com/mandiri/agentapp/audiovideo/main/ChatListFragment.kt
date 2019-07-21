package com.mandiri.agentapp.audiovideo.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.mandiri.agentapp.R
import com.mandiri.agentapp.audiovideo.SocketEvent
import com.mandiri.agentapp.audiovideo.model.Chat
import com.mandiri.agentapp.audiovideo.model.ChatStatus
import com.mandiri.agentapp.audiovideo.model.ChatType
import com.mandiri.agentapp.audiovideo.model.Group
import com.mandiri.agentapp.audiovideo.model.StarredChat
import com.mandiri.agentapp.audiovideo.model.User
import com.mandiri.agentapp.audiovideo.model.UserTypingStatus
import com.mandiri.agentapp.audiovideo.other.ChatApplication
import com.mandiri.agentapp.audiovideo.other.RecyclerItemClickListener
import com.mandiri.agentapp.audiovideo.other.Util
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.otto.Subscribe

import java.util.ArrayList
import java.util.Date
import java.util.HashMap

import com.mandiri.agentapp.audiovideo.main.ChatListFragment.ChatListAdapter.ViewHolder
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_chat_list.*

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnChatListListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ChatListFragment : Fragment() {

    private var mListener: OnChatListListener? = null
    private var user: User? = null

    internal var chats: List<Chat> = ArrayList()
    internal var userTypingStatuses: MutableList<UserTypingStatus> = ArrayList()


    internal var contextMenu: Menu? = null
    internal var actionMode: ActionMode? = null
    private var isMultiSelect = false
    private var multiSelectList = ArrayList<Chat>()
    private val mActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.chat_list_option_menu, menu)
            contextMenu = menu
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_pin -> {
                }
                R.id.action_delete -> {
                }
                R.id.action_mute -> {
                }
                R.id.action_archive -> {
                }
                R.id.action_add_shortcut -> {
                }
                R.id.action_view_contact -> {
                }
                R.id.action_mark_as_unread -> {
                }
                else -> finishActionModeMultiSelect()
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            finishActionModeMultiSelect()
        }
    }


    // Add/Remove the item from/to the list

    fun multiSelectRowAt(position: Int) {
        if (actionMode != null) {
            if (multiSelectList.contains(chats[position]))
                multiSelectList.remove(chats[position])
            else
                multiSelectList.add(chats[position])

            if (multiSelectList.size > 0)
                actionMode!!.title = "" + multiSelectList.size
            else {
                finishActionModeMultiSelect()
            }

            recyclerView!!.adapter?.notifyDataSetChanged()
        }
    }

    private fun finishActionModeMultiSelect() {
        multiSelectList = ArrayList()
        recyclerView!!.adapter?.notifyDataSetChanged()
        isMultiSelect = false
        actionMode!!.finish()
        actionMode = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnChatListListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnChatListListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = mListener!!.realm.where(User::class.java).equalTo("myself", true).findFirst()

        val groups = mListener!!.realm.where(Group::class.java).findAll()
        recyclerView!!.adapter = ChatListAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.addOnItemTouchListener(RecyclerItemClickListener(activity!!, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {

                if (isMultiSelect) {
                    multiSelectRowAt(position)
                } else {
                    mListener!!.onClickChat(chats[position]._id ?:"")
                }
            }

            override fun onLongItemClick(view: View?, position: Int) {

                if (!isMultiSelect) {
                    multiSelectList = ArrayList()
                    isMultiSelect = true
                    if (actionMode == null) {
                        actionMode = (activity as AppCompatActivity).startSupportActionMode(mActionModeCallback)
                    }
                }
                multiSelectRowAt(position)
            }
        }))

    }

    override fun onResume() {
        super.onResume()

        ChatApplication.bus.register(this)
        getAllLastChat()
        recyclerView!!.adapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        ChatApplication.bus.unregister(this)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    internal fun getAllLastChat() {

        // Get all chat
        // Distinct by sender then put it in array to avoid matching sender or receiver
        val realmResults = mListener!!.realm.where(Chat::class.java).findAllSorted("dateTime", Sort.DESCENDING).where().distinct("to")
        val chatsMap = HashMap<String, Chat>()

        val tempChats = ArrayList<Chat>()
        userTypingStatuses = ArrayList()
        for (chat in realmResults) {
            if (chat.to == user!!.phone && !chatsMap.containsKey(chat.from)) {
                chatsMap.put(chat?.from ?: "", chat)
                tempChats.add(chat)
                userTypingStatuses.add(UserTypingStatus(false, chat?.from?:""))
            } else if (chat.from == user!!.phone && !chatsMap.containsKey(chat.to)) {
                chatsMap.put(chat?.to ?: "", chat)
                tempChats.add(chat)
                userTypingStatuses.add(UserTypingStatus(false, chat?.to?:""))
            } else if (chat.type == ChatType.GROUP && !chatsMap.containsKey(chat.to)) {

                // Group chat
                chatsMap.put(chat?.to ?: "", chat)
                tempChats.add(chat)
                userTypingStatuses.add(UserTypingStatus(false, chat.to?: ""))
            }
        }

        chats = tempChats
        val chatArrayList = mListener!!.realm.copyFromRealm(chats) as ArrayList<Chat>
        Log.i("INFO", "Load Chat")
    }

    @Subscribe
    fun receiveChatFromSocket(chat: Chat) {

        // If get notification of a new chat
        getAllLastChat()
        recyclerView!!.adapter?.notifyDataSetChanged()
    }

    @Subscribe
    fun receiveChatStatusFromSocket(chatStatus: ChatStatus) {

        // Only get notification to update chat
        recyclerView!!.adapter?.notifyDataSetChanged()
    }


    @Subscribe
    fun receiveUserTypingStatusFromSocket(status: UserTypingStatus) {
        val index = userTypingStatuses.indexOf(status)
        if (index >= 0) {
            userTypingStatuses[index].isTyping = status.isTyping
            recyclerView!!.adapter?.notifyItemChanged(index)
        }
    }


    interface OnChatListListener {
        val realm: Realm
        fun onClickChat(chatId: String)
    }

    private inner class ChatListAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_chat_row, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val chat = chats[position]
            try {
                holder.messageTextView.text = chat.content?.text
                // When showing row of this user chat
                // Set checkmark status according to chat status
                if (chat.type == ChatType.PRIVATE) {


                    holder.userTextView.text = if (chat.to == user!!.phone) chat.from else chat.to
                    if (chat.from == user!!.phone) {
                        holder.statusImageView.visibility = View.VISIBLE
                        holder.statusImageView.setImageResource(
                                if (chat.readTime != null)
                                    R.drawable.message_got_read_receipt_from_target
                                else if (chat.deliveredTime != null)
                                    R.drawable.message_got_receipt_from_target
                                else if (chat.arrivedTime != null)
                                    R.drawable.message_got_receipt_from_server
                                else
                                    R.drawable.waiting)
                        holder.incomingCountTextView.text = ""
                    } else {
                        holder.statusImageView.visibility = View.GONE
                        val unReadIncomingChatCount = mListener!!.realm.where(Chat::class.java).equalTo("to", user!!.phone).isNull("readTime").count()

                        holder.incomingCountTextView.text = if (unReadIncomingChatCount > 0) unReadIncomingChatCount.toString() + "" else ""
                    }
                    holder.fromUserTextView.visibility = View.GONE


                } else if (chat.type == ChatType.GROUP) {

                    val group = mListener!!.realm.where(Group::class.java).equalTo("_id", chat.to).findFirst()
                    if (group != null) {
                        holder.userTextView.text = group.subject
                        if (chat.from == user!!.phone) {
                            holder.statusImageView.visibility = View.VISIBLE
                            holder.statusImageView.setImageResource(
                                    if (chat.groupChatReadTimes?.size ?: 0 >= group.users?.size ?: 0)
                                        R.drawable.message_got_read_receipt_from_target
                                    else if (chat.groupChatDeliveredTimes?.size ?: 0 >= group.users?.size ?: 0)
                                        R.drawable.message_got_receipt_from_target
                                    else if (chat.arrivedTime != null)
                                        R.drawable.message_got_receipt_from_server
                                    else
                                        R.drawable.waiting)
                            holder.incomingCountTextView.text = ""
                        } else {
                            holder.statusImageView.visibility = View.GONE
                            val unReadIncomingChatCount = mListener!!.realm.where(Chat::class.java).equalTo("to", group._id).notEqualTo("from", user!!.phone).isNull("readTime").findAll().size.toLong()

                            holder.incomingCountTextView.text = if (unReadIncomingChatCount > 0) unReadIncomingChatCount.toString() + "" else ""
                        }
                    }
                    holder.fromUserTextView.visibility = View.VISIBLE
                    holder.fromUserTextView.text = if (chat.from == user!!.phone) "You:" else chat.from + ":"

                }

                if (userTypingStatuses[position] != null && userTypingStatuses[position].isTyping) {
                    holder.userStatusTextView.text = "is typing..."
                    holder.userStatusTextView.visibility = View.VISIBLE
                    holder.chatStatusLayout.visibility = View.GONE
                } else {
                    holder.userStatusTextView.visibility = View.GONE
                    holder.userStatusTextView.text = ""
                    holder.chatStatusLayout.visibility = View.VISIBLE
                }


                if (multiSelectList.contains(chat)) {
                    holder.innerLayout.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                } else {
                    holder.innerLayout.setBackgroundColor(resources.getColor(android.R.color.transparent))
                }

                holder.timeTextView.text = Util.utcToLocalformattedDate(chat.dateTime ?: "", Util.defaultFormattedDate, "HH:mm")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }

        override fun getItemCount(): Int {
            return chats.size
        }

        inner class ViewHolder(internal val mView: View) : RecyclerView.ViewHolder(mView) {
            internal val userTextView: TextView
            internal val messageTextView: TextView
            internal var statusImageView: ImageView
            internal var incomingCountTextView: TextView
            internal var userStatusTextView: TextView
            internal var chatStatusLayout: LinearLayout
            internal var timeTextView: TextView
            internal var fromUserTextView: TextView
            internal var innerLayout: RelativeLayout

            init {
                userTextView = mView.findViewById(R.id.userTextView)
                messageTextView = mView.findViewById(R.id.messageTextView)
                statusImageView = mView.findViewById(R.id.statusImageView)
                incomingCountTextView = mView.findViewById(R.id.incomingCountTextView)
                userStatusTextView = mView.findViewById(R.id.userStatusTextView)
                chatStatusLayout = mView.findViewById(R.id.chatStatusLayout)
                timeTextView = mView.findViewById(R.id.timeTextView)
                fromUserTextView = mView.findViewById(R.id.fromUserTextView)
                innerLayout = mView.findViewById(R.id.innerLayout)
            }

        }
    }

}
