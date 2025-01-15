package com.example.currencyconverter.repository

import com.example.currencyconverter.model.CurrencyResponse
import com.example.currencyconverter.network.ApiService

class CurrencyRepository(private val apiService: ApiService) {
    fun getExchangeRates(base: String, symbols: String) =
        apiService.getExchangeRates(base, symbols)
}
