package com.mandiri.agentapp.audiovideo.api

interface ResponseHandler<T> {
    fun response(success: Boolean, data: T?, errorMessage: String?)
}
