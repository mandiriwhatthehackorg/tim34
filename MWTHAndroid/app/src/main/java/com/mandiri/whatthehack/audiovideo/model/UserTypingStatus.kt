package com.mandiri.whatthehack.audiovideo.model

/**
 * Created by andreyyoshuamanik on 9/20/17.
 */

class UserTypingStatus {

    var isTyping: Boolean = false
    var from: String? = null

    constructor() {

    }

    constructor(typing: Boolean, from: String) {
        this.isTyping = typing
        this.from = from
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as UserTypingStatus?

        return from == that!!.from

    }

    override fun hashCode(): Int {
        return 31 * from!!.hashCode()
    }

    companion object {
        val TYPING = "typing"
        val NOT_TYPING = "notTyping"
    }
}
