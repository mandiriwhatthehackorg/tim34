package com.mandiri.domain.models

data class SubmitKYCReq(
    val alamat: String,
    val branch: String,
    val callReff: String,
    val card: String,
    val dob: String,
    val ktp: String,
    val motherName: String,
    val nama: String,
    val nik: String,
    val phone: String,
    val product: String,
    val selfie: String,
    val ttd: String
)