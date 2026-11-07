package com.example.bankapp.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for /exchangeReport/STOCK_DAY_AVG_ALL
 * 上市個股日收盤價及月平均價
 */
data class StockAvgPriceDto(
    @SerializedName("Code") val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("ClosingPrice") val closingPrice: String,
    @SerializedName("MonthlyAveragePrice") val monthlyAveragePrice: String
)
