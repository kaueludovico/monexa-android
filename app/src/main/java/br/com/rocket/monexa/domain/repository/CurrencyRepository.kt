package br.com.rocket.monexa.domain.repository

import br.com.rocket.monexa.domain.model.Currency

interface CurrencyRepository {
    suspend fun getLatestRates(moedas: List<String>): List<Currency>
}