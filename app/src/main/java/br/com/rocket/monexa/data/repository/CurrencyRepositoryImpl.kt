package br.com.rocket.monexa.data.repository

import br.com.rocket.monexa.data.datasource.CurrencyApi
import br.com.rocket.monexa.data.dto.CurrencyResponse
import br.com.rocket.monexa.domain.model.Currency
import br.com.rocket.monexa.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(private val api: CurrencyApi) : CurrencyRepository {

    override suspend fun getLatestRates(moedas: List<String>): List<Currency> {
        val response = api.getLatestRates(moedas.joinToString(","))
        return response.currency.map { it.toCurrency() }
    }
}