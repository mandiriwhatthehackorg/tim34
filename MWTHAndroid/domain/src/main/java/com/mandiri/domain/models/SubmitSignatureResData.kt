package com.mandiri.domain.models

data class SubmitSignatureResData(
    val `data`: SubmitSignatureResInnerData,
    val token: String,
    val viewName: String
)