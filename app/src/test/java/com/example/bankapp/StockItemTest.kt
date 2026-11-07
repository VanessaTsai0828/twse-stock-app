package com.example.bankapp

import com.example.bankapp.domain.model.StockItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [StockItem] domain model computed properties.
 */
class StockItemTest {

    private fun buildStock(
        closingPrice: String = "100.00",
        monthlyAveragePrice: String = "95.00",
        change: String = "2.50"
    ) = StockItem(
        code = "2330",
        name = "台積電",
        tradeVolume = "50000000",
        tradeValue = "2500000000",
        openingPrice = "98.00",
        highestPrice = "101.00",
        lowestPrice = "97.00",
        closingPrice = closingPrice,
        change = change,
        transaction = "12345",
        monthlyAveragePrice = monthlyAveragePrice,
        peRatio = "15.2",
        dividendYield = "3.5",
        pbRatio = "2.1"
    )

    // --- closingPriceValue ---

    @Test
    fun `closingPriceValue returns correct double for valid price`() {
        val stock = buildStock(closingPrice = "583.00")
        assertEquals(583.00, stock.closingPriceValue!!, 0.001)
    }

    @Test
    fun `closingPriceValue handles comma formatted price`() {
        val stock = buildStock(closingPrice = "1,234.50")
        assertEquals(1234.50, stock.closingPriceValue!!, 0.001)
    }

    @Test
    fun `closingPriceValue returns null for blank price`() {
        val stock = buildStock(closingPrice = "")
        assertNull(stock.closingPriceValue)
    }

    @Test
    fun `closingPriceValue returns null for non-numeric price`() {
        val stock = buildStock(closingPrice = "--")
        assertNull(stock.closingPriceValue)
    }

    // --- isAboveMonthlyAverage ---

    @Test
    fun `isAboveMonthlyAverage returns true when closing price above monthly average`() {
        val stock = buildStock(closingPrice = "100.00", monthlyAveragePrice = "95.00")
        assertTrue(stock.isAboveMonthlyAverage!!)
    }

    @Test
    fun `isAboveMonthlyAverage returns false when closing price below monthly average`() {
        val stock = buildStock(closingPrice = "90.00", monthlyAveragePrice = "95.00")
        assertFalse(stock.isAboveMonthlyAverage!!)
    }

    @Test
    fun `isAboveMonthlyAverage returns null when monthly average is blank`() {
        val stock = buildStock(closingPrice = "100.00", monthlyAveragePrice = "")
        assertNull(stock.isAboveMonthlyAverage)
    }

    @Test
    fun `isAboveMonthlyAverage returns false when prices are equal`() {
        val stock = buildStock(closingPrice = "95.00", monthlyAveragePrice = "95.00")
        assertFalse(stock.isAboveMonthlyAverage!!)
    }

    // --- changeValue ---

    @Test
    fun `changeValue returns positive double for gain`() {
        val stock = buildStock(change = "5.00")
        assertEquals(5.00, stock.changeValue!!, 0.001)
    }

    @Test
    fun `changeValue returns negative double for loss`() {
        val stock = buildStock(change = "-3.50")
        assertEquals(-3.50, stock.changeValue!!, 0.001)
    }

    @Test
    fun `changeValue handles plus prefix`() {
        val stock = buildStock(change = "+2.00")
        assertEquals(2.00, stock.changeValue!!, 0.001)
    }

    @Test
    fun `changeValue returns null for non-numeric change`() {
        val stock = buildStock(change = "--")
        assertNull(stock.changeValue)
    }

    // --- isChangePositive ---

    @Test
    fun `isChangePositive returns true for positive change`() {
        val stock = buildStock(change = "3.00")
        assertTrue(stock.isChangePositive!!)
    }

    @Test
    fun `isChangePositive returns false for negative change`() {
        val stock = buildStock(change = "-1.50")
        assertFalse(stock.isChangePositive!!)
    }

    @Test
    fun `isChangePositive returns false for zero change`() {
        val stock = buildStock(change = "0")
        assertFalse(stock.isChangePositive!!)
    }

    @Test
    fun `isChangePositive returns null for non-numeric change`() {
        val stock = buildStock(change = "")
        assertNull(stock.isChangePositive)
    }
}
