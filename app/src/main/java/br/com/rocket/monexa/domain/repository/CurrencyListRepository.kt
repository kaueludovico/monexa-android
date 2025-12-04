package br.com.rocket.monexa.domain.repository

import br.com.rocket.monexa.domain.model.CurrencySymbol

interface CurrencyListRepository {
    suspend fun getAvailableCurrencies(): List<CurrencySymbol>
}