package com.mandiri.whatthehack.onboarding.pages


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mandiri.whatthehack.R
import android.os.Build
import android.annotation.TargetApi
import android.content.Intent
import com.an.biometric.BiometricCallback
import com.an.biometric.BiometricManager
import com.mandiri.whatthehack.main.DashboardActivity
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            startActivity(Intent(activity, DashboardActivity::class.java))
        }

        fingerprintButton.setOnClickListener {
            displayBiometricPrompt(object: BiometricCallback {
                override fun onSdkVersionNotSupported() {
                    
                }

                override fun onBiometricAuthenticationPermissionNotGranted() {
                    
                }

                override fun onAuthenticationCancelled() {
                    
                }

                override fun onBiometricAuthenticationInternalError(error: String?) {
                    
                }

                override fun onBiometricAuthenticationNotSupported() {
                    
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    
                }

                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                    
                }

                override fun onAuthenticationSuccessful() {
                    startActivity(Intent(activity, DashboardActivity::class.java))
                }

                override fun onAuthenticationFailed() {
                    
                }

                override fun onBiometricAuthenticationNotAvailable() {
                    
                }

            })
        }
    }
    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricPrompt(biometricCallback: BiometricCallback) {
        BiometricManager.BiometricBuilder(this.activity)
            .setTitle("Gunakan sidik jarimu")
            .setSubtitle("Untuk Login")
            .setDescription("Supaya kamu terotentikasi")
            .setNegativeButtonText("Gausah Deh")
            .build()
            .authenticate(biometricCallback)
    }

}
