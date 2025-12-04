package br.com.rocket.monexa.domain.usecase

import br.com.rocket.monexa.domain.repository.CurrencyRepository

class CurrencyConversionUseCase(private val repository: CurrencyRepository) {

    suspend fun execute(
        from: String,
        to: String,
        value: Double,
        date: String? = null // se null, é tempo real
    ): Double {
        return if (date == null) {
            // Cotação em tempo real
            val rates = repository.getLatestRates(listOf("$from-$to"))
            val rate = rates["$from$to"] ?: error("Par de moedas não encontrado")
            value * rate.bid
        } else {
            // Cotação histórica
            val historical = repository.getHistoricalRates("$from-$to", date, date)
            val rate = historical.firstOrNull()?.bid ?: error("Dados históricos não encontrados")
            value * rate
        }
    }
}