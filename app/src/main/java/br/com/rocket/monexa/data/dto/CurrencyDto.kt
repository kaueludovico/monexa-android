package br.com.rocket.monexa.data.dto

import br.com.rocket.monexa.domain.model.Currency

//data class CurrencyDto(
//    val code: String,
//    val codein: String,
//    val name: String,
//    val bid: String,
//    val ask: String,
//    val timestamp: String
//) {
//    fun toCurrency(): Currency {
//        return Currency(
//            code = code,
//            codein = codein,
//            name = name,
//            bid = bid.toDouble(),
//            ask = ask.toDouble(),
//            timestamp = timestamp.toLong()
//        )
//    }
//}

data class CurrencyResponse(
    val currency: List<CurrencyDto>
)

data class CurrencyDto(
    val fromCurrency: String,
    val toCurrency: String,
    val name: String,
    val high: String,
    val low: String,
    val bidVariation: String,
    val percentageChange: String,
    val bidPrice: String,
    val askPrice: String,
    val updatedAtTimestamp: String,
    val updatedAtDate: String
) {
    fun toCurrency(): Currency {
        return Currency(
            code = fromCurrency,
            codein = toCurrency,
            name = name,
            bid = bidPrice.safeToDouble(),
            ask = askPrice.safeToDouble(),
            timestamp = updatedAtTimestamp.toLong()
        )
    }

    fun String.safeToDouble(): Double {
        return this.replace(",", ".").toDoubleOrNull() ?: 0.0
    }
}
