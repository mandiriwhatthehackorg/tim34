package com.mandiri.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.widget.RatingBar


@BindingAdapter("realValueAttrChanged")
fun setListener(spinner: RatingBar, listener: InverseBindingListener?) {
    if (listener != null) {
        spinner.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser -> listener.onChange() }
    }
}

@BindingAdapter("realValue")
fun setRealValue(view: RatingBar, value: Any?) {
    if (view.rating != value) {
        for (i in 0 until view.numStars) {
            if (i == value) {
                view.rating = i.toFloat()
                break
            }
        }
    }
}

@InverseBindingAdapter(attribute = "realValue")
fun getRealValue(spinner: RatingBar): Any? {
    return spinner.rating
}

