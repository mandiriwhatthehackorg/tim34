package com.mandiri.domain.models

data class ValidateOTPResInnerData(
    val branchList: List<Branch>,
    val cardList: List<Card>,
    val customerData: CustomerData,
    val productList: List<Product>
)