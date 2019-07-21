package com.mandiri.agentapp.audiovideo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

import com.mandiri.agentapp.audiovideo.other.ChatApplication
import com.mandiri.agentapp.audiovideo.service.SocketIOService
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mandiri.agentapp.R
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_audio_call.*

import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Capturer
import org.webrtc.CameraEnumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import org.webrtc.VideoRenderer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack

import java.util.ArrayList

class AudioCallActivity : AppCompatActivity(), View.OnClickListener {

    lateinit internal var peerConnectionFactory: PeerConnectionFactory
    internal var audioConstraints: MediaConstraints = MediaConstraints()
    internal var videoConstraints: MediaConstraints = MediaConstraints()
    internal var sdpConstraints: MediaConstraints = MediaConstraints()
    internal var videoSource: VideoSource? = null
    internal var localVideoTrack: VideoTrack? = null
    internal var audioSource: AudioSource? = null
    internal var localAudioTrack: AudioTrack? = null

    internal var localRenderer: VideoRenderer? = null
    internal var remoteRenderer: VideoRenderer? = null

    internal var peerConn: PeerConnection? = null
    internal var dataChannel: DataChannel? = null
    internal var isInitiator = false
    private var to: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_call)

        to = intent.getStringExtra(DESTINATION_CALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
        } else {
            // Start Socket service
            // The service will be connecting the socket

            initViews()
            initVideos()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Start Socket service
                // The service will be connecting the socket
                startService(Intent(this, SocketIOService::class.java))

                initViews()
                initVideos()
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                Toast.makeText(this, "Anda tidak bisa mengambil foto", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Register for bus
        ChatApplication.bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        ChatApplication.bus.unregister(this)
    }


    override fun onDestroy() {
        super.onDestroy()

    }

    private fun initViews() {
        startButton.setOnClickListener(this)
        callButton.setOnClickListener(this)
        hangupButton.setOnClickListener(this)
    }

    private fun initVideos() {
        val rootEglBase = EglBase.create()
        localVideoView.init(rootEglBase.eglBaseContext, null)
        remoteVideoView.init(rootEglBase.eglBaseContext, null)
        localVideoView.setZOrderMediaOverlay(true)
        remoteVideoView.setZOrderMediaOverlay(true)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.startButton -> {
                start()
            }
            R.id.callButton -> {
                call()
            }
            R.id.hangupButton -> {
                hangup()
            }
        }
    }


    fun start() {
        startButton.isEnabled = false
        callButton.isEnabled = true
        //Initialize PeerConnectionFactory globals.
        //Params are context, initAudio,initVideo and videoCodecHwAcceleration
        PeerConnectionFactory.initializeAndroidGlobals(this, true)

        //Create a new PeerConnectionFactory instance.
        val options = PeerConnectionFactory.Options()
        peerConnectionFactory = PeerConnectionFactory(options)


        //Now create a VideoCapturer instance. Callback methods are there if you want to do something! Duh!
        val videoCapturerAndroid = createVideoCapturer()//getVideoCapturer(new CustomCameraEventsHandler());

        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = MediaConstraints()
        videoConstraints = MediaConstraints()

        //Create a VideoSource instance
        videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid!!)
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)

        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
        localAudioTrack.let {
            it!!.setEnabled(true)
            it.setVolume(1.0)
        }
        localVideoView.visibility = View.VISIBLE
        localVideoView.setMirror(true)

        //we will start capturing the video from the camera
        //width,height and fps
        videoCapturerAndroid.startCapture(1000, 1000, 30)

        //create a videoRenderer based on SurfaceViewRenderer instance
        localRenderer = VideoRenderer(localVideoView)
        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.
        localVideoTrack.let { it!!.addRenderer(localRenderer) }

    }


    @Subscribe
    fun getSocketEvent(event: String) {
        val iceServers = ArrayList<PeerConnection.IceServer>()
        when (event) {
            SocketEvent.ROOM_CREATED -> {
                isInitiator = true
                start()
            }
            SocketEvent.JOINED_ROOM -> {
                isInitiator = false
                start()
            }
            SocketEvent.ROOM_FULL -> {
            }
            SocketEvent.ROOM_READY -> createPeerConnection(isInitiator, PeerConnection.RTCConfiguration(iceServers))
            Socket.EVENT_CONNECT -> ChatApplication.bus.post(SocketEvent(SocketEvent.CREATE_OR_JOIN_ROOM, to))
            else -> {
            }
        }
    }

    private fun call() {
        startButton.isEnabled = false
        callButton.isEnabled = false
        hangupButton.isEnabled = true

        //we already have video and audio tracks. Now create peerconnections
        val iceServers = ArrayList<PeerConnection.IceServer>()

        createPeerConnection(true, PeerConnection.RTCConfiguration(iceServers))

    }


    private fun hangup() {
        peerConn!!.close()
        peerConn = null
        startButton.isEnabled = true
        callButton.isEnabled = false
        hangupButton.isEnabled = false
    }

    private fun gotRemoteStream(stream: MediaStream) {
        //we have remote video stream. add to the renderer.
        val videoTrack = stream.videoTracks.first
        val audioTrack = stream.audioTracks.first
        runOnUiThread {
            try {
                remoteRenderer = VideoRenderer(remoteVideoView)
                remoteVideoView.visibility = View.VISIBLE
                videoTrack.addRenderer(remoteRenderer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun onIceCandidateReceived(peer: PeerConnection, iceCandidate: IceCandidate) {
        //we have received ice candidate. We can set it to the other peer.
        //        if (peer == localPeer) {
        //            remotePeer.addIceCandidate(iceCandidate);
        //        } else {
        //            localPeer.addIceCandidate(iceCandidate);
        //        }
        peer.addIceCandidate(iceCandidate)
    }


    private fun createPeerConnection(isInitiator: Boolean, configuration: PeerConnection.RTCConfiguration) {
        Log.i("INFO", "Creating Peer connection as initiator? " + isInitiator + "config: " + configuration)

        //create sdpConstraints
        sdpConstraints = MediaConstraints()
        if (isInitiator) {
            sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"))
            sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"))
        }

        peerConn = peerConnectionFactory.createPeerConnection(configuration, sdpConstraints, object : CustomPeerConnectionObserver("local peer connection") {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                super.onIceCandidate(iceCandidate)

                // send any ice candidates to the other peer
                iceCandidate.let {
                    Log.i("INFO", "icecandidate event: " + it!!)

                    val jsonObject = Gson().fromJson(Gson().toJson(it), JsonObject::class.java)
                    jsonObject.addProperty("type", "candidate")
                    jsonObject.addProperty("to", to)
                    ChatApplication.bus.post(SocketEvent(SocketEvent.MESSAGE, jsonObject))
                }

                Log.i("INFO", "End of candidates")
            }

            override fun onAddStream(mediaStream: MediaStream) {
                super.onAddStream(mediaStream)

                Log.i("INFO", "Get Remote Stream")
                gotRemoteStream(mediaStream)
            }
        })

        //creating local mediastream
        val stream = peerConnectionFactory.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        peerConn!!.addStream(stream)


        if (isInitiator) {
            Log.i("INFO", "Creating Data Channel")
            val init = DataChannel.Init()

            dataChannel = peerConn!!.createDataChannel("", init)
            dataChannel.let {
                it!!.registerObserver(object : DataChannel.Observer {
                override fun onBufferedAmountChange(l: Long) {

                }

                override fun onStateChange() {

                }

                override fun onMessage(buffer: DataChannel.Buffer) {

                }
            })
            }

            Log.i("INFO", "Creating an offer")
            peerConn!!.createOffer(object : CustomSdpObserver("create offer") {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    onLocalDescriptionCreated(sessionDescription)
                }
            }, sdpConstraints)
        }
    }

    private fun onLocalDescriptionCreated(desc: SessionDescription) {

        peerConn!!.setLocalDescription(object : CustomSdpObserver("set local desc") {

            override fun onSetSuccess() {
                super.onSetSuccess()
                // Send to socket this local description
                val jsonObject = Gson().fromJson(Gson().toJson(desc), JsonObject::class.java)
                jsonObject.addProperty("to", to)

                ChatApplication.bus.post(SocketEvent(SocketEvent.MESSAGE, jsonObject))
            }
        }, desc)
    }

    @Subscribe
    fun signalingMessageCallback(`object`: JsonObject) {
        if (`object`.get("type").asString.toLowerCase() == "offer") {
            Log.i("INFO", "Got offer. Sending answer to peer.")
            val sessionDescription = Gson().fromJson(`object`, SessionDescription::class.java)
            peerConn!!.setRemoteDescription(CustomSdpObserver("get remote desc"), sessionDescription)
            peerConn!!.createAnswer(object : CustomSdpObserver("remote create answer") {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    super.onCreateSuccess(sessionDescription)

                    onLocalDescriptionCreated(sessionDescription)
                }
            }, sdpConstraints)

        } else if (`object`.get("type").asString.toLowerCase() == "answer") {
            Log.i("INFO", "Got answer.")
            val sessionDescription = Gson().fromJson(`object`, SessionDescription::class.java)
            peerConn!!.setRemoteDescription(CustomSdpObserver("get remote desc"), sessionDescription)

        } else if (`object`.get("type").asString.toLowerCase() == "candidate") {
            val iceCandidate = Gson().fromJson(`object`, IceCandidate::class.java)
            peerConn!!.addIceCandidate(iceCandidate)

        } else if (`object`.get("type").asString.toLowerCase() == "bye") {
            // TODO: cleanup RTC connection?
            hangup()
        }
    }

    private fun createVideoCapturer(): VideoCapturer? {
        val videoCapturer: VideoCapturer?
        videoCapturer = createCameraCapturer(Camera1Enumerator(false))
        return videoCapturer
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        // Trying to find a front facing camera!
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // We were not able to find a front cam. Look for other cameras
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        return null
    }

    // Cycle through likely device names for the camera and return the first
    // capturer that works, or crash if none do.
    private fun getVideoCapturer(eventsHandler: CameraVideoCapturer.CameraEventsHandler): VideoCapturer {
        val cameraFacing = arrayOf("front", "back")
        val cameraIndex = intArrayOf(0, 1)
        val cameraOrientation = intArrayOf(0, 90, 180, 270)
        for (facing in cameraFacing) {
            for (index in cameraIndex) {
                for (orientation in cameraOrientation) {
                    val name = "Camera " + index + ", Facing " + facing +
                            ", Orientation " + orientation
                    try {

                        val capturer = Camera2Capturer(this, name, eventsHandler)
                        if (capturer != null) {
                            Log.d("Using camera: ", name)
                            return capturer
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }
            }
        }
        throw RuntimeException("Failed to open capture")
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }

    companion object {

        val DESTINATION_CALL = "destinationCall"

        private val REQUEST_CAMERA_PERMISSION = 100
    }


}
