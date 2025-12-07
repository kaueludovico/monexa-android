package br.com.rocket.monexa.data.datasource

import br.com.rocket.monexa.data.dto.CurrencyListResponse
import br.com.rocket.monexa.data.dto.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("v2/currency/available")
    suspend fun getCurrencies(
        @Query("token") token: String
    ): CurrencyListResponse

    @GET("v2/currency")
    suspend fun getLatestRates(
        @Query("currency") currency: String
    ): CurrencyResponse
}