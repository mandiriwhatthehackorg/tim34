package com.mandiri.agentapp.audiovideo.registration


import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
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
import com.mandiri.agentapp.audiovideo.model.User
import com.mandiri.agentapp.audiovideo.other.Util
//import com.google.firebase.iid.FirebaseInstanceId

import kotlinx.android.synthetic.main.fragment_verify_code.*

/**
 * A simple [Fragment] subclass.
 */
class VerifyCodeFragment : Fragment() {

    internal var listener: OnVerifyListener? = null

    public interface OnVerifyListener {
        fun onVerifyResponse(user: User?)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_verify_code, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verifyButton.isEnabled = charSequence.length == 6
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verifyButton.isEnabled = charSequence.length == 6
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        verifyButton.setOnClickListener { sendCode() }
        resendButton.setOnClickListener { askToResendCode() }
    }


    internal fun sendCode() {

        val code = codeEditText.text.toString()

        val dialog = Util.showProgressDialogWithTitle("Processing...", activity!!)

        // Verifying code inputted,
        // If success send token
        API.verifyCode(code, object : ResponseHandler<User> {
            override fun response(success: Boolean, data: User?, errorMessage: String?) {
                if (success) {

//                    val token = FirebaseInstanceId.getInstance().token
//                    if (token != null) {
//                        API.registerFirebaseToken(token, object : ResponseHandler<String> {
//                            override fun response(success: Boolean, string: String?, errorMessage: String?) {
//                                if (success) {
//                                    listener?.onVerifyResponse(data)
//                                }
//                            }
//
//                        })
//                    } else {
                        Toast.makeText(activity, "An internal error occured", Toast.LENGTH_SHORT).show()
                        listener?.onVerifyResponse(data)
//                    }
                } else {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

        })
    }

    internal fun askToResendCode() {

    }

    companion object {

        fun getInstance(listener: OnVerifyListener): VerifyCodeFragment {
            val fragment = VerifyCodeFragment()
            fragment.listener = listener
            return fragment
        }
    }
}// Required empty public constructor
