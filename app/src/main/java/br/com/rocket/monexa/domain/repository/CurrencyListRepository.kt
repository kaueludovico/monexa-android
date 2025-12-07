package br.com.rocket.monexa.domain.repository

import br.com.rocket.monexa.data.dto.CurrencyListResponse

interface CurrencyListRepository {
    suspend fun getAvailableCurrencies(): CurrencyListResponse
}