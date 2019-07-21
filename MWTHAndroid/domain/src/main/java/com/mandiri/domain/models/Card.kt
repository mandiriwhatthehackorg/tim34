package com.mandiri.domain.models

data class Card(
    val cardCode: String,
    val cardName: String
) {
    override fun toString(): String {
        return cardName
    }
}