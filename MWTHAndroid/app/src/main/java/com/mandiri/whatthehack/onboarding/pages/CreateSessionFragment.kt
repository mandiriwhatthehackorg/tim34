package com.mandiri.whatthehack.onboarding.pages


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mandiri.domain.Constants
import com.mandiri.domain.models.CreateSessionRes

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.databinding.FragmentCreateSessionBinding
import com.mandiri.whatthehack.onboarding.domain.SharedPrefs
import kotlinx.android.synthetic.main.fragment_create_session.*

class CreateSessionFragment : Fragment() {

    lateinit var dataBinding: FragmentCreateSessionBinding
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(OnboardingViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_session, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.viewModel = viewModel

        submitButton.setOnClickListener {
            val createSession = viewModel.createSession()
            createSession.observe(this, object: Observer<CreateSessionRes> {
                override fun onChanged(t: CreateSessionRes?) {
                    t?.let {
                        if (!it.success || it.data == null) {
                            Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                        } else {
                            SharedPrefs.set(activity!!, Constants.KEY_TOKEN.value!!, it.data!!.token)
                            SharedPrefs.set(activity!!, Constants.KEY_STEP.value!!, Constants.VALUE_STEP_OTP.value)
                            findNavController().navigate(R.id.goToOtp)
                        }
                    }
                    createSession.removeObserver(this)
                }

            })
        }

    }

}
