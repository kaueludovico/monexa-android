package br.com.rocket.monexa.data.mapper

import br.com.rocket.monexa.data.dto.CurrencyDto
import br.com.rocket.monexa.domain.model.ExchangeRates

fun CurrencyDto.toExchangeRates(): ExchangeRates {
    return ExchangeRates(
        base = this.baseCode,
        timeLastUpdate = this.timeLastUpdateUtc,
        timeNextUpdate = this.timeNextUpdateUtc,
        rates = this.rates
    )
}
