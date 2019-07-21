package com.mandiri.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.widget.TextView


@BindingAdapter("realValueAttrChanged")
fun setListener(TextView: TextView, listener: InverseBindingListener?) {
    if (listener != null) {
        TextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                listener.onChange()
            }
        })
    }
}

@BindingAdapter("realValue")
fun setRealValue(view: TextView, value: String?) {
    if (view.text.toString() != value) {
        view.setText(value)
    }
}

@InverseBindingAdapter(attribute = "realValue")
fun getRealValue(TextView: TextView): String {
    return TextView.text.toString()
}


@BindingAdapter("movementMethod")
fun setRealValue(view: TextView, movementMethod: LinkMovementMethod) {
    view.movementMethod = movementMethod
}