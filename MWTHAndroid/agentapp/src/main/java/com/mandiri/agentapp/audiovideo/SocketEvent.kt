package com.mandiri.agentapp.audiovideo

class SocketEvent(var action: String?, var `object`: Any?) {
    companion object {
        val SOCKET = "socket"
        val CHAT = "chat"
        val CHAT_DELIVERED = "chatDelivered"
        val CHAT_READ = "chatRead"
        val CHAT_ARRIVED = "chatArrived"
        val ONLINE = "askingUserOnline"
        val TYPING = "askingUserTyping"
        val GROUP_AND_FIRSTCHAT = "groupAndFirstChat"
        val MESSAGE = "message"

        val CREATE_OR_JOIN_ROOM = "create or join"
        val ROOM_CREATED = "created"
        val JOINED_ROOM = "joined"
        val ROOM_FULL = "full"
        val ROOM_READY = "ready"
    }
}
