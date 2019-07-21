package com.mandiri.domain.models

data class ValidateOTPResData(
    val `data`: ValidateOTPResInnerData,
    val token: String,
    val viewName: String
)