package com.mandiri.agentapp.audiovideo.model

import io.realm.RealmObject

/**
 * Created by andreyyoshuamanik on 9/16/17.
 */

open class GroupChatReadTime : RealmObject() {
    var userId: String? = null
    var readTime: String? = null
}
