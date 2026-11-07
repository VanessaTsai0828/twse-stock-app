package com.example.bankapp.domain.repository

import com.example.bankapp.domain.model.StockItem

/**
 * Repository interface defining the contract for stock data access.
 * Following Clean Architecture, the domain layer defines this contract
 * and the data layer provides the implementation.
 */
interface StockRepository {

    /**
     * Fetches all stock items by combining data from three TWSE API endpoints:
     * - BWIBBU_ALL: PE ratio, dividend yield, PB ratio
     * - STOCK_DAY_AVG_ALL: closing price, monthly average price
     * - STOCK_DAY_ALL: daily trade info
     *
     * @return Result wrapping a list of [StockItem] or an exception
     */
    suspend fun getStockItems(): Result<List<StockItem>>
}
