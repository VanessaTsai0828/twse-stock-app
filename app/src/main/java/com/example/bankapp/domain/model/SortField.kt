package com.example.bankapp.domain.model

/**
 * Enumeration of available sort fields for stock list.
 */
enum class SortField(val displayName: String) {
    CODE("股票代號"),
    NAME("股票名稱"),
    CLOSING_PRICE("收盤價"),
    CHANGE("漲跌價差"),
    TRADE_VOLUME("成交股數"),
    MONTHLY_AVG_PRICE("月平均價"),
    PE_RATIO("本益比"),
    DIVIDEND_YIELD("殖利率"),
    PB_RATIO("股價淨值比")
}

enum class SortOrder {
    ASCENDING, DESCENDING
}
