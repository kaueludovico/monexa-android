package br.com.rocket.monexa.data.repository

import br.com.rocket.monexa.BuildConfig
import br.com.rocket.monexa.data.datasource.CurrencyApi
import br.com.rocket.monexa.data.dto.CurrencyListResponse
import br.com.rocket.monexa.domain.repository.CurrencyListRepository

class CurrencyListRepositoryImpl(private val api: CurrencyApi) : CurrencyListRepository {
    override suspend fun getAvailableCurrencies(): CurrencyListResponse {
        return api.getCurrencies(BuildConfig.API_TOKEN)
    }
}