package br.com.rocket.monexa.domain.repository

import br.com.rocket.monexa.domain.model.ExchangeRates

interface CurrencyListRepository {
    suspend fun getLatestRates(base: String? = null): Result<ExchangeRates>
}