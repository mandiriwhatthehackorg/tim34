package com.mandiri.domain.models

data class SubmitSignatureRes(
    val `data`: SubmitSignatureResData?,
    val message: String,
    val success: Boolean
)