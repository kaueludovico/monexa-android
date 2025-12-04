package br.com.rocket.monexa.di

import br.com.rocket.monexa.data.datasource.CurrencyListApi
import br.com.rocket.monexa.data.datasource.CurrencyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    val currencyListApi: CurrencyListApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://brasilapi.com.br/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyListApi::class.java)
    }

    val currencyApi: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }
}