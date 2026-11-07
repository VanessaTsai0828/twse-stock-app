package com.example.bankapp

import com.example.bankapp.data.remote.TwseApiService
import com.example.bankapp.data.remote.model.StockAvgPriceDto
import com.example.bankapp.data.remote.model.StockDayDto
import com.example.bankapp.data.remote.model.StockValuationDto
import com.example.bankapp.data.repository.StockRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [StockRepositoryImpl] data merging logic.
 */
class StockRepositoryTest {

    private lateinit var apiService: TwseApiService
    private lateinit var repository: StockRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = StockRepositoryImpl(apiService)
    }

    @Test
    fun `getStockItems merges data by code correctly`() = runTest {
        // Given
        coEvery { apiService.getStockDayInfo() } returns listOf(
            StockDayDto(
                code = "2330", name = "台積電",
                tradeVolume = "50000", tradeValue = "2500000",
                openingPrice = "498", highestPrice = "502",
                lowestPrice = "496", closingPrice = "500",
                change = "2", transaction = "1000"
            )
        )
        coEvery { apiService.getStockAvgPrices() } returns listOf(
            StockAvgPriceDto(
                code = "2330", name = "台積電",
                closingPrice = "500", monthlyAveragePrice = "490"
            )
        )
        coEvery { apiService.getStockValuations() } returns listOf(
            StockValuationDto(
                code = "2330", name = "台積電",
                peRatio = "20.5", dividendYield = "2.3", pbRatio = "3.1"
            )
        )

        // When
        val result = repository.getStockItems()

        // Then
        assertTrue(result.isSuccess)
        val stocks = result.getOrThrow()
        assertEquals(1, stocks.size)

        val stock = stocks.first()
        assertEquals("2330", stock.code)
        assertEquals("台積電", stock.name)
        assertEquals("490", stock.monthlyAveragePrice)
        assertEquals("20.5", stock.peRatio)
        assertEquals("2.3", stock.dividendYield)
        assertEquals("3.1", stock.pbRatio)
        assertEquals("500", stock.closingPrice)
    }

    @Test
    fun `getStockItems handles missing valuation data gracefully`() = runTest {
        // Given
        coEvery { apiService.getStockDayInfo() } returns listOf(
            StockDayDto(
                code = "9999", name = "測試股",
                tradeVolume = "1000", tradeValue = "100000",
                openingPrice = "100", highestPrice = "105",
                lowestPrice = "99", closingPrice = "102",
                change = "2", transaction = "50"
            )
        )
        coEvery { apiService.getStockAvgPrices() } returns emptyList()
        coEvery { apiService.getStockValuations() } returns emptyList()

        // When
        val result = repository.getStockItems()

        // Then
        assertTrue(result.isSuccess)
        val stocks = result.getOrThrow()
        assertEquals(1, stocks.size)

        val stock = stocks.first()
        assertEquals("", stock.monthlyAveragePrice)
        assertEquals("", stock.peRatio)
        assertEquals("", stock.dividendYield)
        assertEquals("", stock.pbRatio)
    }

    @Test
    fun `getStockItems returns failure when API throws exception`() = runTest {
        // Given
        coEvery { apiService.getStockDayInfo() } throws RuntimeException("Network error")

        // When
        val result = repository.getStockItems()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getStockItems returns empty list when API returns empty`() = runTest {
        // Given
        coEvery { apiService.getStockDayInfo() } returns emptyList()
        coEvery { apiService.getStockAvgPrices() } returns emptyList()
        coEvery { apiService.getStockValuations() } returns emptyList()

        // When
        val result = repository.getStockItems()

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }
}
