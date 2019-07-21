package com.mandiri.agentapp.audiovideo.model

import io.realm.RealmObject

open class GroupChatDeliveredTime : RealmObject() {
    var userId: String? = null
    var deliveredTime: String? = null
}
