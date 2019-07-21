package com.mandiri.whatthehack.audiovideo.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import android.os.Vibrator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.mandiri.whatthehack.audiovideo.AudioCallActivity
import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.SocketEvent
import com.mandiri.whatthehack.audiovideo.UserDetailActivity
import com.mandiri.whatthehack.audiovideo.model.Chat
import com.mandiri.whatthehack.audiovideo.model.ChatContent
import com.mandiri.whatthehack.audiovideo.model.ChatContentType
import com.mandiri.whatthehack.audiovideo.model.ChatStatus
import com.mandiri.whatthehack.audiovideo.model.ChatType
import com.mandiri.whatthehack.audiovideo.model.Group
import com.mandiri.whatthehack.audiovideo.model.StarredChat
import com.mandiri.whatthehack.audiovideo.model.User
import com.mandiri.whatthehack.audiovideo.model.UserStatus
import com.mandiri.whatthehack.audiovideo.model.UserTypingStatus
import com.mandiri.whatthehack.audiovideo.other.ChatApplication
import com.mandiri.whatthehack.audiovideo.other.RecyclerItemClickListener
import com.mandiri.whatthehack.audiovideo.other.Util
import com.mandiri.whatthehack.audiovideo.other.ViewProxy
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.squareup.otto.Subscribe

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

import java.util.ArrayList
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import java.util.concurrent.TimeUnit

import io.realm.OrderedCollectionChangeSet
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private var startedDraggingX = -1f
    private var distCanMove = dp(80f).toFloat()
    private var startTime = 0L
    internal var timeInMilliseconds = 0L
    internal var timeSwapBuff = 0L
    internal var updatedTime = 0L
    private var timer: Timer? = null

    internal var realm: Realm = Realm.getDefaultInstance()
    internal var userOrigin: User? = null
    internal var userDestination: User? = null
    internal var groupDestination: Group? = null
    internal var otherUserStatus: UserStatus? = null
    internal var chats: RealmResults<Chat>? = null

    // Listener of keyboard typing, this runnable called when user finish typing
    // Give a delay 1000 to make sure that user is completely finished type
    internal var delay: Long = 1000 // 1 seconds after user stops typing
    internal var lastTextEdit: Long = 0
    internal var handler = Handler()

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {

            val userTypingObject = JsonObject()
            if (userDestination != null) {
                userTypingObject.addProperty("to", userDestination!!.phone)
            } else if (groupDestination != null) {
                userTypingObject.addProperty("to", groupDestination!!._id)
            }
            userTypingObject.addProperty("typing", false)
            ChatApplication.bus.post(SocketEvent(SocketEvent.TYPING, Gson().toJson(userTypingObject)))
        }
    }


    internal var contextMenu: Menu? = null
    internal var actionMode: ActionMode? = null
    private var isMultiSelect = false
    private var multiSelectList = ArrayList<Chat>()
    private val mActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.chat_option_menu, menu)
            contextMenu = menu
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_reply -> finishActionModeWithReplying()
                R.id.action_star -> finishActionModeWithStaring()
                R.id.action_info -> finishActionModeWithInfoing()
                R.id.action_delete -> finishActionModeWithDeleting()
                R.id.action_copy -> finishActionModeWithCopying()
                R.id.action_forward -> {
                }
                else -> finishActionModeMultiSelect()
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            finishActionModeMultiSelect()
        }
    }


    private fun startrecord() {
        // TODO Auto-generated method stub
        startTime = SystemClock.uptimeMillis()
        timer = Timer()
        val myTimerTask = MyTimerTask()
        timer!!.schedule(myTimerTask, 1000, 1000)
        vibrate()
    }

    private fun stoprecord() {
        // TODO Auto-generated method stub
        if (timer != null) {
            timer!!.cancel()
        }
        if (recording_time_text!!.text.toString() == "00:00") {
            return
        }
        recording_time_text!!.text = "00:00"
        vibrate()
    }

    private fun vibrate() {
        // TODO Auto-generated method stub
        try {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(200)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    internal inner class MyTimerTask : TimerTask() {

        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            val hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)))
            val lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime))
            println(lastsec.toString() + " hms " + hms)
            runOnUiThread {
                try {
                    if (recording_time_text != null)
                        recording_time_text!!.text = hms
                } catch (e: Exception) {
                    // TODO: handle exception
                }
            }
        }
    }

    private fun finishActionModeMultiSelect() {
        multiSelectList = ArrayList()
        recyclerView!!.adapter?.notifyDataSetChanged()
        isMultiSelect = false
        actionMode!!.finish()
        actionMode = null
    }

    private fun finishActionModeWithStaring() {
        // Set the chat to be starred
        // By adding to starredchat class
        for (chat in multiSelectList) {
            realm.executeTransaction { realm ->
                // When selected row is only one, just unstarred it
                // If more than one, star all chat
                if (multiSelectList.size > 1) {
                    val starredChat = StarredChat()
                    starredChat.chatId = chat._id
                    realm.insertOrUpdate(starredChat)
                } else {
                    var starredChat: StarredChat? = realm.where(StarredChat::class.java).equalTo("chatId", chat._id).findFirst()
                    if (starredChat != null) {
                        starredChat.deleteFromRealm()
                    } else {
                        starredChat = StarredChat()
                        starredChat.chatId = chat._id
                        realm.insertOrUpdate(starredChat)
                    }
                }
            }
        }
        finishActionModeMultiSelect()
    }

    private fun finishActionModeWithInfoing() {
        val chat = multiSelectList[0]

        // Show chat info detail
        val intent = Intent(this, ChatInfoActivity::class.java)
        intent.putExtra(ChatInfoActivity.CHAT_ID, chat._id)
        startActivity(intent)
    }

    private fun finishActionModeWithCopying() {
        var generatedChatStr = ""
        for (chat in multiSelectList) {

            // Loop through the chat, if only one chat, only the text is copied, if more, generate chat with the details
            if (multiSelectList.size > 1) {
                val chatDate = Util.formattedDate(chat.dateTime ?: "", Util.defaultFormattedDate, "d/M HH:mm")
                val user = realm.where(User::class.java).equalTo("phone", chat.from).findFirst()
                generatedChatStr += "[" + chatDate + "] " + (if (user != null) user.phone!! + ": " else "") + chat.content?.text + "\n"
            } else {
                generatedChatStr += chat.content?.text
            }
        }

        // Put generated string to clipboard, and show  a toast informing user that chat has been copied
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("link", generatedChatStr.trim { it <= ' ' })
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Chat has been copied to clipboard", Toast.LENGTH_SHORT).show()
        finishActionModeMultiSelect()
    }

    private fun finishActionModeWithDeleting() {
        AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Delete message?")
                .setPositiveButton("Delete") { dialogInterface, i ->
                    for (chat in multiSelectList) {
                        realm.executeTransaction { realm -> realm.where(Chat::class.java).equalTo("_id", chat._id).findAll().deleteAllFromRealm() }
                    }

                    finishActionModeMultiSelect()
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun finishActionModeWithReplying() {
        val chat = multiSelectList[0]

        val user = realm.where(User::class.java).equalTo("phone", chat.from).findFirst()
        replySenderTextView!!.text = user.phone
        replyTextView!!.text = chat.content?.text
        replyLayout!!.visibility = View.VISIBLE
        finishActionModeMultiSelect()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(toolbar)

        // Close reply layout first
        replyLayout!!.visibility = View.GONE

        realm = Realm.getDefaultInstance()

        // Get my user
        userOrigin = realm.where(User::class.java).equalTo("myself", true).findFirst()


        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = ChatAdapter()
        recyclerView!!.addOnItemTouchListener(RecyclerItemClickListener(this, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (isMultiSelect) {
                    multiSelectRowAt(position)
                } else {

                }
            }

            override fun onLongItemClick(view: View?, position: Int) {

                if (!isMultiSelect) {
                    multiSelectList = ArrayList()
                    isMultiSelect = true
                    if (actionMode == null) {
                        actionMode = startSupportActionMode(mActionModeCallback)
                    }
                }
                multiSelectRowAt(position)
            }
        }))

        getChatDestination()

        getAllChats()


        recyclerView!!.post { chats?.let { recyclerView!!.scrollToPosition(it.size - 1) } }

        sendReadStatusForAllUnreadChats()
        sendAllUnarrivedChats()

        // Assign Keyboard listener, so when keyboard showing up, the chat is scroll to bottom so last chat is to top off the keyboard
        KeyboardVisibilityEvent.setEventListener(
                this
        ) { recyclerView!!.post { recyclerView!!.scrollToPosition(chats!!.size - 1) } }

        setAudioButtonToReadyListenOnTouch()

        // Listen when user typing
        inputEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                       count: Int) {
                Log.i("INFO", s.toString())
                if (s.toString().length > 0) {
                    cameraButton!!.visibility = View.GONE
                    sendButton!!.setImageResource(R.drawable.input_send)
                } else {
                    cameraButton!!.visibility = View.VISIBLE
                    sendButton!!.setImageResource(R.drawable.ic_mic_white)
                }
                //You need to remove this to run only once
                handler.removeCallbacks(inputFinishChecker)

                val userTypingObject = JsonObject()
                if (userDestination != null) {
                    userTypingObject.addProperty("to", userDestination!!.phone)
                } else if (groupDestination != null) {
                    userTypingObject.addProperty("to", groupDestination!!._id)

                }
                userTypingObject.addProperty("typing", true)
                if (s.toString().length > 0) {
                    ChatApplication.bus.post(SocketEvent(SocketEvent.TYPING, Gson().toJson(userTypingObject)))
                }
            }

            override fun afterTextChanged(s: Editable) {
                //avoid triggering event when text is empty
                lastTextEdit = System.currentTimeMillis()
                handler.postDelayed(inputFinishChecker, delay)
            }
        })

        backButton.setOnClickListener { back() }
        replyCloseButton.setOnClickListener { closeReplyLayout() }
        sendButton.setOnClickListener { send() }
        sendButton.setOnLongClickListener { record() }
        userNameLayout.setOnClickListener { userDetailClicked() }
    }

    private fun getChatDestination() {

        // This chat activity can come from list of chat that clicked or from list of user that clicked
        // When comes from list of chat, the chat id that is sent use for search the last chat, that last chat contains the incoming user
        // Get the incoming user from that chat or from user id that is sent
        val lastClickedChatId = intent.getStringExtra(CHAT_ID)
        val lastChatClicked = realm.where(Chat::class.java).equalTo("_id", lastClickedChatId).findFirst()
        if (lastChatClicked != null) {
            userOrigin.let {
                userDestination = realm.where(User::class.java).equalTo("phone", if (lastChatClicked.to == it!!.phone) lastChatClicked.from else lastChatClicked.to).findFirst()
                if (userDestination == null) {
                    groupDestination = realm.where(Group::class.java).equalTo("_id", lastChatClicked.to).findFirst()
                }
            }
        } else {
            val userId = intent.getStringExtra(USER_ID)
            userDestination = realm.where(User::class.java).equalTo("phone", userId).findFirst()
            if (userDestination == null) {
                groupDestination = realm.where(Group::class.java).equalTo("_id", userId).findFirst()
            }
        }


    }

    private fun getAllChats() {

        // If this chat comes from a private chat, show info and chat from other user
        // Else if chat comes from groupDestination chat, show info and chat from that groupDestination
        if (userDestination != null) {
            userTextView!!.text = userDestination!!.name
            chats = realm.where(Chat::class.java).equalTo("from", userDestination!!.phone).or().equalTo("to", userDestination!!.phone).findAllSorted("dateTime")

            askingOnlineForThisChatDestination()
        } else if (groupDestination != null) {
            userTextView!!.text = groupDestination!!.subject
            chats = realm.where(Chat::class.java).equalTo("to", groupDestination!!._id).findAllSorted("dateTime")
        }


        // When this chat results change, (ex: new chat coming) change the row for that recycler
        // Make an animation for the row inserted
        chats?.addChangeListener(OrderedRealmCollectionChangeListener { chats, changeSet ->
            // `null`  means the async query returns the first time.
            if (changeSet == null) {

                recyclerView!!.adapter?.notifyDataSetChanged()
                recyclerView!!.scrollToPosition(chats.size - 1)
                return@OrderedRealmCollectionChangeListener
            }
            // For deletions, the adapter has to be notified in reverse order.
            val deletions = changeSet.deletionRanges
            for (i in deletions.indices.reversed()) {

                val range = deletions[i]
                recyclerView!!.adapter?.notifyItemRangeRemoved(range.startIndex, range.length)
                recyclerView!!.scrollToPosition(chats.size - 1)
            }

            val insertions = changeSet.insertionRanges
            for (range in insertions) {

                recyclerView!!.adapter?.notifyItemRangeInserted(range.startIndex, range.length)
                recyclerView!!.scrollToPosition(chats.size - 1)
            }

            val modifications = changeSet.changeRanges
            for (range in modifications) {

                recyclerView!!.adapter?.notifyItemRangeChanged(range.startIndex, range.length)
                recyclerView!!.scrollToPosition(chats.size - 1)
            }
        })

    }

    private fun askingOnlineForThisChatDestination() {

        // Asking online for this user
        val askOnlineObject = JsonObject()
        askOnlineObject.addProperty("to", userDestination!!.phone)
        ChatApplication.bus.post(SocketEvent(SocketEvent.ONLINE, Gson().toJson(askOnlineObject)))
    }

    private fun sendReadStatusForAllUnreadChats() {

        // When arrive to this activity, send to socket the read status for all chats that have not been read
        realm.executeTransaction { realm ->
            if (userDestination != null) {
                userOrigin.let {
                    val unreadChats = realm.where(Chat::class.java).equalTo("to", it!!.phone).isNull("readTime").findAll()
                    for (chat in unreadChats) {
                        // If this chat comes from private chat,
                        // read all chat that shown in this page
                        chat.readTime = Util.localToUtcFormatDate(Date())
                        ChatApplication.bus.post(SocketEvent(SocketEvent.CHAT_READ, GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(realm.copyFromRealm(chat))))
                    }
                }

            } else if (groupDestination != null) {
                userOrigin.let {
                    val unReadChats = realm.where(Chat::class.java).equalTo("to", groupDestination!!._id).notEqualTo("from", it!!.phone).isNull("readTime").findAll()
                    for (chat in unReadChats) {
                        chat.readTime = Util.localToUtcFormatDate(Date())
                        ChatApplication.bus.post(SocketEvent(SocketEvent.CHAT_READ, GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(realm.copyFromRealm(chat))))
                    }
                }
            }
        }
    }

    private fun sendAllUnarrivedChats() {

        // When arrive to this activity, send to socket the read status for all chats that have not been read
        realm.executeTransaction { realm ->
            if (userDestination != null) {
                userOrigin.let {

                    val unreadChats = realm.where(Chat::class.java).equalTo("from", it!!.phone).isNull("arrivedTime").findAll()
                    for (chat in unreadChats) {
                        // If this chat comes from private chat,
                        // read all chat that shown in this page

                        // Post a bus to socket io of a new chat
                        ChatApplication.bus.post(SocketEvent(SocketEvent.CHAT, Gson().toJson(realm.copyFromRealm(chat))))
                    }
                }
            } else if (groupDestination != null) {
                userOrigin.let {
                    val unReadChats = realm.where(Chat::class.java).equalTo("to", groupDestination!!._id).equalTo("from", it!!.phone).isNull("arrivedTime").findAll()
                    for (chat in unReadChats) {
                        // Post a bus to socket io of a new chat
                        ChatApplication.bus.post(SocketEvent(SocketEvent.CHAT, Gson().toJson(realm.copyFromRealm(chat))))
                    }
                }
            }
        }
    }

    // Add/Remove the item from/to the list

    fun multiSelectRowAt(position: Int) {
        if (actionMode != null) {
            if (multiSelectList.contains(chats!![position]))
                multiSelectList.remove(chats!![position])
            else
                multiSelectList.add(chats!![position])

            actionMode!!.menu.findItem(R.id.action_star).setIcon(if (realm.where(StarredChat::class.java).equalTo("chatId", chats!![position]._id).count() > 0) R.drawable.ic_starred_white else R.drawable.ic_star_white)
            actionMode!!.menu.findItem(R.id.action_reply).isVisible = multiSelectList.size <= 1
            actionMode!!.menu.findItem(R.id.action_info).isVisible = multiSelectList.size <= 1

            if (multiSelectList.size > 0)
                actionMode!!.title = "" + multiSelectList.size
            else {
                finishActionModeMultiSelect()
            }


            recyclerView!!.adapter?.notifyDataSetChanged()
        }
    }


    private fun setAudioButtonToReadyListenOnTouch() {

        chat_audio_send_button!!.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val params = slideText!!
                        .layoutParams as FrameLayout.LayoutParams
                params.leftMargin = dp(30f)
                slideText!!.layoutParams = params
                ViewProxy.setAlpha(slideText, 1f)
                startedDraggingX = -1f
                // startRecording();
                startrecord()
                chat_audio_send_button!!.parent
                        .requestDisallowInterceptTouchEvent(true)
                record_panel!!.visibility = View.VISIBLE
            } else if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
                startedDraggingX = -1f
                stoprecord()
                // stopRecording(true);
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                var x = motionEvent.x
                if (x < -distCanMove) {
                    stoprecord()
                    // stopRecording(false);
                }
                x = x + ViewProxy.getX(chat_audio_send_button)
                val params = slideText!!
                        .layoutParams as FrameLayout.LayoutParams
                if (startedDraggingX != -1f) {
                    val dist = x - startedDraggingX
                    params.leftMargin = dp(30f) + dist.toInt()
                    slideText!!.layoutParams = params
                    var alpha = 1.0f + dist / distCanMove
                    if (alpha > 1) {
                        alpha = 1f
                    } else if (alpha < 0) {
                        alpha = 0f
                    }
                    ViewProxy.setAlpha(slideText, alpha)
                }
                if (x <= (ViewProxy.getX(slideText) + slideText!!.width.toFloat()
                        + dp(30f).toFloat())) {
                    if (startedDraggingX == -1f) {
                        startedDraggingX = x
                        distCanMove = (record_panel!!.measuredWidth
                                - slideText!!.measuredWidth - dp(48f)) / 2.0f
                        if (distCanMove <= 0) {
                            distCanMove = dp(80f).toFloat()
                        } else if (distCanMove > dp(80f)) {
                            distCanMove = dp(80f).toFloat()
                        }
                    }
                }
                if (params.leftMargin > dp(30f)) {
                    params.leftMargin = dp(30f)
                    slideText!!.layoutParams = params
                    ViewProxy.setAlpha(slideText, 1f)
                    startedDraggingX = -1f
                }
            }
            view.onTouchEvent(motionEvent)
            true
        }

    }

    override fun onResume() {
        super.onResume()

        // Register for bus
        ChatApplication.bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        ChatApplication.bus.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.removeAllChangeListeners()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.phoneCall -> {
                val intent = Intent(this, AudioCallActivity::class.java)
                intent.putExtra(AudioCallActivity.DESTINATION_CALL, userDestination!!.phone)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal fun back() {
        finish()
    }

    internal fun closeReplyLayout() {
        replyLayout!!.visibility = View.GONE
    }

    internal fun send() {
        val input = inputEditText!!.text.toString()

        if (TextUtils.isEmpty(input)) {
            return
        }

        userOrigin.let {

            val chat = Chat()
            chat._id = UUID.randomUUID().toString()
            chat.from = it!!.phone

            if (userDestination != null) {
                chat.to = userDestination!!.phone
                chat.type = ChatType.PRIVATE
            } else if (groupDestination != null) {
                chat.to = groupDestination!!._id
                chat.type = ChatType.GROUP
            }

            val chatContent = ChatContent()
            chatContent.text = input
            chatContent.type = ChatContentType.TEXT
            chat.content = chatContent
            chat.dateTime = Util.localToUtcFormatDate(Date())

            // Save to realm
            realm.executeTransaction { realm -> realm.insert(chat) }

            // Post a bus to socket io of a new chat
            ChatApplication.bus.post(SocketEvent(SocketEvent.CHAT, Gson().toJson(chat)))

            // After send the chat, remove text from edittext
            inputEditText!!.setText("")
        }
    }

    internal fun record(): Boolean {

        return true
    }

    internal fun userDetailClicked() {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra(UserDetailActivity.USER_ID, userDestination!!.phone)
        startActivity(intent)
    }

    @Subscribe
    fun receiveChatFromSocket(chat: Chat) {
        // If get notification of a new chat
        realm.executeTransaction { realm ->
            val updatedChat = realm.where(Chat::class.java).equalTo("_id", chat._id).findFirst()
            if (updatedChat != null) {
                updatedChat.readTime = Util.localToUtcFormatDate(Date())
            }

            ChatApplication.bus.post(SocketEvent(SocketEvent.CHAT_READ, GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(realm.copyFromRealm(updatedChat))))
        }
    }

    @Subscribe
    fun receiveChatStatusFromSocket(chatStatus: ChatStatus) {

        // Only get notification to update chat
        val chat = chatStatus.chat
        val updatedChat = realm.where(Chat::class.java).equalTo("_id", chat?._id).findFirst()
        recyclerView!!.adapter?.notifyItemChanged(chats!!.indexOf(updatedChat))
    }

    @Subscribe
    fun receiveEventFromSocket(event: String) {
        when (event) {
            Socket.EVENT_CONNECT -> {
                askingOnlineForThisChatDestination()
                sendAllUnarrivedChats()
            }
            else -> userStatusTextView!!.text = "Connecting..."
        }
        Toast.makeText(this, event, Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    fun receiveUserStatusFromSocket(status: UserStatus) {
        otherUserStatus = status
        userStatusTextView!!.text = if (status.isOnline) "Online" else "Last seen " + Util.utcToLocalformattedDate(otherUserStatus!!.lastSeen ?: "", Util.defaultFormattedDate, "HH:mm")
    }

    @Subscribe
    fun receiveUserTypingStatusFromSocket(status: UserTypingStatus) {
        if (userDestination != null) {
            userStatusTextView!!.text = if (status.isTyping && status.from == userDestination!!.phone)
                "... is typing"
            else if (otherUserStatus != null && otherUserStatus!!.isOnline) "Online" else if (otherUserStatus != null) "Last seen " + Util.formattedDate(otherUserStatus!!.lastSeen ?: "", "dd MM yyyy HH:mm", "HH:mm") else ""
        } else if (groupDestination != null) {
            userOrigin.let {
                userStatusTextView!!.text = if (status.isTyping && status.from != it!!.phone) status.from + "... is typing" else ""
            }
        }

    }

    // Chat Adapter
    private inner class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

        internal val SELF = 0
        internal val OTHER = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == SELF) {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_self_row, parent, false)
                return ViewHolder(view)
            }

            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_other_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            try {
                val chat = chats!![position]
                holder.messageTextView?.text = chat.content?.text
                holder.timeTextView?.text = Util.utcToLocalformattedDate(chat.dateTime ?: "", Util.defaultFormattedDate, "HH:mm")

                // When showing row of this user chat
                // Set checkmark status according to chat status
                if (holder.itemViewType == SELF) {
                    if (chat.type == ChatType.PRIVATE) {
                        holder.statusImageView?.setImageResource(
                                if (chat.readTime != null)
                                    R.drawable.message_got_read_receipt_from_target
                                else if (chat.deliveredTime != null)
                                    R.drawable.message_got_receipt_from_target
                                else if (chat.arrivedTime != null)
                                    R.drawable.message_got_receipt_from_server
                                else
                                    R.drawable.waiting)
                    } else if (chat.type == ChatType.GROUP) {

                        holder.statusImageView?.setImageResource(
                                if ((chat.groupChatReadTimes?.size ?: 0) >= groupDestination!!.users!!.size)
                                    R.drawable.message_got_read_receipt_from_target
                                else if ((chat.groupChatDeliveredTimes?.size ?: 0) >= groupDestination!!.users!!.size)
                                    R.drawable.message_got_receipt_from_target
                                else if (chat.arrivedTime != null)
                                    R.drawable.message_got_receipt_from_server
                                else
                                    R.drawable.waiting)
                    }
                }

                if (multiSelectList.contains(chat)) {
                    holder.itemView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                } else {
                    holder.itemView.setBackgroundColor(resources.getColor(android.R.color.transparent))
                }

                holder.starredImageView?.visibility = if (realm.where(StarredChat::class.java).equalTo("chatId", chat._id).count() > 0) View.VISIBLE else View.GONE
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }


        override fun getItemViewType(position: Int): Int {
            val currentRow = chats!![position]
            userOrigin.let {
                return if (currentRow.from == it!!.phone) {
                    SELF
                } else {
                    OTHER
                }
            }
        }

        override fun getItemCount(): Int {
            return if (chats != null) chats!!.size else 0
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var messageTextView: TextView?
            var timeTextView: TextView?
            var statusImageView: ImageView?
            var starredImageView: ImageView?

            init {
                messageTextView = itemView.findViewById(R.id.messageTextView)
                timeTextView = itemView.findViewById(R.id.timeTextView)
                statusImageView = itemView.findViewById(R.id.statusImageView)
                starredImageView = itemView.findViewById(R.id.starredImageView)
            }
        }
    }

    companion object {

        val CHAT_ID = "chatId"
        val USER_ID = "userId"

        fun dp(value: Float): Int {
            return Math.ceil((1 * value).toDouble()).toInt()
        }
    }

}

