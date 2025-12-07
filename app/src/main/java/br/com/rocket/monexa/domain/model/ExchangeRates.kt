package br.com.rocket.monexa.domain.model

data class ExchangeRates(
    val base: String,
    val timeLastUpdate: String,
    val timeNextUpdate: String,
    val rates: Map<String, Double>
) {
    fun getCurrencyCodes(): List<String> = rates.keys.sorted()
    fun getRate(currencyCode: String): Double? = rates[currencyCode]
    fun getLatestUpdate(): String = timeLastUpdate
    fun getNextUpdate(): String = timeNextUpdate
}