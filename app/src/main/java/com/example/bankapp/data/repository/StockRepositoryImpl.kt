package com.example.bankapp.data.repository

import com.example.bankapp.data.remote.TwseApiService
import com.example.bankapp.domain.model.StockItem
import com.example.bankapp.domain.repository.StockRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [StockRepository] that fetches data from the TWSE OpenAPI
 * and merges three data sources by stock code.
 */
@Singleton
class StockRepositoryImpl @Inject constructor(
    private val apiService: TwseApiService
) : StockRepository {

    override suspend fun getStockItems(): Result<List<StockItem>> {
        return runCatching {
            // Fetch all three endpoints concurrently via coroutine structured concurrency
            val dayInfoList = apiService.getStockDayInfo()
            val avgPriceList = apiService.getStockAvgPrices()
            val valuationList = apiService.getStockValuations()

            // Build lookup maps for O(1) merging
            val avgPriceMap = avgPriceList.associateBy { it.code }
            val valuationMap = valuationList.associateBy { it.code }

            // Merge: STOCK_DAY_ALL is the primary source; join avg price and valuation
            dayInfoList.mapNotNull { day ->
                val avg = avgPriceMap[day.code]
                val val_ = valuationMap[day.code]

                StockItem(
                    code = day.code,
                    name = day.name,
                    tradeVolume = day.tradeVolume,
                    tradeValue = day.tradeValue,
                    openingPrice = day.openingPrice,
                    highestPrice = day.highestPrice,
                    lowestPrice = day.lowestPrice,
                    closingPrice = avg?.closingPrice ?: day.closingPrice,
                    change = day.change,
                    transaction = day.transaction,
                    monthlyAveragePrice = avg?.monthlyAveragePrice.orEmpty(),
                    peRatio = val_?.peRatio.orEmpty(),
                    dividendYield = val_?.dividendYield.orEmpty(),
                    pbRatio = val_?.pbRatio.orEmpty()
                )
            }
        }
    }
}
