package br.com.rocket.monexa.di

import br.com.rocket.monexa.data.datasource.CurrencyApi
import br.com.rocket.monexa.data.repository.CurrencyListRepositoryImpl
import br.com.rocket.monexa.domain.repository.CurrencyListRepository
import br.com.rocket.monexa.domain.usecase.GetCurrencyListUseCase
import br.com.rocket.monexa.presentation.viewmodel.ConverterViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val networkModule = module {

    single<Gson> {
        GsonBuilder().setLenient().create()
    }

    single<OkHttpClient> {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/v6/") // ← NOVA URL
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single<CurrencyApi> {
        get<Retrofit>().create(CurrencyApi::class.java)
    }
}

val repositoryModule = module {
    single<CurrencyListRepository> {
        CurrencyListRepositoryImpl(get())
    }
}

val useCaseModule = module {
    factory { GetCurrencyListUseCase(get()) }
}

val viewModelModule = module {
    single { ConverterViewModel(get()) }
}