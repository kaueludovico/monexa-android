package br.com.rocket.monexa.data.dto

import br.com.rocket.monexa.domain.model.CurrencySymbol

data class CurrencySymbolDto(
    val simbolo: String,
    val nome: String,
    val tipo_moeda: String
) {
    fun toCurrencySymbol(): CurrencySymbol = CurrencySymbol(simbolo, nome)
}
