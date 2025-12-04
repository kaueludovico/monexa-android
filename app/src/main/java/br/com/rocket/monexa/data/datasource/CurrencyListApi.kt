package br.com.rocket.monexa.data.datasource

import br.com.rocket.monexa.data.dto.CurrencySymbolDto
import retrofit2.http.GET

interface CurrencyListApi {
    @GET("cambio/v1/moedas")
    suspend fun getCurrencies(): List<CurrencySymbolDto>
}