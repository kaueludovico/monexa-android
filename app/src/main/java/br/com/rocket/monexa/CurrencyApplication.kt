package br.com.rocket.monexa

import android.app.Application
import br.com.rocket.monexa.di.networkModule
import br.com.rocket.monexa.di.repositoryModule
import br.com.rocket.monexa.di.useCaseModule
import br.com.rocket.monexa.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CurrencyApplication)
            modules(
                networkModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}