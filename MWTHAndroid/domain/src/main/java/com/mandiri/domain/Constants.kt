package com.mandiri.domain

enum class Constants(var value: String?) {
    Token(null),
    KEY_TOKEN("token"),
    KEYCREF("cref"),
    KEY_STEP("step"),
    VALUE_STEP_OTP("otp"),
    VALUE_STEP_SUBMIT_DATA("submitData"),
    VALUE_STEP_KTP("ktp"),
    VALUE_STEP_SELFIE("selfie"),
    VALUE_STEP_SIGNATURE("signature"),
}