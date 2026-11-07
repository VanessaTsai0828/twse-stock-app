package com.example.bankapp

import app.cash.turbine.test
import com.example.bankapp.domain.model.SortField
import com.example.bankapp.domain.model.SortOrder
import com.example.bankapp.domain.model.StockItem
import com.example.bankapp.domain.repository.StockRepository
import com.example.bankapp.ui.viewmodel.StockUiState
import com.example.bankapp.ui.viewmodel.StockViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: StockRepository
    private lateinit var viewModel: StockViewModel

    private val sampleStocks = listOf(
        buildStockItem(code = "2330", name = "台積電", closingPrice = "583", monthlyAvg = "550", change = "10"),
        buildStockItem(code = "2317", name = "鴻海", closingPrice = "100", monthlyAvg = "110", change = "-2"),
        buildStockItem(code = "2454", name = "聯發科", closingPrice = "850", monthlyAvg = "900", change = "-15")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        coEvery { repository.getStockItems() } returns Result.success(emptyList())
        viewModel = StockViewModel(repository)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertTrue(initial is StockUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Success when repository succeeds`() = runTest {
        coEvery { repository.getStockItems() } returns Result.success(sampleStocks)
        viewModel = StockViewModel(repository)

        viewModel.uiState.test {
            skipItems(1) // skip Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val success = awaitItem()
            assertTrue(success is StockUiState.Success)
            assertEquals(3, (success as StockUiState.Success).stocks.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Error when repository fails`() = runTest {
        coEvery { repository.getStockItems() } returns Result.failure(RuntimeException("Network error"))
        viewModel = StockViewModel(repository)

        viewModel.uiState.test {
            skipItems(1) // skip Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertTrue(errorState is StockUiState.Error)
            assertEquals("Network error", (errorState as StockUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query filters stocks by code`() = runTest {
        coEvery { repository.getStockItems() } returns Result.success(sampleStocks)
        viewModel = StockViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("2330")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is StockUiState.Success) {
                assertEquals(1, state.stocks.size)
                assertEquals("2330", state.stocks.first().code)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query filters stocks by name`() = runTest {
        coEvery { repository.getStockItems() } returns Result.success(sampleStocks)
        viewModel = StockViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("鴻海")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is StockUiState.Success) {
                assertEquals(1, state.stocks.size)
                assertEquals("鴻海", state.stocks.first().name)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sorting toggles order when same field selected twice`() = runTest {
        coEvery { repository.getStockItems() } returns Result.success(sampleStocks)
        viewModel = StockViewModel(repository)

        // First selection: field changes, order becomes DESCENDING
        viewModel.onSortFieldSelected(SortField.CLOSING_PRICE)
        assertEquals(SortField.CLOSING_PRICE, viewModel.sortField.value)
        assertEquals(SortOrder.DESCENDING, viewModel.sortOrder.value)

        // Second selection: same field, order toggles to ASCENDING
        viewModel.onSortFieldSelected(SortField.CLOSING_PRICE)
        assertEquals(SortField.CLOSING_PRICE, viewModel.sortField.value)
        assertEquals(SortOrder.ASCENDING, viewModel.sortOrder.value)
    }

    @Test
    fun `selecting stock updates selectedStock state`() = runTest {
        coEvery { repository.getStockItems() } returns Result.success(sampleStocks)
        viewModel = StockViewModel(repository)

        val stock = sampleStocks.first()
        viewModel.onStockSelected(stock)
        assertEquals(stock, viewModel.selectedStock.value)
    }

    @Test
    fun `dismissing stock detail clears selectedStock`() = runTest {
        coEvery { repository.getStockItems() } returns Result.success(sampleStocks)
        viewModel = StockViewModel(repository)

        viewModel.onStockSelected(sampleStocks.first())
        viewModel.onStockDetailDismissed()
        assertEquals(null, viewModel.selectedStock.value)
    }

    private fun buildStockItem(
        code: String,
        name: String,
        closingPrice: String,
        monthlyAvg: String,
        change: String
    ) = StockItem(
        code = code, name = name,
        tradeVolume = "1000", tradeValue = "100000",
        openingPrice = closingPrice, highestPrice = closingPrice,
        lowestPrice = closingPrice, closingPrice = closingPrice,
        change = change, transaction = "100",
        monthlyAveragePrice = monthlyAvg,
        peRatio = "15", dividendYield = "3", pbRatio = "2"
    )
}
