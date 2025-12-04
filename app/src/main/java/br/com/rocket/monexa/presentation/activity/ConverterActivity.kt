package br.com.rocket.monexa.presentation.activity

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import br.com.rocket.monexa.databinding.ActivityConverterBinding
import br.com.rocket.monexa.presentation.viewmodel.ConverterViewModel

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.rocket.monexa.data.repository.CurrencyListRepositoryImpl
import br.com.rocket.monexa.data.repository.CurrencyRepositoryImpl
import br.com.rocket.monexa.di.AppModule
import br.com.rocket.monexa.domain.usecase.CurrencyConversionUseCase
import br.com.rocket.monexa.domain.usecase.GetCurrencyListUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConverterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConverterBinding
    private val currencyRepositoryList = CurrencyListRepositoryImpl(AppModule.currencyListApi)
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
        configSwitch()
        configEditText()
        configBotaoConverter()
    }

    private fun configSwitch() {
        binding.btnConverter.isEnabled = true
        val oneDayMillis = 24 * 60 * 60 * 1000
        binding.datePicker.maxDate = System.currentTimeMillis() - oneDayMillis
        binding.swHoje.setOnCheckedChangeListener { _, isChecked ->
            binding.datePicker.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
    }

    private fun configSpinners() {
        lifecycleScope.launch {
            viewModel.currencies.collectLatest { list ->
                val symbols = list.map { it.simbolo }
                val adapter = ArrayAdapter(this@ConverterActivity, R.layout.simple_spinner_item, symbols)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spMoedaOrigem.adapter = adapter
                binding.spMoedaDestino.adapter = adapter
            }
        }

        viewModel.loadCurrencies()
    }

    private fun configBotaoConverter() {
        binding.btnConverter.setOnClickListener {
            val from = binding.spMoedaOrigem.selectedItem.toString()
            val to = binding.spMoedaDestino.selectedItem.toString()

            val value = binding.etValor.text.toString().toDoubleOrNull() ?: return@setOnClickListener

            val date = if (binding.swHoje.isChecked) {
                null
            } else {
                val day = binding.datePicker.dayOfMonth
                val month = binding.datePicker.month
                val year = binding.datePicker.year

                val calendar = java.util.Calendar.getInstance()
                calendar.set(year, month, day, 0, 0, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)

                formatDate(calendar.timeInMillis)
            }

            viewModel.convert(from, to, value, date)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { value ->
                    if (value != null) {
                        alert()
                    }
                }
            }
        }


//    viewModel.result.observe(this) { converted ->
//        binding.tvResultado.text = converted?.toString() ?: "Erro"
//    }
    }

    private fun configEditText() {
        binding.etValor.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var cursorPosition = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                cursorPosition = binding.etValor.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null || s.toString() == current) return

                binding.etValor.removeTextChangedListener(this)

                try {
                    // Remove caracteres inválidos (tudo que não seja número ou ponto)
                    var clean = s.toString().replace("[^\\d.]".toRegex(), "")

                    // Limita casas decimais
                    val parts = clean.split(".")
                    var intPart = parts[0].take(9) // até 9 dígitos inteiros (1 bilhão)
                    val decimalPart = if (parts.size > 1) parts[1].take(2) else ""

                    // Formata os milhares sem inverter
                    intPart = intPart.reversed().chunked(3).joinToString(".").reversed()

                    val formatted = if (decimalPart.isNotEmpty()) "$intPart.$decimalPart" else intPart

                    // Ajusta o cursor corretamente
                    val diff = formatted.length - s.toString().length
                    val newCursor = (cursorPosition + diff).coerceIn(0, formatted.length)

                    current = formatted
                    binding.etValor.setText(formatted)
                    binding.etValor.setSelection(newCursor)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.etValor.addTextChangedListener(this)
            }
        })
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

    private fun formatDate(timeInMillis: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timeInMillis))
    }
}