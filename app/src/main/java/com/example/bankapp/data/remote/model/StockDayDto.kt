package com.example.bankapp.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for /exchangeReport/STOCK_DAY_ALL
 * 上市個股日成交資訊
 */
data class StockDayDto(
    @SerializedName("Code") val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("TradeVolume") val tradeVolume: String,
    @SerializedName("TradeValue") val tradeValue: String,
    @SerializedName("OpeningPrice") val openingPrice: String,
    @SerializedName("HighestPrice") val highestPrice: String,
    @SerializedName("LowestPrice") val lowestPrice: String,
    @SerializedName("ClosingPrice") val closingPrice: String,
    @SerializedName("Change") val change: String,
    @SerializedName("Transaction") val transaction: String
)
