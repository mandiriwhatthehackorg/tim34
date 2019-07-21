package com.mandiri.whatthehack.audiovideo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {

    @PrimaryKey
    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
    var status: String? = null
    var profilePictUrl: String? = null

    var myself: Boolean = false

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val user = o as User?

        if (if (phone != null) phone != user!!.phone else user!!.phone != null) return false
        return if (name != null) name == user.name else user.name == null

    }

    override fun hashCode(): Int {
        var result = if (phone != null) phone!!.hashCode() else 0
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        return result
    }
}
