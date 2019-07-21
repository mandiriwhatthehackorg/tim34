package com.mandiri.agentapp.audiovideo

import android.util.Log

import org.webrtc.CameraVideoCapturer

/**
 * Webrtc_Step2
 * Created by vivek-3102 on 11/03/17.
 */

class CustomCameraEventsHandler : CameraVideoCapturer.CameraEventsHandler {

    private val logTag = this.javaClass.canonicalName


    override fun onCameraError(s: String) {
        Log.d(logTag, "onCameraError() called with: s = [$s]")
    }

    override fun onCameraDisconnected() {

    }

    override fun onCameraFreezed(s: String) {
        Log.d(logTag, "onCameraFreezed() called with: s = [$s]")
    }


    override fun onCameraOpening(i: String) {
        Log.d(logTag, "onCameraOpening() called with: i = [$i]")
    }

    override fun onFirstFrameAvailable() {
        Log.d(logTag, "onFirstFrameAvailable() called")
    }

    override fun onCameraClosed() {
        Log.d(logTag, "onCameraClosed() called")
    }
}
