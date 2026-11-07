package com.example.bankapp.data.remote

import com.example.bankapp.data.remote.model.StockAvgPriceDto
import com.example.bankapp.data.remote.model.StockDayDto
import com.example.bankapp.data.remote.model.StockValuationDto
import retrofit2.http.GET

/**
 * Retrofit API interface for Taiwan Stock Exchange (TWSE) OpenAPI.
 * Base URL: https://openapi.twse.com.tw/v1/
 */
interface TwseApiService {

    @GET("exchangeReport/BWIBBU_ALL")
    suspend fun getStockValuations(): List<StockValuationDto>

    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockAvgPrices(): List<StockAvgPriceDto>

    @GET("exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayInfo(): List<StockDayDto>
}
