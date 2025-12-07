package br.com.rocket.monexa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rocket.monexa.domain.model.ExchangeRates
import br.com.rocket.monexa.domain.usecase.GetCurrencyListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConverterViewModel(
    private val getCurrencyListUseCase: GetCurrencyListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    private var exchangeRates: ExchangeRates? = null

    fun loadCurrencies(base: String? = null) {
        viewModelScope.launch {
            _uiState.value = CurrencyUiState.Loading

            getCurrencyListUseCase.execute(base)
                .onSuccess { rates ->
                    exchangeRates = rates // ← Armazena as taxas
                    _uiState.value = CurrencyUiState.Success(rates.getCurrencyCodes())
                }
                .onFailure { exception ->
                    _uiState.value = CurrencyUiState.Error(exception.message ?: "Erro")
                }
        }
    }

    fun convertCurrency(from: String, to: String, amount: Double): Double? {
        val rates = exchangeRates ?: return null

        val fromRate = rates.getRate(from) ?: return null
        val toRate = rates.getRate(to) ?: return null

        return (amount / fromRate) * toRate
    }

    fun getLastUpdate(): String? {
        val date = exchangeRates ?: return null
        return date.getLatestUpdate()
    }

    fun getNextUpdate(): String? {
        val date = exchangeRates ?: return null
        return date.getNextUpdate()
    }
}

sealed class CurrencyUiState {
    object Loading : CurrencyUiState()
    data class Success(val currencies: List<String>) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}