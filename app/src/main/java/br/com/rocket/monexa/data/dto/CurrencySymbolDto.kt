package br.com.rocket.monexa.data.dto

import br.com.rocket.monexa.domain.model.CurrencySymbol
import com.google.gson.annotations.SerializedName

data class CurrencyListResponse(
    @SerializedName("currencies")
    val currencies: List<CurrencySymbol>
)


