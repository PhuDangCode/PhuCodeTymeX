package com.example.currencyconverter.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyconverter.R
import com.example.currencyconverter.model.CurrencyResponse
import com.example.currencyconverter.network.ApiService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

data class CurrencyItem(
    val currencyCode: String,
    val symbol: String,
    val flagResId: Int
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val amountEditText = findViewById<EditText>(R.id.amountEditText)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        val fromSpinner = findViewById<Spinner>(R.id.fromSpinner)
        val toSpinner = findViewById<Spinner>(R.id.toSpinner)
        val convertButton = findViewById<Button>(R.id.convertButton)
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val fromFlagImageView = findViewById<ImageView>(R.id.fromFlagImageView)
        val toFlagImageView = findViewById<ImageView>(R.id.toFlagImageView)
        val viewPager = findViewById<ViewPager2>(R.id.rankingViewPager)

        val currencyList = listOf(
            CurrencyItem("USD", "$", R.drawable.ic_usd_flag),
            CurrencyItem("EUR", "€", R.drawable.ic_eur_flag),
            CurrencyItem("JPY", "¥", R.drawable.ic_jpy_flag),
            CurrencyItem("GBP", "£", R.drawable.ic_gbp_flag),
            CurrencyItem("VND", "₫", R.drawable.ic_vnd_flag),
            CurrencyItem("AUD", "A$", R.drawable.ic_aud_flag),
            CurrencyItem("CAD", "C$", R.drawable.ic_cad_flag),
            CurrencyItem("CHF", "Fr.", R.drawable.ic_chf_flag),
            CurrencyItem("CNY", "¥", R.drawable.ic_cny_flag),
            CurrencyItem("HKD", "HK$", R.drawable.ic_hkd_flag),
            CurrencyItem("KRW", "₩", R.drawable.ic_krw_flag),
            CurrencyItem("INR", "₹", R.drawable.ic_inr_flag),
            CurrencyItem("SGD", "S$", R.drawable.ic_sgd_flag),
            CurrencyItem("MYR", "RM", R.drawable.ic_myr_flag),
            CurrencyItem("IDR", "Rp", R.drawable.ic_idr_flag),
            CurrencyItem("PHP", "₱", R.drawable.ic_php_flag),
            CurrencyItem("THB", "฿", R.drawable.ic_thb_flag),
            CurrencyItem("ZAR", "R", R.drawable.ic_zar_flag),
            CurrencyItem("NZD", "$", R.drawable.ic_nzd_flag),
            CurrencyItem("BRL", "R$", R.drawable.ic_brl_flag),
            CurrencyItem("RUB", "₽", R.drawable.ic_rub_flag)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList.map { it.currencyCode })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner.adapter = adapter
        toSpinner.adapter = adapter

        fromSpinner.setSelection(currencyList.indexOfFirst { it.currencyCode == "USD" })
        toSpinner.setSelection(currencyList.indexOfFirst { it.currencyCode == "VND" })

        fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fromFlagImageView.setImageResource(currencyList[position].flagResId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                toFlagImageView.setImageResource(currencyList[position].flagResId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        convertButton.setOnClickListener {
            performConversion(
                amountEditText.text.toString().toFloatOrNull() ?: 1f,
                fromSpinner.selectedItem.toString(),
                toSpinner.selectedItem.toString(),
                resultTextView,
                lineChart
            )
        }
        performConversion(1f, "USD", "VND", resultTextView, lineChart)
        animateViewsOnStart(resultTextView, lineChart, convertButton)
        fetchCurrencyRanking("USD", viewPager)
    }

    private fun animateViewsOnStart(resultTextView: TextView, lineChart: LineChart, convertButton: Button) {
        // Animation for Result TextView
        ObjectAnimator.ofFloat(resultTextView, "alpha", 0f, 1f).apply {
            duration = 800
            start()
        }

        // Animation for Line Chart
        ObjectAnimator.ofFloat(lineChart, "translationY", 300f, 0f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(lineChart, "alpha", 0f, 1f).apply {
            duration = 1000
            start()
        }

        // Animation for Convert Button
        ObjectAnimator.ofFloat(convertButton, "scaleX", 0.5f, 1f).apply {
            duration = 800
            start()
        }

        ObjectAnimator.ofFloat(convertButton, "scaleY", 0.5f, 1f).apply {
            duration = 800
            start()
        }
    }

    private fun performConversion(
        amount: Float,
        fromCurrency: String,
        toCurrency: String,
        resultTextView: TextView,
        lineChart: LineChart
    ) {
        val apiService = ApiService.create()
        val accessKey = "ea6d059d62fd37cc1dd1b5ada30db62c"

        apiService.getExchangeRates(accessKey, fromCurrency, toCurrency)
            .enqueue(object : Callback<CurrencyResponse> {
                override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                    if (response.isSuccessful) {
                        val rates = response.body()?.rates
                        val exchangeRate = rates?.get(toCurrency)

                        if (exchangeRate != null) {
                            val convertedAmount = amount * exchangeRate
                            resultTextView.text = String.format(
                                "%.2f %s = %.2f %s",
                                amount, fromCurrency, convertedAmount, toCurrency
                            )

                            updateChartData(lineChart, exchangeRate, fromCurrency, toCurrency)
                        } else {
                            resultTextView.text = "Error: Rate not available"
                        }
                    } else {
                        resultTextView.text = "Error: Unable to fetch rates"
                    }
                }

                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                    resultTextView.text = "Error: ${t.localizedMessage}"
                }
            })
    }

    private fun animateViewsOnConvert(resultTextView: TextView, lineChart: LineChart) {
        // Fade in animation for Result TextView
        ObjectAnimator.ofFloat(resultTextView, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }

        // Slide-up animation for Line Chart
        ObjectAnimator.ofFloat(lineChart, "translationY", 200f, 0f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun updateChartData(lineChart: LineChart, exchangeRate: Float, fromCurrency: String, toCurrency: String) {
        val entries = generateDynamicChartData(exchangeRate)
        val dataSet = LineDataSet(entries, "$fromCurrency to $toCurrency Trend").apply {
            color = getColor(R.color.purple_500)
            valueTextSize = 0f
            setCircleColor(getColor(R.color.purple_200))
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
        }

        lineChart.data = LineData(dataSet)
        lineChart.description = Description().apply { text = "" }
        lineChart.invalidate()
    }

    private fun generateDynamicChartData(baseRate: Float): List<Entry> {
        val entries = mutableListOf<Entry>()
        var currentRate = baseRate
        for (i in 1..10) {
            currentRate += Random.nextFloat() * 2 - 1
            entries.add(Entry(i.toFloat(), currentRate))
        }
        return entries
    }
    private fun fetchCurrencyRanking(baseCurrency: String, viewPager: ViewPager2) {
        val apiService = ApiService.create()
        apiService.getExchangeRates("ea6d059d62fd37cc1dd1b5ada30db62c", baseCurrency)
            .enqueue(object : Callback<CurrencyResponse> {
                override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                    if (response.isSuccessful) {
                        val rates = response.body()?.rates ?: emptyMap()
                        val rankingList = rates.entries.sortedByDescending { it.value }

                        // Prepare data for the adapter
                        val data = rankingList.map { entry ->
                            mapOf(
                                "icon" to entry.key.take(1), // Icon logic: take the first letter
                                "currency" to entry.key, // Currency code
                                "amount" to String.format("$%.2f", entry.value), // Main amount
                                "secondary" to String.format("$%.2f", entry.value * 1.1) // Example secondary amount
                            )
                        }

                        // Paginate the data into chunks of 5
                        val paginatedData = data.chunked(3)

                        // Set up the ViewPager2 with the custom adapter
                        viewPager.adapter = RankingPagerAdapter(this@MainActivity, paginatedData)
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to fetch rankings", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
