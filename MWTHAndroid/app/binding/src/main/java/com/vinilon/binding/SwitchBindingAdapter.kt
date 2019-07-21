package com.mandiri.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.widget.Switch


@BindingAdapter("realValueAttrChanged")
fun setListener(checkBox: Switch, listener: InverseBindingListener?) {
    if (listener != null) {
        checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
            listener.onChange()
        }
    }
}

@BindingAdapter("realValue")
fun setRealValue(view: Switch, value: Boolean?) {
    if (view.isChecked != value) {
        view.isChecked = value ?: false
    }
}


@InverseBindingAdapter(attribute = "realValue")
fun getRealValue(checkBox: Switch): Boolean {
    return checkBox.isChecked
}