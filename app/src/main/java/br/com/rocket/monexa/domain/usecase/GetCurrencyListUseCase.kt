package br.com.rocket.monexa.domain.usecase

import br.com.rocket.monexa.domain.model.ExchangeRates
import br.com.rocket.monexa.domain.repository.CurrencyListRepository

class GetCurrencyListUseCase(private val repository: CurrencyListRepository) {
    suspend fun execute(base: String? = null): Result<ExchangeRates> {
        return repository.getLatestRates(base)
    }
}