package com.mandiri.whatthehack.audiovideo.model

import java.util.ArrayList

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by andreyyoshuamanik on 9/23/17.
 */

open class Group : RealmObject() {
    var _id: String? = null
    var subject: String? = null
    var users: RealmList<User>? = null
    var createdDate: String? = null
    var joinedDate: String? = null
}
