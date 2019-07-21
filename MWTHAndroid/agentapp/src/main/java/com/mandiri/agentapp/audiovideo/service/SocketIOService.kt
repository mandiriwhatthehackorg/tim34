package com.mandiri.agentapp.audiovideo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

import com.mandiri.agentapp.audiovideo.SocketEvent
import com.mandiri.agentapp.audiovideo.api.API
import com.mandiri.agentapp.audiovideo.model.Chat
import com.mandiri.agentapp.audiovideo.model.ChatStatus
import com.mandiri.agentapp.audiovideo.model.ChatType
import com.mandiri.agentapp.audiovideo.model.Group
import com.mandiri.agentapp.audiovideo.model.GroupChatDeliveredTime
import com.mandiri.agentapp.audiovideo.model.GroupChatReadTime
import com.mandiri.agentapp.audiovideo.model.GroupResponse
import com.mandiri.agentapp.audiovideo.model.Token
import com.mandiri.agentapp.audiovideo.model.User
import com.mandiri.agentapp.audiovideo.model.UserStatus
import com.mandiri.agentapp.audiovideo.model.UserTypingStatus
import com.mandiri.agentapp.audiovideo.other.ChatApplication
import com.mandiri.agentapp.audiovideo.other.NetworkUtil
import com.mandiri.agentapp.audiovideo.other.Util
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.squareup.otto.Subscribe

import java.net.URISyntaxException
import java.util.Date

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

/**
 * Created by andreyyoshuamanik on 9/18/17.
 */

class SocketIOService : Service() {


    private var mSocket: Socket? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(i: Intent, flags: Int, startId: Int): Int {

        val realm = Realm.getDefaultInstance()
        try {
            val options = IO.Options()
            val token = realm.where(Token::class.java).findFirst()
            options.query = "token=" + token.token!!
            mSocket = IO.socket(API.API_BASE_URL + "/chat", options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }


        mSocket!!.on(Socket.EVENT_CONNECT) {
            ChatApplication.bus.post(Socket.EVENT_CONNECT)


            val realm = Realm.getDefaultInstance()
            // When user connected to socket, send all unsent / unarrived chat to socket again
            val myself = realm.where(User::class.java).equalTo("myself", true).findFirst()
            val chats = realm.where(Chat::class.java).isNull("arrivedTime").equalTo("from", myself.phone).findAll()
            for (chat in chats) {
                // Post a bus to socket io of a new chat
                socketEmitObject(SocketEvent(SocketEvent.CHAT, Gson().toJson(realm.copyFromRealm(chat))))
            }
            realm.close()


            Log.i("INFO", "Connect")
        }

        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            ChatApplication.bus.post(Socket.EVENT_CONNECT_ERROR)
            Log.i("INFO", Socket.EVENT_CONNECT_ERROR)
        }

        mSocket!!.on(Socket.EVENT_ERROR) {
            ChatApplication.bus.post(Socket.EVENT_ERROR)
            Log.i("INFO", Socket.EVENT_ERROR)
        }

        mSocket!!.on(Socket.EVENT_RECONNECT) {
            ChatApplication.bus.post(Socket.EVENT_RECONNECT)
            Log.i("INFO", Socket.EVENT_RECONNECT)
        }

        mSocket!!.on(Socket.EVENT_RECONNECT_ATTEMPT) {
            ChatApplication.bus.post(Socket.EVENT_RECONNECT_ATTEMPT)
            Log.i("INFO", Socket.EVENT_RECONNECT_ATTEMPT)
        }

        mSocket!!.on(Socket.EVENT_RECONNECT_ERROR) {
            ChatApplication.bus.post(Socket.EVENT_RECONNECT_ERROR)
            Log.i("INFO", Socket.EVENT_RECONNECT_ERROR)
        }

        mSocket!!.on(Socket.EVENT_RECONNECT_FAILED) {
            ChatApplication.bus.post(Socket.EVENT_RECONNECT_FAILED)
            Log.i("INFO", Socket.EVENT_RECONNECT_FAILED)
        }

        mSocket!!.on(Socket.EVENT_RECONNECTING) {
            ChatApplication.bus.post(Socket.EVENT_RECONNECTING)
            Log.i("INFO", Socket.EVENT_RECONNECTING)
        }

        mSocket!!.on(Socket.EVENT_DISCONNECT) {
            ChatApplication.bus.post(Socket.EVENT_DISCONNECT)
            Log.i("INFO", Socket.EVENT_DISCONNECT)
        }

        mSocket!!.on(SocketEvent.CHAT) { args ->
            if (args.size > 0) {
                val chat = Gson().fromJson(args[0].toString() + "", Chat::class.java)

                // If new chat coming set delivered time
                // Send the chat back to socket
                chat.deliveredTime = Util.localToUtcFormatDate(Date())
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm -> realm.insertOrUpdate(chat) }
                realm.close()
                socketEmitObject(
                    SocketEvent(
                        SocketEvent.CHAT_DELIVERED,
                        GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(chat)
                    )
                )

                ChatApplication.bus.post(chat)
            }
        }

        mSocket!!.on(SocketEvent.CHAT_DELIVERED) { args ->
            if (args.size > 0) {
                val chat = Gson().fromJson(args[0].toString() + "", Chat::class.java)
                val chatStatus = ChatStatus()
                chatStatus.chat = chat
                chatStatus.status = ChatStatus.DELIVERED

                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->
                    val updatedChat = realm.where(Chat::class.java).equalTo("_id", chat._id).findFirst()
                    if (updatedChat != null) {
                        if (chatStatus.isStatus(ChatStatus.DELIVERED)) {
                            if (updatedChat.type == ChatType.PRIVATE) {
                                updatedChat.deliveredTime = chat.deliveredTime
                            } else if (updatedChat.type == ChatType.GROUP) {
                                if (updatedChat.groupChatDeliveredTimes == null) {
                                    updatedChat.groupChatDeliveredTimes = RealmList()
                                }
                                updatedChat.groupChatDeliveredTimes!!.add(chat.groupChatDeliveredTimes!!.first())
                            }
                        }
                    }
                }
                realm.close()
                ChatApplication.bus.post(chatStatus)
            }
        }

        mSocket!!.on(SocketEvent.CHAT_READ) { args ->
            if (args.size > 0) {
                val chat = Gson().fromJson(args[0].toString() + "", Chat::class.java)
                val chatStatus = ChatStatus()
                chatStatus.chat = chat
                chatStatus.status = ChatStatus.READ

                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->
                    val updatedChat = realm.where(Chat::class.java).equalTo("_id", chat._id).findFirst()
                    if (updatedChat != null) {
                        if (chatStatus.isStatus(ChatStatus.READ)) {
                            if (updatedChat.type == ChatType.PRIVATE) {
                                updatedChat.readTime = chat.readTime

                            } else if (updatedChat.type == ChatType.GROUP) {
                                if (updatedChat.groupChatReadTimes == null) {
                                    updatedChat.groupChatReadTimes = RealmList()
                                }
                                updatedChat.groupChatReadTimes!!.add(chat.groupChatReadTimes!!.first())
                            }
                        }
                    }
                }
                realm.close()
                ChatApplication.bus.post(chatStatus)
            }
        }

        mSocket!!.on(SocketEvent.CHAT_ARRIVED) { args ->
            if (args.size > 0) {
                val chat = Gson().fromJson(args[0].toString() + "", Chat::class.java)
                val chatStatus = ChatStatus()
                chatStatus.chat = chat
                chatStatus.status = ChatStatus.ARRIVED

                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->
                    val updatedChat = realm.where(Chat::class.java).equalTo("_id", chat._id).findFirst()
                    if (updatedChat != null) {

                        if (chatStatus.isStatus(ChatStatus.ARRIVED)) {
                            updatedChat.arrivedTime = chat.arrivedTime
                        }

                    }
                }
                realm.close()


                ChatApplication.bus.post(chatStatus)
            }
        }

        mSocket!!.on(SocketEvent.GROUP_AND_FIRSTCHAT, Emitter.Listener { args ->
            if (args.size > 0) {
                val groupResponse = Gson().fromJson(args[0].toString() + "", GroupResponse::class.java)
                val groupChat = groupResponse.notifChat
                val realm = Realm.getDefaultInstance()

                if (realm.where(Chat::class.java).equalTo("_id", groupChat!!._id).count() > 0) {
                    realm.close()
                    return@Listener
                }

                if (realm.where(Group::class.java).equalTo("_id", groupResponse.group!!._id).count() > 0) {
                    realm.close()
                    return@Listener
                }
                realm.executeTransaction { realm ->
                    var myself = realm.where(User::class.java).equalTo("myself", true).findFirst()
                    myself = realm.copyFromRealm(myself)
                    realm.insertOrUpdate(groupChat)
                    realm.insertOrUpdate(groupResponse.group!!)
                    realm.insertOrUpdate(myself)
                }
                realm.close()

                ChatApplication.bus.post(groupChat)
            }
        })

        mSocket!!.on(SocketEvent.ONLINE) { args ->
            if (args.size > 0) {
                val status = Gson().fromJson(args[0].toString() + "", UserStatus::class.java)
                ChatApplication.bus.post(status)
            }
        }

        mSocket!!.on(SocketEvent.TYPING) { args ->
            if (args.size > 0) {
                val status = Gson().fromJson(args[0].toString() + "", UserTypingStatus::class.java)
                ChatApplication.bus.post(status)
            }
        }

        mSocket!!.on(SocketEvent.MESSAGE) { args ->
            if (args.size > 0) {
                val jsonObject = Gson().fromJson(args[0].toString() + "", JsonObject::class.java)
                ChatApplication.bus.post(jsonObject)
            }
        }


        mSocket!!.on(SocketEvent.ROOM_CREATED) { ChatApplication.bus.post(SocketEvent.ROOM_CREATED) }

        mSocket!!.on(SocketEvent.JOINED_ROOM) { ChatApplication.bus.post(SocketEvent.JOINED_ROOM) }

        mSocket!!.on(SocketEvent.ROOM_FULL) { ChatApplication.bus.post(SocketEvent.ROOM_FULL) }

        mSocket!!.on(SocketEvent.ROOM_READY) { ChatApplication.bus.post(SocketEvent.ROOM_READY) }
        mSocket!!.connect()

        return Service.START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()

        ChatApplication.bus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket!!.disconnect()
        ChatApplication.bus.unregister(this)
        stopSelf()
    }

    @Subscribe
    fun socketEmitObject(event: SocketEvent) {
        if ((NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_MOBILE || NetworkUtil.getConnectivityStatus(
                this
            ) == NetworkUtil.TYPE_WIFI) && mSocket!!.connected()
        ) {
            mSocket!!.emit(event.action, event.`object`)
        }
    }
}
