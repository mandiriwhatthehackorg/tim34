package com.mandiri.whatthehack.audiovideo.model

import com.google.gson.annotations.Expose

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Chat : RealmObject() {

    @Expose
    @PrimaryKey
    var _id: String? = null
    var to: String? = null
    var from: String? = null
    var content: ChatContent? = null
    var dateTime: String? = null
    @Expose
    var type: String? = null
    @Expose
    var deliveredTime: String? = null
    @Expose
    var readTime: String? = null
    @Expose
    var arrivedTime: String? = null
    var groupChatDeliveredTimes: RealmList<GroupChatDeliveredTime>? = null
    var groupChatReadTimes: RealmList<GroupChatReadTime>? = null
}

