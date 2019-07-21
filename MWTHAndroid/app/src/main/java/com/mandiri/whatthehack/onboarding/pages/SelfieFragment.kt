package com.mandiri.whatthehack.onboarding.pages


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mandiri.domain.Constants
import com.mandiri.domain.models.CreateSessionRes

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.onboarding.domain.SharedPrefs
import com.mandiri.whatthehack.onboarding.domain.ZhihuImagePicker
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_ktp.*
import kotlinx.android.synthetic.main.fragment_ktp.imageView
import kotlinx.android.synthetic.main.fragment_ktp.submitButton
import kotlinx.android.synthetic.main.fragment_selfie.*

class SelfieFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selfie, container, false)
    }

    val rxPermission by lazy {
        RxPermissions(this)
    }
    val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(OnboardingViewModel::class.java)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val disposable = rxPermission.request(android.Manifest.permission.CAMERA)
            .subscribe { granted ->
                if (granted) {
                    selfieButton.setOnClickListener {

                        val rxImagePicker: ZhihuImagePicker = RxImagePicker
                            .create(ZhihuImagePicker::class.java)

                        rxImagePicker.openCamera(this.activity!!)
                            .subscribe {

                                Glide.with(this)
                                    .asBitmap()
                                    .load(it.uri)
                                    .into(object : CustomTarget<Bitmap>(400, 400){
                                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                            viewModel.selfieImage.set(resource)
                                        }
                                        override fun onLoadCleared(placeholder: Drawable?) {
                                            // this is called when imageView is cleared on lifecycle call or for
                                            // some other reason.
                                            // if you are referencing the bitmap somewhere else too other than this imageView
                                            // clear it here as you can no longer have the bitmap
                                        }
                                    })

                                Glide.with(this.activity!!)
                                    .load(it.uri)
                                    .into(imageView)
                            }
                    }
                }
            }

        submitButton.setOnClickListener {
            val submitSelfie = viewModel.submitSelfie()
            submitSelfie.observe(this, object: Observer<CreateSessionRes> {
                override fun onChanged(t: CreateSessionRes?) {
                    t?.let {
                        if (!it.success || it.data == null) {
                            Toast.makeText(activity!!, it.message, Toast.LENGTH_SHORT).show()
                        } else {
                            SharedPrefs.set(activity!!, Constants.KEY_TOKEN.value!!, it.data?.token)
                            SharedPrefs.set(activity!!, Constants.KEY_STEP.value!!, Constants.VALUE_STEP_SIGNATURE.value)
                            findNavController().navigate(R.id.goToSignature)
                        }
                    }
                    submitSelfie.removeObserver(this)
                }

            })
        }
    }


}
