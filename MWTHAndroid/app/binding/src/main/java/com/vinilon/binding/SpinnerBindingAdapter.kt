package com.mandiri.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner


@BindingAdapter("realValueAttrChanged")
fun setListener(spinner: Spinner, listener: InverseBindingListener?) {
    if (listener != null) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onChange()
            }
        }
    }
}

@BindingAdapter("realValue")
fun setRealValue(view: Spinner, value: Any?) {
    if (view.selectedItem != value) {
        for (i in 0 until (view.adapter?.count ?: 0)) {
            if (view.adapter.getItem(i) == value) {
                view.setSelection(i)
                break
            }
        }
    }
}

@InverseBindingAdapter(attribute = "realValue")
fun getRealValue(spinner: Spinner): Any? {
    return spinner.selectedItem
}

