package com.example.currencyconverter.network

import com.example.currencyconverter.model.CurrencyResponse
import com.example.currencyconverter.model.TrendResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Latest exchange rates endpoint
    @GET("latest")
    fun getExchangeRates(
        @Query("access_key") accessKey: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String = "" // Optional parameter
    ): Call<CurrencyResponse>

    // Time series exchange rates endpoint
    @GET("timeseries")
    fun getCurrencyTrends(
        @Query("access_key") accessKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): Call<TrendResponse>

    companion object {
        private const val BASE_URL = "https://api.exchangeratesapi.io/v1/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
