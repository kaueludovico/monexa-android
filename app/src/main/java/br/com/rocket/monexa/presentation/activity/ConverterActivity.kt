package br.com.rocket.monexa.presentation.activity

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import br.com.rocket.monexa.databinding.ActivityConverterBinding
import br.com.rocket.monexa.presentation.viewmodel.ConverterViewModel

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.rocket.monexa.data.mapper.CurrencyMapper
import br.com.rocket.monexa.data.repository.CurrencyListRepositoryImpl
import br.com.rocket.monexa.data.repository.CurrencyRepositoryImpl
import br.com.rocket.monexa.di.AppModule
import br.com.rocket.monexa.domain.usecase.CurrencyConversionUseCase
import br.com.rocket.monexa.domain.usecase.GetCurrencyListUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ConverterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConverterBinding
    private val currencyRepositoryList = CurrencyListRepositoryImpl(AppModule.currencyApi)
    private val currencyConversionUseCase = CurrencyConversionUseCase(
        CurrencyRepositoryImpl(AppModule.currencyApi)
    )
    private val getCurrencyListUseCase = GetCurrencyListUseCase(currencyRepositoryList)
    private val viewModel: ConverterViewModel by viewModels {
        ConverterViewModel.ConverterViewModelFactory(
            currencyConversionUseCase,
            getCurrencyListUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configSpinners()
        configConvertButton()

        binding.etValor.setupCurrencyFormat()
    }

    private fun configSpinners() {
        lifecycleScope.launch {
            viewModel.currencies.collectLatest { list ->
                val (baseCurrencies, allCurrencies) = CurrencyMapper.toDomain(list)

                // Spinner FROM: apenas moedas G10
                val baseSymbols = baseCurrencies.map { "${it.code} - ${it.name}" }
                val adapterFrom = ArrayAdapter(
                    this@ConverterActivity,
                    R.layout.simple_spinner_item,
                    baseSymbols
                )
                adapterFrom.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spMoedaOrigem.adapter = adapterFrom

                // Spinner TO: todas as moedas
                val allSymbols = allCurrencies.map { "${it.code} - ${it.name}" }
                val adapterTo = ArrayAdapter(
                    this@ConverterActivity,
                    R.layout.simple_spinner_item,
                    allSymbols
                )
                adapterTo.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spMoedaDestino.adapter = adapterTo
            }
        }

        viewModel.loadCurrencies()
    }

    private fun configConvertButton() {
        binding.btnConverter.setOnClickListener {
            val from = binding.spMoedaOrigem.selectedItem.toString()
            val to = binding.spMoedaDestino.selectedItem.toString()

            val value = binding.etValor.getCurrencyValue()

            if (from != to) {
                viewModel.convert(
                    from.substring(0, 3),
                    to.substring(0, 3),
                    value
                )
            } else {
                Toast.makeText(
                    this,
                    "Não é possível converter pois as duas moedas selecionadas são iguais.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { value ->
                    if (value != null) {
                        alert()
                    } else {
                        toast()
                    }
                }
            }
        }
    }

    fun EditText.setupCurrencyFormat() {
        val locale = Locale.GERMANY
        val symbols = DecimalFormatSymbols(locale)
        val formatter = DecimalFormat("#,##0.00", symbols).apply {
            isDecimalSeparatorAlwaysShown = true
        }

        var isUpdating = false

        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isUpdating) return

                isUpdating = true

                try {
                    val cleanString = editable.toString().replace(Regex("[^\\d]"), "")

                    if (cleanString.isEmpty()) {
                        setText("")
                    } else {
                        val parsed = cleanString.toLongOrNull() ?: 0L
                        val value = parsed / 100.0

                        if (value > 1_000_000_000.0) {
                            val formatted = formatter.format(1_000_000_000.0)
                            setText(formatted)
                            setSelection(formatted.length)
                        } else {
                            val formatted = formatter.format(value)
                            setText(formatted)
                            setSelection(formatted.length)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                isUpdating = false
            }
        })

        inputType = android.text.InputType.TYPE_CLASS_NUMBER
    }

    fun EditText.getCurrencyValue(): Double {
        val cleanString = text.toString().replace(Regex("[^\\d]"), "")
        return if (cleanString.isEmpty()) 0.0 else cleanString.toDouble() / 100.0
    }

    fun alert() {
        AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage("Operação concluída com sucesso.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun toast() {
        Toast.makeText(
            this,
            "Não é possível converter pois as duas moedas selecionadas são iguais.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun formatDate(timeInMillis: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timeInMillis))
    }
}