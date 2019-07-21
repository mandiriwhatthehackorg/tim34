package com.mandiri.binding

import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter


@BindingAdapter("font")
fun setFont(button: Button, fontResId: Int) {
    val typeface = ResourcesCompat.getFont(button.context, fontResId)
    button.typeface = typeface
}