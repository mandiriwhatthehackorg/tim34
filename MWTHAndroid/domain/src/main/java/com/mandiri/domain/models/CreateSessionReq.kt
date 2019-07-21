package com.mandiri.domain.models

data class CreateSessionReq(
    val email: String,
    val nik: String,
    val phone: String,
    val ttl: String
)