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
import com.mandiri.whatthehack.audiovideo.registration.RegistrationActivity
import kotlinx.android.synthetic.main.fragment_on_boarding.*

class OnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener { findNavController().navigate(R.id.goToLogin) }
        signUpButton.setOnClickListener { findNavController().navigate(R.id.goToSignUp) }
    }

}
