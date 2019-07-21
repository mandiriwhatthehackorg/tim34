package com.mandiri.whatthehack.audiovideo.api

interface ResponseHandler<T> {
    fun response(success: Boolean, data: T?, errorMessage: String?)
}
