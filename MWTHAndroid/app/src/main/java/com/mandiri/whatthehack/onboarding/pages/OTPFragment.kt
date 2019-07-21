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
import com.mandiri.domain.models.ValidateOTPRes

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.databinding.FragmentOtBinding
import com.mandiri.whatthehack.onboarding.domain.SharedPrefs
import kotlinx.android.synthetic.main.fragment_ot.*

class OTPFragment : Fragment() {

    lateinit var dataBinding: FragmentOtBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ot, container, false)
        return dataBinding.root
    }

    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(OnboardingViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.viewModel = viewModel

        resendButton.setOnClickListener {
            val resendOtp = viewModel.resendOtp()
            resendOtp.observe(this, object : Observer<CreateSessionRes> {
                override fun onChanged(t: CreateSessionRes?) {
                    t?.let {
                        Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    }
                    resendOtp.removeObserver(this)
                }

            })
        }

        submitButton.setOnClickListener {

            val createSession = viewModel.validateOtp()
            createSession.observe(this, object: Observer<ValidateOTPRes> {
                override fun onChanged(t: ValidateOTPRes?) {
                    t?.let {
                        if (!t.success || it.data == null) {
                            Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                        } else {
                            SharedPrefs.set(activity!!, Constants.KEY_TOKEN.value!!, it.data?.token)
                            SharedPrefs.set(activity!!, Constants.KEY_STEP.value!!, Constants.VALUE_STEP_OTP.value)

                            viewModel.branchOptions.set(it.data?.data?.branchList)
                            viewModel.productTypeOptions.set(it.data?.data?.productList)
                            viewModel.cardListOptions.set(it.data?.data?.cardList)

                            findNavController().navigate(R.id.goToInputData)
                        }
                    }
                    createSession.removeObserver(this)
                }

            })
        }
    }

}
