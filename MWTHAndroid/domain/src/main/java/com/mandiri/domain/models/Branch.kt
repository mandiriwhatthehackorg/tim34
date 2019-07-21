package com.mandiri.domain.models

data class Branch(
    val branchAddress: String,
    val branchCode: String,
    val branchName: String
) {
    override fun toString(): String {
        return branchName
    }
}