package com.mandiri.whatthehack.audiovideo.service

import android.util.Log

import com.mandiri.whatthehack.audiovideo.api.API
import com.mandiri.whatthehack.audiovideo.api.ResponseHandler
//import com.google.firebase.iid.FirebaseInstanceId
//import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by andreyyoshuamanik on 9/17/17.
 */

//class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
//
//    override fun onTokenRefresh() {
//        super.onTokenRefresh()
//
//        // Get updated InstanceID token.
//        //        fw_wkgZAal0:APA91bF1Y6giaMxGs7BWTLfLQomIFZxugQXmkFUCqr-DYR3C54thNb4krhxEBEO9Us5-Rz9ECoJPFm7PMuHOiFL72ehtjg7uw8miIKOhp0Gd0KjKPUqTfz7p3OQGxfmUwIDRaOplJ89e
//        val refreshedToken = FirebaseInstanceId.getInstance().token
//        Log.i("FIREBASE INSTANCE ID", "Refreshed token: " + refreshedToken!!)
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken)
//    }
//
//    // TODO: Implement send token registration to server
//    internal fun sendRegistrationToServer(refreshToken: String) {
//        API.registerFirebaseToken(refreshToken, object : ResponseHandler<String> {
//            override fun response(success: Boolean, data: String?, errorMessage: String?) {
//
//            }
//
//        })
//    }
//}
