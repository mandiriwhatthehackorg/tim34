package com.mandiri.binding

import androidx.databinding.BindingAdapter
import android.webkit.WebView


@BindingAdapter("url")
fun setListener(view: WebView, url: String?) {

    url ?: return
    view.settings.javaScriptEnabled = true
    view.loadUrl(url)
}
