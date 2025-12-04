package br.com.rocket.monexa.data.repository

import br.com.rocket.monexa.data.datasource.CurrencyApi
import br.com.rocket.monexa.domain.model.Currency
import br.com.rocket.monexa.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(private val api: CurrencyApi) : CurrencyRepository {

    override suspend fun getLatestRates(moedas: List<String>): Map<String, Currency> {
        val response = api.getLatestRates(moedas.joinToString(","))
        return response.mapValues { it.value.toCurrency() }
    }

    override suspend fun getHistoricalRates(
        moeda: String,
        startDate: String,
        endDate: String
    ): List<Currency> {
        val response = api.getHistoricalRates(moeda, startDate, endDate)
        return response.map { it.toCurrency() }
    }
}