package br.com.rocket.monexa.data.datasource

import br.com.rocket.monexa.data.dto.CurrencyDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApi {

    @GET("json/last/{moedas}")
    suspend fun getLatestRates(
        @Path("moedas") moedas: String
    ): Map<String, CurrencyDto>

    @GET("json/daily/{moeda}/1")
    suspend fun getHistoricalRates(
        @Path("moeda") moeda: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<CurrencyDto>
}