package com.mandiri.binding

import androidx.databinding.BindingAdapter
import androidx.cardview.widget.CardView

@BindingAdapter("clipToOutline")
fun clipToOutline(cardView: CardView, value: Boolean) {
    cardView.clipToOutline = value
}