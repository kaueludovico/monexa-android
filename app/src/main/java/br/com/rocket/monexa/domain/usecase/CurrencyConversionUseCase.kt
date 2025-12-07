package br.com.rocket.monexa.domain.usecase

import br.com.rocket.monexa.domain.repository.CurrencyRepository

class CurrencyConversionUseCase(private val repository: CurrencyRepository) {

    suspend fun execute(
        from: String,
        to: String,
        value: Double
    ): Double {
        val rates = repository.getLatestRates(listOf("$from-$to")).first()
        return value * rates.bid
    }
}