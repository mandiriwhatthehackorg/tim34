package com.mandiri.domain.models

data class CreateSessionRes(
    val `data`: CreateSessionResData?,
    val message: String,
    val success: Boolean
)