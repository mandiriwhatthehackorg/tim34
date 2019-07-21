package com.mandiri.agentapp.audiovideo

import android.util.Log

import com.google.gson.Gson

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 * Webrtc_Step2
 * Created by vivek-3102 on 11/03/17.
 */

internal open class CustomSdpObserver(val tag: String = CustomSdpObserver::class.java.canonicalName ?: "") : SdpObserver {


    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        Log.i("INFO", Gson().toJson(sessionDescription))
        Log.d(tag, "onCreateSuccess() called with: sessionDescription = [" + "" + "]")
    }

    override fun onSetSuccess() {
        Log.d(tag, "onSetSuccess() called")
    }

    override fun onCreateFailure(s: String) {
        Log.d(tag, "onCreateFailure() called with: s = [$s]")
    }

    override fun onSetFailure(s: String) {
        Log.d(tag, "onSetFailure() called with: s = [$s]")
    }

}
