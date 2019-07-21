package com.mandiri.binding

import androidx.databinding.BindingAdapter


@BindingAdapter("onRefreshListener")
fun setRefreshListener(view: androidx.swiperefreshlayout.widget.SwipeRefreshLayout, value: androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener?) {
    view.setOnRefreshListener(value)

}

@BindingAdapter("refreshing")
fun setRefreshing(view: androidx.swiperefreshlayout.widget.SwipeRefreshLayout, value: Boolean?) {
    view.isRefreshing = value ?: false

}