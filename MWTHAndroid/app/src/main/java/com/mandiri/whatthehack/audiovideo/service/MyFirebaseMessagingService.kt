package com.mandiri.whatthehack.audiovideo.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

import androidx.core.app.NotificationCompat

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.api.API
import com.mandiri.whatthehack.audiovideo.api.ResponseHandler
import com.mandiri.whatthehack.audiovideo.model.Group
import com.mandiri.whatthehack.audiovideo.model.GroupResponse
import com.mandiri.whatthehack.audiovideo.model.User
import com.mandiri.whatthehack.audiovideo.other.Util
import com.mandiri.whatthehack.audiovideo.registration.RegistrationActivity
import com.mandiri.whatthehack.audiovideo.model.Chat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.util.Date

import io.realm.Realm

/**
 * Created by andreyyoshuamanik on 9/17/17.
 */

//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
//        super.onMessageReceived(remoteMessage)
//
//        Log.i("FIREBASE MESSAGING", remoteMessage!!.data.toString())
//        // If new message coming and contains chat
//        // save it and show a local notification about this chat
//        //
//        if (remoteMessage.data.containsKey("content")) {
//            // Save the chat as delivered
//            val chatStr = remoteMessage.data["content"]
//            val chat = Gson().fromJson(chatStr, Chat::class.java)
//            val groupResponse = Gson().fromJson(chatStr, GroupResponse::class.java)
//
//            val realm = Realm.getDefaultInstance()
//
//            // If new chat coming from push notif, save it, and set delivered time and send back to server in the background
//            // else If incoming message is group and first chat, save both the group and the first chat
//            if (chat != null && (groupResponse!!.notifChat == null || groupResponse.group == null)) {
//
//                chat.deliveredTime = Util.localToUtcFormatDate(Date())
//
//                if (realm.where(Chat::class.java).equalTo("_id", chat._id).count() > 0) {
//                    realm.close()
//                    return
//                }
//                realm.executeTransaction { realm -> realm.insertOrUpdate(chat) }
//
//                // Show the notification
//                val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                val intent = Intent(this, RegistrationActivity::class.java)
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                val contentIntent = PendingIntent.getActivity(
//                    this, 0,
//                    intent, PendingIntent.FLAG_ONE_SHOT
//                )
//                val mBuilder = NotificationCompat.Builder(this)
//                    .setContentTitle("You got a new Message from " + chat.from!!)
//                    .setContentText(chat.content!!.text)
//                    .setSmallIcon(R.mipmap.ic_launcher) as NotificationCompat.Builder
//                mBuilder.setContentIntent(contentIntent)
//                mNotificationManager.notify(1, mBuilder.build())
//
//
//                API.postReadAChat(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(chat), object : ResponseHandler<String> {
//                    override fun response(success: Boolean, data: String?, errorMessage: String?) {
//
//                    }
//
//                })
//
//
//            } else if (groupResponse != null) {
//
//                val groupChat = groupResponse.notifChat
//
//                if (realm.where(Chat::class.java).equalTo("_id", groupChat!!._id).count() > 0) {
//                    realm.close()
//                    return
//                }
//
//                if (realm.where(Group::class.java).equalTo("_id", groupResponse.group!!._id).count() > 0) {
//                    realm.close()
//                    return
//                }
//                realm.executeTransaction { realm ->
//                    var myself = realm.where(User::class.java).equalTo("myself", true).findFirst()
//                    myself = realm.copyFromRealm(myself)
//                    realm.insertOrUpdate(groupChat)
//                    realm.insertOrUpdate(groupResponse.group!!)
//                    realm.insertOrUpdate(myself)
//                }
//
//                API.postReadAChat(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(groupChat), object : ResponseHandler<String> {
//                    override fun response(success: Boolean, data: String?, errorMessage: String?) {
//
//                    }
//
//                })
//
//                //                try {
//                //                    IO.Options options = new IO.Options();
//                //                    Token token = realm.where(Token.class).findFirst();
//                //                    options.query = "token=" + token.getToken();
//                //
//                //                    final Socket mSocket = IO.socket(API.API_BASE_URL + "/chat", options);
//                //                    mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//                //                        @Override
//                //                        public void call(Object... args) {
//                //                            mSocket.emit(SocketEvent.CHAT_DELIVERED, new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(groupChat));
//                //                            Log.i("INFO", "Connect");
//                //                            mSocket.disconnect();
//                //                        }
//                //                    });
//                //                    mSocket.connect();
//                //                } catch (URISyntaxException e) { e.printStackTrace(); }
//            }
//
//            realm.close()
//        }
//    }
//}
