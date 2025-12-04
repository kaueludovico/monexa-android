package br.com.rocket.monexa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.rocket.monexa.domain.model.CurrencySymbol
import br.com.rocket.monexa.domain.usecase.CurrencyConversionUseCase
import br.com.rocket.monexa.domain.usecase.GetCurrencyListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConverterViewModel(private val useCase: CurrencyConversionUseCase,
                         private val useCaseList: GetCurrencyListUseCase) : ViewModel() {

    private val _result = MutableStateFlow<Double?>(null)
    val result: StateFlow<Double?> get() = _result

    private val _currencies = MutableStateFlow<List<CurrencySymbol>>(emptyList())
    val currencies: StateFlow<List<CurrencySymbol>> get() = _currencies

    fun convert(
        from: String,
        to: String,
        value: Double,
        date: String? = null
    ) {
        viewModelScope.launch {
            try {
                val converted = useCase.execute(from, to, value, date)
                _result.value = converted
            } catch (e: Exception) {
                _result.value = null
            }
        }
    }

    fun loadCurrencies() {
        viewModelScope.launch {
            try {
                val list = useCaseList.execute()
                _currencies.value = list
            } catch (e: Exception) {
                _currencies.value = emptyList()
            }
        }
    }

    class ConverterViewModelFactory(
        private val conversionUseCase: CurrencyConversionUseCase,
        private val listUseCase: GetCurrencyListUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ConverterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ConverterViewModel(conversionUseCase, listUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}