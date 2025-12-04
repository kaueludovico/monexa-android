package br.com.rocket.monexa.data.dto

import br.com.rocket.monexa.domain.model.Currency

data class CurrencyDto(
    val code: String,
    val codein: String,
    val name: String,
    val bid: String,
    val ask: String,
    val timestamp: String
) {
    fun toCurrency(): Currency {
        return Currency(
            code = code,
            codein = codein,
            name = name,
            bid = bid.toDouble(),
            ask = ask.toDouble(),
            timestamp = timestamp.toLong()
        )
    }
}
