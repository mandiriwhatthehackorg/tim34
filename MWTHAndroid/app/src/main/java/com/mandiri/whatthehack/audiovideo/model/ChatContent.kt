package com.mandiri.whatthehack.audiovideo.model

import io.realm.RealmObject

open class ChatContent : RealmObject() {
    var text: String? = null
    var type: String? = null
}
