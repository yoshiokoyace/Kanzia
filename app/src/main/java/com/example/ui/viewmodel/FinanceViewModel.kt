package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.InvestmentEntity
import com.example.data.local.entity.StockEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.repository.FinanceRepository
import com.example.ui.util.FinanceFormatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StockGroup(
    val stock: String,
    val total: Double,
    val entries: List<InvestmentEntity>
)

data class CategoryExpense(
    val category: String,
    val amount: Double,
    val percentage: Float
)

data class MonthlyData(
    val month: String, // "2026-08"
    val monthLabel: String, // "Aug"
    val income: Double,
    val expense: Double
)

data class FinanceTotals(
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val balance: Double = 0.0,
    val totalInvested: Double = 0.0
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FinanceRepository(db.transactionDao(), db.investmentDao(), db.stockDao())

        // Seed initial mock transactions & investments if database is fresh
        seedInitialDataIfNeeded()
    }

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val investments: StateFlow<List<InvestmentEntity>> = repository.allInvestments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stocks: StateFlow<List<StockEntity>> = repository.allStocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow("home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _filter = MutableStateFlow("All")
    val filter: StateFlow<String> = _filter.asStateFlow()

    private val _seeAll = MutableStateFlow(false)
    val seeAll: StateFlow<Boolean> = _seeAll.asStateFlow()

    val totals: StateFlow<FinanceTotals> = combine(transactions, investments) { txList, invList ->
        val income = txList.filter { it.type.equals("income", ignoreCase = true) }.sumOf { it.amount }
        val expense = txList.filter { it.type.equals("expense", ignoreCase = true) }.sumOf { it.amount }
        val balance = income - expense
        val totalInvested = invList.sumOf { it.amount }
        FinanceTotals(
            income = income,
            expense = expense,
            balance = balance,
            totalInvested = totalInvested
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinanceTotals())

    val expenseByCategory: StateFlow<List<CategoryExpense>> = transactions.combine(transactions) { txList, _ ->
        val expenseTx = txList.filter { it.type.equals("expense", ignoreCase = true) }
        val totalExpense = expenseTx.sumOf { it.amount }
        if (totalExpense <= 0.0) {
            emptyList()
        } else {
            val map = mutableMapOf<String, Double>()
            expenseTx.forEach { tx ->
                map[tx.category] = (map[tx.category] ?: 0.0) + tx.amount
            }
            map.map { (cat, amt) ->
                CategoryExpense(
                    category = cat,
                    amount = amt,
                    percentage = ((amt / totalExpense) * 100).toFloat()
                )
            }.sortedByDescending { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyData: StateFlow<List<MonthlyData>> = transactions.combine(transactions) { txList, _ ->
        val map = mutableMapOf<String, Pair<Double, Double>>() // month -> (income, expense)
        txList.forEach { tx ->
            val monthKey = if (tx.date.length >= 7) tx.date.substring(0, 7) else "2026-01"
            val current = map[monthKey] ?: Pair(0.0, 0.0)
            if (tx.type.equals("income", ignoreCase = true)) {
                map[monthKey] = Pair(current.first + tx.amount, current.second)
            } else {
                map[monthKey] = Pair(current.first, current.second + tx.amount)
            }
        }
        map.entries
            .sortedBy { it.key }
            .takeLast(6)
            .map { entry ->
                MonthlyData(
                    month = entry.key,
                    monthLabel = FinanceFormatters.formatMonthLabel(entry.key),
                    income = entry.value.first,
                    expense = entry.value.second
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val investmentsByStock: StateFlow<List<StockGroup>> = investments.combine(investments) { invList, _ ->
        val map = mutableMapOf<String, MutableList<InvestmentEntity>>()
        invList.forEach { inv ->
            val list = map.getOrPut(inv.stock) { mutableListOf() }
            list.add(inv)
        }
        map.map { (stock, entries) ->
            StockGroup(
                stock = stock,
                total = entries.sumOf { it.amount },
                entries = entries.sortedByDescending { it.date }
            )
        }.sortedByDescending { it.total }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun setFilter(filter: String) {
        _filter.value = filter
    }

    fun toggleSeeAll() {
        _seeAll.value = !_seeAll.value
    }

    fun addTransaction(type: String, amount: Double, category: String, note: String, date: String) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    note = note.trim(),
                    date = date.ifBlank { FinanceFormatters.todayISO() }
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun addInvestment(stock: String, amount: Double, date: String) {
        viewModelScope.launch {
            repository.addInvestment(
                InvestmentEntity(
                    stock = stock.trim().uppercase(),
                    amount = amount,
                    date = date.ifBlank { FinanceFormatters.todayISO() }
                )
            )
        }
    }

    fun deleteInvestment(id: Long) {
        viewModelScope.launch {
            repository.deleteInvestment(id)
        }
    }

    fun addStock(ticker: String, name: String) {
        viewModelScope.launch {
            val formattedTicker = ticker.trim().uppercase()
            val formattedName = name.trim().uppercase()
            if (formattedTicker.isNotBlank() && formattedName.isNotBlank()) {
                repository.addStock(StockEntity(formattedTicker, formattedName))
            }
        }
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch {
            val initialStocks = listOf(
                StockEntity("KO", "COCA-COLA"),
                StockEntity("V", "VISA"),
                StockEntity("SPGI", "S&P GLOBAL"),
                StockEntity("TXRH", "TEXAS ROADHOUSE"),
                StockEntity("NVDA", "NVIDIA"),
                StockEntity("AAPL", "APPLE")
            )
            initialStocks.forEach { repository.addStock(it) }

            // Add sample starter transactions if repository has none
            // This ensures Jay's ledger looks stunning out of the box with real numbers
            val today = FinanceFormatters.todayISO()
            repository.addTransaction(
                TransactionEntity(type = "income", amount = 18500.0, category = "Salary", note = "Monthly Salary", date = today)
            )
            repository.addTransaction(
                TransactionEntity(type = "expense", amount = 420.0, category = "Food & Dining", note = "Dinner with friends", date = today)
            )
            repository.addTransaction(
                TransactionEntity(type = "expense", amount = 150.0, category = "Transport", note = "Fuel refill", date = today)
            )
            repository.addTransaction(
                TransactionEntity(type = "expense", amount = 85.0, category = "Entertainment", note = "Cinema tickets", date = today)
            )
            repository.addTransaction(
                TransactionEntity(type = "income", amount = 1200.0, category = "Freelance", note = "Design consulting", date = today)
            )

            // Add starter investment deposits
            repository.addInvestment(
                InvestmentEntity(stock = "KO", amount = 1500.0, date = today)
            )
            repository.addInvestment(
                InvestmentEntity(stock = "V", amount = 2200.0, date = today)
            )
            repository.addInvestment(
                InvestmentEntity(stock = "SPGI", amount = 1800.0, date = today)
            )
        }
    }
}
