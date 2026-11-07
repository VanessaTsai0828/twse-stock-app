package com.example.bankapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.domain.model.SortField
import com.example.bankapp.domain.model.SortOrder
import com.example.bankapp.domain.model.StockItem
import com.example.bankapp.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the stock list screen.
 */
sealed interface StockUiState {
    data object Loading : StockUiState
    data class Success(val stocks: List<StockItem>) : StockUiState
    data class Error(val message: String) : StockUiState
}

/**
 * ViewModel for the stock list screen.
 * Handles data fetching, sorting, filtering, and UI state management.
 */
@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _rawStocks = MutableStateFlow<List<StockItem>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val _sortField = MutableStateFlow(SortField.CODE)
    val sortField: StateFlow<SortField> = _sortField

    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedStock = MutableStateFlow<StockItem?>(null)
    val selectedStock: StateFlow<StockItem?> = _selectedStock

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet

    /**
     * Combined UI state derived from raw data, sort config, and search query.
     */
    val uiState: StateFlow<StockUiState> = combine(
        _rawStocks, _isLoading, _error, _sortField, _sortOrder, _searchQuery
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val stocks = values[0] as List<StockItem>
        val isLoading = values[1] as Boolean
        val error = values[2] as String?
        val sortField = values[3] as SortField
        val sortOrder = values[4] as SortOrder
        val query = values[5] as String

        when {
            isLoading -> StockUiState.Loading
            error != null -> StockUiState.Error(error)
            else -> {
                val filtered = if (query.isBlank()) {
                    stocks
                } else {
                    stocks.filter {
                        it.code.contains(query, ignoreCase = true) ||
                            it.name.contains(query, ignoreCase = true)
                    }
                }
                StockUiState.Success(filtered.sortedWith(sortField, sortOrder))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StockUiState.Loading
    )

    init {
        loadStocks()
    }

    fun loadStocks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            stockRepository.getStockItems()
                .onSuccess { stocks ->
                    _rawStocks.value = stocks
                    _isLoading.value = false
                }
                .onFailure { throwable ->
                    _error.value = throwable.message ?: "Unknown error occurred"
                    _isLoading.value = false
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortFieldSelected(field: SortField) {
        if (_sortField.value == field) {
            // Toggle order if same field selected
            _sortOrder.value = if (_sortOrder.value == SortOrder.DESCENDING) {
                SortOrder.ASCENDING
            } else {
                SortOrder.DESCENDING
            }
        } else {
            _sortField.value = field
            _sortOrder.value = SortOrder.DESCENDING
        }
        _showFilterSheet.value = false
    }

    fun onStockSelected(stock: StockItem) {
        _selectedStock.value = stock
    }

    fun onStockDetailDismissed() {
        _selectedStock.value = null
    }

    fun onFilterIconClicked() {
        _showFilterSheet.value = true
    }

    fun onFilterSheetDismissed() {
        _showFilterSheet.value = false
    }
}

/**
 * Extension function to sort a list of StockItem by the given field and order.
 */
private fun List<StockItem>.sortedWith(field: SortField, order: SortOrder): List<StockItem> {
    val comparator: Comparator<StockItem> = when (field) {
        SortField.CODE -> compareBy { it.code }
        SortField.NAME -> compareBy { it.name }
        SortField.CLOSING_PRICE -> compareBy { it.closingPriceValue ?: Double.MIN_VALUE }
        SortField.CHANGE -> compareBy { it.changeValue ?: Double.MIN_VALUE }
        SortField.TRADE_VOLUME -> compareBy {
            it.tradeVolume.replace(",", "").toLongOrNull() ?: Long.MIN_VALUE
        }
        SortField.MONTHLY_AVG_PRICE -> compareBy { it.monthlyAveragePriceValue ?: Double.MIN_VALUE }
        SortField.PE_RATIO -> compareBy {
            it.peRatio.replace(",", "").toDoubleOrNull() ?: Double.MIN_VALUE
        }
        SortField.DIVIDEND_YIELD -> compareBy {
            it.dividendYield.replace(",", "").toDoubleOrNull() ?: Double.MIN_VALUE
        }
        SortField.PB_RATIO -> compareBy {
            it.pbRatio.replace(",", "").toDoubleOrNull() ?: Double.MIN_VALUE
        }
    }

    return if (order == SortOrder.DESCENDING) {
        sortedWith(comparator.reversed())
    } else {
        sortedWith(comparator)
    }
}
