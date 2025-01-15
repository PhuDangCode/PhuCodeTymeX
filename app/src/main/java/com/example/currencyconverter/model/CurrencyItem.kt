package com.example.currencyconverter.model

data class CurrencyItem(
    val currencyCode: String, // E.g. USD, EUR
    val symbol: String,       // E.g. $, €
    val flagResId: Int        // Resource ID of the flag image
)