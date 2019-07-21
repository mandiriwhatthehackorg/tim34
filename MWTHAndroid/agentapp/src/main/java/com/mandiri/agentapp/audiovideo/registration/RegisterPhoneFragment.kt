package com.mandiri.agentapp.audiovideo.registration


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.mandiri.agentapp.R
import com.mandiri.agentapp.audiovideo.api.API
import com.mandiri.agentapp.audiovideo.api.ResponseHandler
import com.mandiri.agentapp.audiovideo.model.CountryCode
import com.mandiri.agentapp.audiovideo.model.RegisterResponse
import com.mandiri.agentapp.audiovideo.other.Util
import com.google.gson.Gson

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Arrays

import android.app.Activity.RESULT_OK
import com.mandiri.agentapp.audiovideo.registration.CountryCodeActivity
import kotlinx.android.synthetic.main.fragment_register_phone.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterPhoneFragment : Fragment() {

    internal var listener: OnRegisterPhoneListener? = null
    internal var codes: Array<CountryCode>? = null
    private val countryCodeTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            val codeInputted = charSequence.toString()
            if (codeInputted == "") {
                countryCodeButton.text = "Invalid Code"
                return
            }
            var found = false
            for (code in codes!!) {
                if (code.dial_code != null && code.dial_code?.startsWith("+" + codeInputted) == true) {
                    countryCodeButton.text = code.name
                    found = true
                    break
                }
            }
            if (!found) {
                countryCodeButton.text = "Invalid Code"
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    interface OnRegisterPhoneListener {
        fun onRegister(response: RegisterResponse)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_register_phone, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var bufferedReader: BufferedReader? = null
        try {
            val json = activity!!.assets.open("country_code.json")
            bufferedReader = BufferedReader(InputStreamReader(json, "UTF-8"))
            var str: String = bufferedReader.readText();

            codes = Gson().fromJson(str, Array<CountryCode>::class.java)
            Arrays.sort(codes!!)
            bufferedReader.close()

        } catch (e: IOException) {
            //log the exception
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: IOException) {
                    //log the exception
                }

            }
            if (codes != null) {
                countryCodeButton.text = codes!![0].name
                countryCodeEditText.setText(codes!![0].dial_code?.replace("+", ""))
                countryCodeEditText.addTextChangedListener(countryCodeTextWatcher)
            }
        }


        countryCodeButton.setOnClickListener { selectCountryCode() }
        okButton.setOnClickListener { ok() }

    }

    fun selectCountryCode() {
        val intent = Intent(activity, CountryCodeActivity::class.java)
        startActivityForResult(intent, REQUEST_COUNTRY_CODE)
    }

    fun ok() {
        val phone = countryCodeEditText.text.toString() + phoneNumberEditText!!.text.toString()
        if (TextUtils.isEmpty(phoneNumberEditText!!.text.toString())) {
            Toast.makeText(activity, "Please input your phone number", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = Util.showProgressDialogWithTitle("Processing...", activity!!)
        API.registerPhone(phone, object : ResponseHandler<RegisterResponse> {
            override fun response(success: Boolean, data: RegisterResponse?, errorMessage: String?) {
                if (success) {
                    listener?.onRegister(data!!)
                } else {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_COUNTRY_CODE && resultCode == RESULT_OK) {
            val selected = data!!.getSerializableExtra(CountryCodeActivity.COUNTRY_CODE) as CountryCode

            countryCodeEditText!!.removeTextChangedListener(countryCodeTextWatcher)
            countryCodeButton!!.text = selected.name
            countryCodeEditText!!.setText(selected.dial_code?.replace("+", ""))
            countryCodeEditText!!.addTextChangedListener(countryCodeTextWatcher)
        }
    }

    companion object {

        private val REQUEST_COUNTRY_CODE = 100

        fun getInstance(listener: OnRegisterPhoneListener): RegisterPhoneFragment {
            val fragment = RegisterPhoneFragment()
            fragment.listener = listener

            return fragment
        }
    }
}// Required empty public constructor
