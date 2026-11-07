package com.example.bankapp.domain.model

/**
 * Domain model combining data from all three TWSE API endpoints.
 * This is the single source of truth for the UI layer.
 */
data class StockItem(
    // Common
    val code: String,
    val name: String,

    // From STOCK_DAY_ALL
    val tradeVolume: String,
    val tradeValue: String,
    val openingPrice: String,
    val highestPrice: String,
    val lowestPrice: String,
    val closingPrice: String,
    val change: String,
    val transaction: String,

    // From STOCK_DAY_AVG_ALL
    val monthlyAveragePrice: String,

    // From BWIBBU_ALL
    val peRatio: String,
    val dividendYield: String,
    val pbRatio: String
) {
    /**
     * Returns numeric closing price or null if not parseable.
     */
    val closingPriceValue: Double?
        get() = closingPrice.replace(",", "").toDoubleOrNull()

    /**
     * Returns numeric monthly average price or null if not parseable.
     */
    val monthlyAveragePriceValue: Double?
        get() = monthlyAveragePrice.replace(",", "").toDoubleOrNull()

    /**
     * Returns numeric change value (positive = gain, negative = loss).
     */
    val changeValue: Double?
        get() = change.replace(",", "").replace("+", "").toDoubleOrNull()

    /**
     * True when closing price is above monthly average price.
     */
    val isAboveMonthlyAverage: Boolean?
        get() {
            val cp = closingPriceValue ?: return null
            val ma = monthlyAveragePriceValue ?: return null
            return cp > ma
        }

    /**
     * True when change is positive.
     */
    val isChangePositive: Boolean?
        get() {
            val c = changeValue ?: return null
            return c > 0
        }
}
