package com.mandiri.whatthehack.onboarding.pages


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.AudioCallActivity
import kotlinx.android.synthetic.main.fragment_kyctrigger.*

class KYCTriggerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kyctrigger, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callCenterButton.setOnClickListener {

        }

        kycButton.setOnClickListener {
            startActivityForResult(Intent(activity, AudioCallActivity::class.java), 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        findNavController().navigate(R.id.goToShipment)
    }

}
