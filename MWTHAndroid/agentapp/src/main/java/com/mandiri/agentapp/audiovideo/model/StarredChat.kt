package com.mandiri.agentapp.audiovideo.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by andreyyoshuamanik on 9/24/17.
 */

open class StarredChat : RealmObject() {
    @PrimaryKey
    var chatId: String? = null
}
