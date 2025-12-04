package br.com.rocket.monexa.data.repository

import br.com.rocket.monexa.data.datasource.CurrencyListApi
import br.com.rocket.monexa.domain.model.CurrencySymbol
import br.com.rocket.monexa.domain.repository.CurrencyListRepository

class CurrencyListRepositoryImpl(private val api: CurrencyListApi) : CurrencyListRepository {
    override suspend fun getAvailableCurrencies(): List<CurrencySymbol> {
        return api.getCurrencies().map { it.toCurrencySymbol() }
    }
}