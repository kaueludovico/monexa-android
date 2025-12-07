package br.com.rocket.monexa.domain.model

import com.google.gson.annotations.SerializedName

data class CurrencySymbol(
    @SerializedName("name")
    val name: String,

    @SerializedName("currency")
    val currency: String
)
