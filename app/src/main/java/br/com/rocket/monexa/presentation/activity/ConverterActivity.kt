package br.com.rocket.monexa.presentation.activity

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.rocket.monexa.databinding.ActivityConverterBinding
import br.com.rocket.monexa.presentation.viewmodel.ConverterViewModel
import br.com.rocket.monexa.presentation.viewmodel.CurrencyUiState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ConverterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConverterBinding
    private val viewModel: ConverterViewModel by viewModel()

    private var currenciesList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        configConvertButton()
        binding.etValor.setupCurrencyFormat()

        viewModel.loadCurrencies()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is CurrencyUiState.Loading -> {
                            showLoading(true)
                        }

                        is CurrencyUiState.Success -> {
                            showLoading(false)
                            currenciesList = state.currencies
                            configSpinners(state.currencies)
                        }

                        is CurrencyUiState.Error -> {
                            showLoading(false)
                            Toast.makeText(
                                this@ConverterActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnConverter.isEnabled = !isLoading
        binding.spMoedaOrigem.isEnabled = !isLoading
        binding.spMoedaDestino.isEnabled = !isLoading
        binding.etValor.isEnabled = !isLoading
    }

    private fun configSpinners(currencies: List<String>) {
        if (currencies.isEmpty()) return

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spMoedaOrigem.adapter = adapter
        binding.spMoedaDestino.adapter = adapter

        val usdIndex = currencies.indexOf("USD")
        val brlIndex = currencies.indexOf("BRL")

        if (usdIndex != -1) binding.spMoedaOrigem.setSelection(usdIndex)
        if (brlIndex != -1) binding.spMoedaDestino.setSelection(brlIndex)
    }

    private fun configConvertButton() {
        binding.btnConverter.setOnClickListener {
            val from = binding.spMoedaOrigem.selectedItem.toString()
            val to = binding.spMoedaDestino.selectedItem.toString()
            val value = binding.etValor.getCurrencyValue()

            if (from != to) {
                if (value > 0) {
                    performConversion(from, to, value)
                } else {
                    Toast.makeText(
                        this,
                        "Por favor, insira um valor válido.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Não é possível converter pois as duas moedas selecionadas são iguais.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun performConversion(from: String, to: String, value: Double) {
        val result = viewModel.convertCurrency(from, to, value)
        val lastDate = viewModel.getLastUpdate()
        val nextDate = viewModel.getNextUpdate()

        if (result != null) {
            binding.tvCodeConversion.setText("Moedas: ${from} - ${to}")
            binding.tvValue.setText("Cotação das moedas informadas: ${String.format("%.2f", result)}")
            binding.tvDateLatestUpdate.setText(formatLastUpdateBrazil(lastDate, "Última"))
            binding.tvDateNextUpdate.setText(formatLastUpdateBrazil(nextDate, "Próxima"))
        } else {
            Toast.makeText(
                this,
                "Erro ao realizar conversão. Tente novamente.",
                Toast.LENGTH_SHORT
            ).show()
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

    fun formatLastUpdateBrazil(dateUtc: String?, time: String?): String {
        return try {
            val parserUtc = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
            parserUtc.timeZone = TimeZone.getTimeZone("UTC")

            val date = parserUtc.parse(dateUtc) ?: return "Data indisponível"

            val formatterBrazil = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
            formatterBrazil.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

            "${time} atualização: ${formatterBrazil.format(date)}"

        } catch (e: Exception) {
            "Data indisponível"
        }
    }
}