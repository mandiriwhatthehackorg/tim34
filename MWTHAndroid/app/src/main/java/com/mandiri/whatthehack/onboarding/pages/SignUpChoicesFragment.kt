package com.mandiri.whatthehack.onboarding.pages


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.mandiri.whatthehack.R
import kotlinx.android.synthetic.main.fragment_sign_up_choices.*

class SignUpChoicesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_choices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nasabahButton.setOnClickListener {
            findNavController().navigate(R.id.goToCreateSession)
        }

        nonNasabahButton.setOnClickListener {
            findNavController().navigate(R.id.goToCreateSession)
        }
    }

}
