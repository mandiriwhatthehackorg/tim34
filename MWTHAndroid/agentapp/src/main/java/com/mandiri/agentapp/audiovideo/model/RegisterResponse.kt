package com.mandiri.agentapp.audiovideo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject

open class RegisterResponse : RealmObject() {

    @SerializedName("token")
    @Expose
    var token: String? = null
    @SerializedName("user")
    @Expose
    var user: User? = null

}

