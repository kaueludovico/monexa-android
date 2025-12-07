package br.com.rocket.monexa.data.repository

import br.com.rocket.monexa.data.datasource.CurrencyApi
import br.com.rocket.monexa.data.mapper.toExchangeRates
import br.com.rocket.monexa.domain.model.ExchangeRates
import br.com.rocket.monexa.domain.repository.CurrencyListRepository

class CurrencyListRepositoryImpl(
    private val api: CurrencyApi
) : CurrencyListRepository {

    override suspend fun getLatestRates(base: String?): Result<ExchangeRates> {
        return try {
            val baseCurrency = base ?: "USD"
            val response = api.getLatestRates(baseCurrency)

            if (response.result == "success") {
                Result.success(response.toExchangeRates())
            } else {
                Result.failure(Exception("API returned result: ${response.result}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}