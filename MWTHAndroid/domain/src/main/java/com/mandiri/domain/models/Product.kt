package com.mandiri.domain.models

data class Product(
    val productCode: String,
    val productName: String
) {
    override fun toString(): String {
        return productName
    }
}