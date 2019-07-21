package com.mandiri.domain.models

data class GetKYCDataResInnerData(
    val callRef: String,
    val productCardChoiceResponse: ProductCardChoiceResponse,
    val sessionData: SessionData,
    val sessionId: String
)