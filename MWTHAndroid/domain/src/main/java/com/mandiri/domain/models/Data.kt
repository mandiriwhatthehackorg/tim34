package com.mandiri.domain.models

data class GetKYCDataResData(
    val `data`: GetKYCDataResInnerData,
    val token: String,
    val viewName: String
)