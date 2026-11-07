package com.example.bankapp.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for /exchangeReport/BWIBBU_ALL
 * 上市個股日本益比、殖利率及股價淨值比
 */
data class StockValuationDto(
    @SerializedName("Code") val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("PEratio") val peRatio: String,
    @SerializedName("DividendYield") val dividendYield: String,
    @SerializedName("PBratio") val pbRatio: String
)
