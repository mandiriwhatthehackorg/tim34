package com.mandiri.agentapp.audiovideo.other

import android.os.Handler
import android.os.Looper

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

/**
 * Created by andreyyoshuamanik on 9/19/17.
 */

class MainThreadBus : Bus(ThreadEnforcer.ANY) {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun post(event: Any) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event)
        } else {
            mHandler.post { super@MainThreadBus.post(event) }
        }
    }
}