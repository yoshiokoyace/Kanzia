package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.InvestmentEntity
import com.example.data.local.entity.StockEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.FinanceRepository
import com.example.ui.components.AuthMode
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
    val authRepository: AuthRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FinanceRepository(db.transactionDao(), db.investmentDao(), db.stockDao())
        authRepository = AuthRepository(db.userDao(), application)

        // Clear any old sample starter data so the user starts fresh with their actual data
        viewModelScope.launch {
            val prefs = application.getSharedPreferences("finance_cleanup_prefs", Application.MODE_PRIVATE)
            val cleanedSampleData = prefs.getBoolean("sample_data_cleared_v1", false)
            if (!cleanedSampleData) {
                repository.clearAllLocalTransactionsAndInvestments()
                prefs.edit().putBoolean("sample_data_cleared_v1", true).apply()
            }
            // Sync with Supabase on startup
            repository.syncWithSupabase()
        }
    }

    val isSyncing: StateFlow<Boolean> = repository.isSyncing
    val isSupabaseConfigured: Boolean get() = repository.isSupabaseConfigured

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val hasLaunchedBefore: StateFlow<Boolean> = authRepository.hasLaunchedBefore
    val savedUserEmail: String? get() = authRepository.savedUserEmail

    private val _showAuthModal = MutableStateFlow(false)
    val showAuthModal: StateFlow<Boolean> = _showAuthModal.asStateFlow()

    private val _authInitialMode = MutableStateFlow(AuthMode.LOGIN)
    val authInitialMode: StateFlow<AuthMode> = _authInitialMode.asStateFlow()

    private val _isAuthModalDismissable = MutableStateFlow(true)
    val isAuthModalDismissable: StateFlow<Boolean> = _isAuthModalDismissable.asStateFlow()

    private val _showProfileSheet = MutableStateFlow(false)
    val showProfileSheet: StateFlow<Boolean> = _showProfileSheet.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

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

    fun openAuthModal(initialMode: AuthMode = AuthMode.LOGIN, isDismissable: Boolean = true) {
        _authInitialMode.value = initialMode
        _isAuthModalDismissable.value = isDismissable
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
        _showAuthModal.value = true
    }

    fun closeAuthModal() {
        _showAuthModal.value = false
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
    }

    fun openProfileSheet() {
        _showProfileSheet.value = true
    }

    fun closeProfileSheet() {
        _showProfileSheet.value = false
    }

    fun clearAuthMessages() {
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            _authSuccessMessage.value = null

            val result = authRepository.login(email, pass)
            _isAuthLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _authSuccessMessage.value = result.message
                    _showAuthModal.value = false
                    repository.syncWithSupabase()
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authErrorMessage.value = result.message
                }
            }
        }
    }

    fun register(name: String, email: String, pass: String, confirmPass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            _authSuccessMessage.value = null

            val result = authRepository.register(name, email, pass, confirmPass)
            _isAuthLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _authSuccessMessage.value = result.message
                    _showAuthModal.value = false
                    repository.syncWithSupabase()
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authErrorMessage.value = result.message
                }
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }

    fun resetPassword(email: String, newPass: String, confirmPass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            _authSuccessMessage.value = null

            val result = authRepository.resetPassword(email, newPass, confirmPass)
            _isAuthLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _authSuccessMessage.value = result.message
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authErrorMessage.value = result.message
                }
            }
        }
    }

    fun lockSession() {
        authRepository.lockSession()
        _showProfileSheet.value = false
        _showAuthModal.value = false
    }

    fun logout() {
        authRepository.logout()
        _showProfileSheet.value = false
        _showAuthModal.value = false
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllLocalTransactionsAndInvestments()
        }
    }
}
