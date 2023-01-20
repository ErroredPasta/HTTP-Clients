package com.example.exchangerate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.exchangerate.domain.model.Currency
import com.example.exchangerate.domain.repository.ConversionRepository
import com.example.exchangerate.util.runCoroutineCatching
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Provider

class MainViewModel @Inject constructor(
    private val repository: ConversionRepository
) : ViewModel() {

    private val _inputState = MutableStateFlow(
        ConversionInputUiState(
            baseCurrency = Currency.KRW,
            targetCurrency = Currency.USD
        )
    )
    val inputState = _inputState.asStateFlow()

    @FlowPreview
    val conversionResultState = inputState
        .debounce(timeoutMillis = 300)
        .map { inputUiState ->
            tryConvertCurrency(inputUiState = inputUiState)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ConversionResultUiState.Loading
        )


    fun onBaseCurrencyAmountChange(amount: String) {
        val amountValue = if (amount.isEmpty()) 0.0 else amount.toDouble()
        _inputState.update { it.copy(baseCurrencyAmount = amountValue) }
    }

    fun onBaseCurrencyChange(baseCurrency: Currency) {
        _inputState.update { it.copy(baseCurrency = baseCurrency) }
    }

    fun onTargetCurrencyChange(targetCurrency: Currency) {
        _inputState.update { it.copy(targetCurrency = targetCurrency) }
    }

    private suspend fun tryConvertCurrency(inputUiState: ConversionInputUiState): ConversionResultUiState =
        runCoroutineCatching {
            repository.convertCurrency(
                from = inputUiState.baseCurrency,
                to = inputUiState.targetCurrency,
                amount = inputUiState.baseCurrencyAmount
            )
        }.map { result ->
            ConversionResultUiState.Success(data = result.conversionResult)
        }.getOrElse { cause ->
            ConversionResultUiState.Error(error = cause)
        }

    class Factory @Inject constructor(
        private val provider: Provider<MainViewModel>
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return provider.get() as T
        }
    }
}

data class ConversionInputUiState(
    val baseCurrency: Currency,
    val baseCurrencyAmount: Double = 1.0,
    val targetCurrency: Currency
)

sealed interface ConversionResultUiState {
    object Loading : ConversionResultUiState
    data class Success(val data: Double) : ConversionResultUiState
    data class Error(val error: Throwable) : ConversionResultUiState
}