package com.example.exchangerate.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.exchangerate.R
import com.example.exchangerate.databinding.ActivityMainBinding
import com.example.exchangerate.domain.exception.ConversionException
import com.example.exchangerate.domain.model.Currency
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val baseCurrencySelectionDialog by lazy {
        CurrencySelectionDialog(
            context = this,
            title = getString(R.string.select_a_base_currency),
            defaultSelectedItemIndex = Currency.values()
                .indexOf(viewModel.inputState.value.baseCurrency),
            onSelectItem = {
                viewModel.onBaseCurrencyChange(baseCurrency = it)
            }
        )
    }

    private val targetCurrencySelectionDialog by lazy {
        CurrencySelectionDialog(
            context = this,
            title = getString(R.string.select_a_target_currency),
            defaultSelectedItemIndex = Currency.values()
                .indexOf(viewModel.inputState.value.targetCurrency),
            onSelectItem = {
                viewModel.onTargetCurrencyChange(targetCurrency = it)
            }
        )
    }

    @FlowPreview
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as ConversionApplication).appComponent
            .getMainActivityComponentFactory()
            .create()
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel
        }

        setClickListeners()

        collectFlowWhenStarted(viewModel.conversionResultState) {
            when (it) {
                ConversionResultUiState.Loading -> {
                    binding.targetCurrencyAmountTextInput.text = getString(R.string.loading)
                }

                is ConversionResultUiState.Success -> {
                    binding.targetCurrencyAmountTextInput.text = String.format("%f", it.data)
                }

                is ConversionResultUiState.Error -> {
                    handleError(throwable = it.error)
                }
            }
        }
    }

    private fun handleError(throwable: Throwable) = when (throwable) {
        ConversionException.MalformedRequest -> showSnackbar(message = getString(R.string.malformed_request))
        ConversionException.UnsupportedCode -> showSnackbar(message = getString(R.string.unsupported_code))
        else -> showSnackbar(message = throwable.message ?: getString(R.string.unknown_error))
    }


    private fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(
            /* view = */ binding.root,
            /* text = */ message,
            /* duration = */ duration
        ).show()
    }

    private fun <T> collectFlowWhenStarted(flow: Flow<T>, action: (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { flow.collectLatest(action) }
        }
    }

    private fun setClickListeners() = with(binding) {
        baseCurrencyText.setOnClickListener { baseCurrencySelectionDialog.show() }
        targetCurrencyText.setOnClickListener { targetCurrencySelectionDialog.show() }
    }

    private var TextInputLayout.text: String
        get() = this.editText!!.text.toString()
        set(value) = this.editText!!.setText(value)
}