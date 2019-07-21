package com.mandiri.whatthehack.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.mandiri.domain.Constants
import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.onboarding.domain.SharedPrefs

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }

    override fun onResume() {
        super.onResume()


//        val currentToken = SharedPrefs.get(this, Constants.KEY_TOKEN.value, null)
//        Constants.Token.value = currentToken
//
//        val currentStep = SharedPrefs.get(this, Constants.KEY_STEP.value, null)
//        when (currentStep) {
//            Constants.VALUE_STEP_OTP.value -> {
//                findNavController(R.id.nav_host_fragment).navigate(R.id.goToOtp)
//            }
//            Constants.VALUE_STEP_SUBMIT_DATA.value -> {
//                findNavController(R.id.nav_host_fragment).navigate(R.id.goToInputData)
//            }
//            Constants.VALUE_STEP_KTP.value -> {
//                findNavController(R.id.nav_host_fragment).navigate(R.id.goToKtp)
//            }
//            Constants.VALUE_STEP_SELFIE.value -> {
//                findNavController(R.id.nav_host_fragment).navigate(R.id.goToSelfie)
//            }
//            Constants.VALUE_STEP_SIGNATURE.value -> {
//                findNavController(R.id.nav_host_fragment).navigate(R.id.goToSignature)
//            }
//        }
    }
}
