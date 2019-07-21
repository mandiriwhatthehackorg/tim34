package com.mandiri.agentapp.audiovideo.model

/**
 * Created by andreyyoshuamanik on 9/20/17.
 */

class ChatStatus {

    var chat: Chat? = null
    var status: String? = null

    fun isStatus(status: String): Boolean {
        return status == status
    }

    companion object {

        val READ = "read"
        val DELIVERED = "delivered"
        val ARRIVED = "arrived"
    }
}
