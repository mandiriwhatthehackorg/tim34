package com.mandiri.whatthehack.onboarding.pages


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mandiri.domain.Constants
import com.mandiri.domain.models.CreateSessionRes
import com.mandiri.domain.models.SubmitSignatureRes

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.onboarding.domain.SharedPrefs
import kotlinx.android.synthetic.main.fragment_signature.*

class SignatureFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signature, container, false)
    }

    val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(OnboardingViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitButton.setOnClickListener {
            viewModel.signatureImage.set(signature_pad.signatureBitmap)

            val submitSignature = viewModel.submitSignature()
            submitSignature.observe(this, object: Observer<SubmitSignatureRes> {
                override fun onChanged(t: SubmitSignatureRes?) {
                    t?.let {
                        if (!it.success || it.data == null) {
                            Toast.makeText(activity!!, it.message, Toast.LENGTH_SHORT).show()
                        } else {
                            SharedPrefs.set(activity!!, Constants.KEY_TOKEN.value!!, it.data?.token)
                            SharedPrefs.set(activity!!, Constants.KEYCREF.value!!, it.data?.data?.callRef)
                            SharedPrefs.set(activity!!, Constants.KEY_STEP.value!!, Constants.VALUE_STEP_SIGNATURE.value)
                            findNavController().navigate(R.id.goToKyc)
                        }
                    }
                    submitSignature.removeObserver(this)
                }

            })
        }
    }
}
