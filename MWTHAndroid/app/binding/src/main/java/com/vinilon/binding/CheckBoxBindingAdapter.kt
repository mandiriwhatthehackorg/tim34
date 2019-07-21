package com.mandiri.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.widget.CheckBox


@BindingAdapter("realValueAttrChanged")
fun setListener(checkBox: CheckBox, listener: InverseBindingListener?) {
    if (listener != null) {
        checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
            listener.onChange()
        }
    }
}

@BindingAdapter("realValue")
fun setRealValue(view: CheckBox, value: Boolean?) {
    if (view.isChecked != value) {
        view.isChecked = value ?: false
    }
}


@InverseBindingAdapter(attribute = "realValue")
fun getRealValue(checkBox: CheckBox): Boolean {
    return checkBox.isChecked
}