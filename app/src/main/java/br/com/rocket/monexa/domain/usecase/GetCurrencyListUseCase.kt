package br.com.rocket.monexa.domain.usecase

import br.com.rocket.monexa.data.dto.CurrencyListResponse
import br.com.rocket.monexa.domain.model.CurrencySymbol
import br.com.rocket.monexa.domain.repository.CurrencyListRepository

class GetCurrencyListUseCase(private val repository: CurrencyListRepository) {
    suspend fun execute(): CurrencyListResponse = repository.getAvailableCurrencies()
}