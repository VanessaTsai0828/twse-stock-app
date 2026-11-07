# 📈 台股總覽 App (TWSE Stock Viewer)

[![Android Studio](https://img.shields.io/badge/Android%20Studio-2024.1+-green.svg)](https://developer.android.com/studio)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%2BClean-orange.svg)](https://developer.android.com/topic/architecture)

一款基於 **Modern Android Development** 開發的台灣股市行情檢視 Android 應用程式。整合台灣證券交易所（TWSE）OpenAPI，提供即時個股資訊查詢、關鍵字搜尋與多維度排序篩選。

---

## ✨ 主要功能 (Features)

* 📊 **台股總覽列表**：實時展示上市個股之收盤價、漲跌、月均價、當日最高/最低價、成交量、成交筆數與成交金額。
* 🔍 **即時關鍵字搜尋**：支援輸入股票代號（例如 `2330`）或股票名稱（例如 `台積電`）進行快速過濾與檢視。
* 🔀 **多維度排序與篩選**：支援按股票代號、收盤價、當日漲跌、成交量、月均價、本益比、殖利率等指標進行升序/降序排序。
* 📋 **個股詳細資訊與財務指標**：點擊卡片彈出 Dialog，顯示該個股完整之財務數據（包含本益比、殖利率、股價淨值比等）。
* 🔄 **一鍵重新載入**：支援頂部按鈕即時重新抓取 TWSE 最新資料。
* 🎨 **Material 3 現代化介面**：採用現代化卡片設計、過渡動畫與流暢的交錯動畫效果。

---

## 🛠️ 技術棧與架構 (Tech Stack & Architecture)

本專案嚴格遵循 Google 推薦的 **Android 最佳實踐** 與 **Clean Architecture** 雙層架構：

```
app/
 ├── data/               # 資料層 (Data Layer)
 │    ├── remote/        # TWSE Retrofit API 介面與 DTO 模型
 │    └── repository/    # StockRepositoryImpl 實現與數據整合
 ├── domain/             # 領域層 (Domain Layer)
 │    ├── model/         # StockItem 核心領域模型與排序列舉
 │    └── repository/    # StockRepository 介面定義
 ├── di/                 # 依賴注入 (Hilt Dependency Injection)
 └── ui/                 # 展示層 (UI Layer)
      ├── component/     # StockCard, StockDetailDialog, SortFilterBottomSheet
      ├── screen/        # StockListScreen (Compose)
      ├── viewmodel/     # StockViewModel & StockUiState
      └── theme/         # Material 3 Color, Type, Theme
```

| 元件/技術 | 說明 |
| :--- | :--- |
| **Language** | 100% Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture + Repository Pattern |
| **Dependency Injection** | Hilt (Dagger Hilt) |
| **Async & Reactive** | Kotlin Coroutines + StateFlow / Flow |
| **Networking** | Retrofit 2 + Gson Converter |
| **Lifecycle** | Android Architecture Components (ViewModel, StateFlow) |
| **Unit Testing** | JUnit 4 + MockK + Coroutines Test |

---

## 🌐 API 資料來源 (Data Source)

本專案資料來自 **台灣證券交易所 (TWSE) OpenAPI**：
1. **STOCK_DAY_ALL**：個股日成交資訊（包含收盤價、成交量、最高/最低價等）。
2. **STOCK_AVG_ALL**：個股收盤月平均價。
3. **BWIBBU_ALL**：個股日本益比、殖利率及股價淨值比。

---

## 🚀 如何運行本專案 (Getting Started)

### 先決條件 (Prerequisites)
* Android Studio Jellyfish (2024.1.1) 或更新版本
* JDK 17+
* Android 8.0 (API Level 26) 以上之裝置或模擬器

### 執行步驟 (Steps)
1. Clone 本專案：
   ```bash
   git clone https://github.com/VanessaTsai0828/twse-stock-app.git
   ```
2. 使用 Android Studio 打開 `twse-stock-app` 資料夾。
3. 等待 Gradle Sync 完成。
4. 點擊 `Run 'app'` (▶️) 即可在模擬器或實機上運行！

---

## 🧪 單元測試 (Unit Tests)

專案包含完整的 ViewModel 與 Repository 單元測試，可透過以下 Command 執行：
```bash
./gradlew test
```

---

## 📄 授權 (License)

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
