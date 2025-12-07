package br.com.rocket.monexa.di

import br.com.rocket.monexa.BuildConfig
import br.com.rocket.monexa.data.datasource.CurrencyApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(BuildConfig.API_TOKEN))
        .build()

    val currencyApi: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://brapi.dev/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }
}