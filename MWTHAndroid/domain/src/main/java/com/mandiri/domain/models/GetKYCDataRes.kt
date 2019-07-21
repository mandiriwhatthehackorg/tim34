package com.mandiri.domain.models

data class GetKYCDataRes(
    val `data`: GetKYCDataResData,
    val message: String,
    val success: Boolean
)