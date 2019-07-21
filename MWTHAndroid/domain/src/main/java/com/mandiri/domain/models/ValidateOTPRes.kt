package com.mandiri.domain.models

data class ValidateOTPRes(
    val `data`: ValidateOTPResData?,
    val message: String,
    val success: Boolean
)