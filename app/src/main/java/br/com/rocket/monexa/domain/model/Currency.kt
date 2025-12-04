package br.com.rocket.monexa.domain.model

data class Currency(
    val code: String,
    val codein: String,
    val name: String,
    val bid: Double,
    val ask: Double,
    val timestamp: Long
)
