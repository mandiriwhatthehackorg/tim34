package com.mandiri.binding

import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout


@BindingAdapter("isLoading")
fun setShimmerStart(view: ShimmerFrameLayout, boolean: Boolean) {
    if (boolean) view.startShimmerAnimation()
    else view.stopShimmerAnimation()
}
