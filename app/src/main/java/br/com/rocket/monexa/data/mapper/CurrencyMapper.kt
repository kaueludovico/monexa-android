package br.com.rocket.monexa.data.mapper

import br.com.rocket.monexa.data.dto.CurrencyListResponse
import br.com.rocket.monexa.data.dto.CurrencyUniqueCode


object CurrencyMapper {

    fun toDomain(response: CurrencyListResponse): Pair<List<CurrencyUniqueCode>, List<CurrencyUniqueCode>> {
        val g10Currencies = setOf(
            "USD", // Dólar Americano
            "EUR", // Euro
            "JPY", // Iene Japonês
            "GBP", // Libra Esterlina
            "CHF", // Franco Suíço
            "CAD", // Dólar Canadense
            "AUD", // Dólar Australiano
            "NZD", // Dólar Neozelandês
            "SEK", // Coroa Sueca
            "NOK"  // Coroa Norueguesa
        )

        val validBases = mutableMapOf<String, String>()    // FROM: apenas G10
        val allCurrencies = mutableMapOf<String, String>() // TO: todas as moedas

        response.currencies.forEach { dto ->
            val codes = dto.name.split("-")
            val names = dto.currency.split("/")

            if (codes.size == 2 && names.size == 2) {
                val code1 = codes[0].trim()
                val name1 = names[0].trim()
                val code2 = codes[1].trim()
                val name2 = names[1].trim()

                // Adiciona todas as moedas no TO
                allCurrencies.putIfAbsent(code1, name1)
                allCurrencies.putIfAbsent(code2, name2)

                // Adiciona apenas moedas G10 no FROM (excluindo BRL automaticamente)
                if (code1 in g10Currencies) {
                    validBases.putIfAbsent(code1, name1)
                }
                if (code2 in g10Currencies) {
                    validBases.putIfAbsent(code2, name2)
                }
            }
        }

        return Pair(
            validBases.entries
                .map { CurrencyUniqueCode(it.key, it.value) }
                .sortedBy { it.code },

            allCurrencies.entries
                .map { CurrencyUniqueCode(it.key, it.value) }
                .sortedBy { it.code }
        )
    }

}