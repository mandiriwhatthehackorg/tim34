package com.mandiri.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


@BindingAdapter("realValueAttrChanged")
fun setListener(editText: EditText, listener: InverseBindingListener?) {
    if (listener != null) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                listener.onChange()
            }
        })
    }
}

@BindingAdapter("realValue")
fun setRealValue(view: EditText, value: String?) {
    if (view.text.toString() != value) {
        view.setText(value)
    }
}


@InverseBindingAdapter(attribute = "realValue")
fun getRealValue(editText: EditText): String {
    return editText.text.toString()
}