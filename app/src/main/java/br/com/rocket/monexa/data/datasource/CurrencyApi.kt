package br.com.rocket.monexa.data.datasource

import br.com.rocket.monexa.data.dto.CurrencyDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {
    @GET("latest/{base}")
    suspend fun getLatestRates(
        @Path("base") base: String = "USD"
    ): CurrencyDto
}

