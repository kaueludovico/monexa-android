package br.com.rocket.monexa.presentation.activity

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.Switch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.rocket.monexa.databinding.ActivityConverterBinding
import br.com.rocket.monexa.presentation.viewmodel.ConverterViewModel

import androidx.lifecycle.lifecycleScope
import br.com.rocket.monexa.data.repository.CurrencyListRepositoryImpl
import br.com.rocket.monexa.data.repository.CurrencyRepositoryImpl
import br.com.rocket.monexa.di.AppModule
import br.com.rocket.monexa.domain.usecase.CurrencyConversionUseCase
import br.com.rocket.monexa.domain.usecase.GetCurrencyListUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConverterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConverterBinding
    // Repositório para lista de moedas
    private val currencyRepositoryList = CurrencyListRepositoryImpl(AppModule.currencyListApi)

    // UseCases
    private val currencyConversionUseCase = CurrencyConversionUseCase(
        CurrencyRepositoryImpl(AppModule.currencyApi)
    )
    private val getCurrencyListUseCase = GetCurrencyListUseCase(currencyRepositoryList)

    // ViewModel
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

        configurarSpinners()
        configurarSwitch()
        configurarBotaoConverter()
    }

    private fun configurarSwitch() {
        binding.calendarView.visibility = if (binding.swHoje.isChecked) View.GONE else View.VISIBLE
        binding.btnConverter.isEnabled = true

        binding.swHoje.setOnCheckedChangeListener { _, ligado ->
            binding.calendarView.visibility = if (ligado) View.GONE else View.VISIBLE
            binding.calendarView.requestLayout()
        }

    }

    private fun configurarSpinners() {
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

    private fun configurarBotaoConverter() {
        binding.btnConverter.setOnClickListener {
            val from = binding.spMoedaOrigem.selectedItem.toString()
            val to = binding.spMoedaDestino.selectedItem.toString()
            val value = binding.etValor.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            val date = if (binding.swHoje.isChecked) null else formatDate(binding.calendarView.date)
            viewModel.convert(from, to, value, date)
        }

//        viewModel.result.observe(this) { converted ->
//            binding.tvResultado.text = converted?.toString() ?: "Erro"
//        }
    }

    private fun formatDate(timeInMillis: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timeInMillis))
    }
}