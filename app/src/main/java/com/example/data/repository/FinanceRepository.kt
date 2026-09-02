package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.InvestmentDao
import com.example.data.local.dao.StockDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.InvestmentEntity
import com.example.data.local.entity.StockEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.remote.SupabaseClient
import com.example.data.remote.model.SupabaseInvestmentDto
import com.example.data.remote.model.SupabaseStockDto
import com.example.data.remote.model.SupabaseTransactionDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val investmentDao: InvestmentDao,
    private val stockDao: StockDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val TAG = "FinanceRepository"

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allInvestments: Flow<List<InvestmentEntity>> = investmentDao.getAllInvestments()
    val allStocks: Flow<List<StockEntity>> = stockDao.getAllStocks()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    val isSupabaseConfigured: Boolean
        get() = SupabaseClient.isConfigured

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        val id = transactionDao.insertTransaction(transaction)
        val transactionWithId = transaction.copy(id = id)

        // Sync to Supabase in background
        if (SupabaseClient.isConfigured) {
            scope.launch {
                try {
                    val dto = SupabaseTransactionDto(
                        id = if (id > 0) id else null,
                        type = transactionWithId.type,
                        amount = transactionWithId.amount,
                        category = transactionWithId.category,
                        note = transactionWithId.note,
                        date = transactionWithId.date,
                        timestamp = transactionWithId.timestamp
                    )
                    SupabaseClient.api?.insertTransaction(dto)
                    Log.d(TAG, "Successfully synced transaction $id to Supabase")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not sync transaction to Supabase: ${e.message}")
                }
            }
        }
        return id
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransaction(id)

        // Delete from Supabase in background
        if (SupabaseClient.isConfigured) {
            scope.launch {
                try {
                    SupabaseClient.api?.deleteTransaction("eq.$id")
                    Log.d(TAG, "Deleted transaction $id from Supabase")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not delete transaction from Supabase: ${e.message}")
                }
            }
        }
    }

    suspend fun addInvestment(investment: InvestmentEntity): Long {
        val id = investmentDao.insertInvestment(investment)
        val investmentWithId = investment.copy(id = id)

        // Sync to Supabase in background
        if (SupabaseClient.isConfigured) {
            scope.launch {
                try {
                    val dto = SupabaseInvestmentDto(
                        id = if (id > 0) id else null,
                        stock = investmentWithId.stock,
                        amount = investmentWithId.amount,
                        date = investmentWithId.date,
                        timestamp = investmentWithId.timestamp
                    )
                    SupabaseClient.api?.insertInvestment(dto)
                    Log.d(TAG, "Successfully synced investment $id to Supabase")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not sync investment to Supabase: ${e.message}")
                }
            }
        }
        return id
    }

    suspend fun deleteInvestment(id: Long) {
        investmentDao.deleteInvestment(id)

        // Delete from Supabase in background
        if (SupabaseClient.isConfigured) {
            scope.launch {
                try {
                    SupabaseClient.api?.deleteInvestment("eq.$id")
                    Log.d(TAG, "Deleted investment $id from Supabase")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not delete investment from Supabase: ${e.message}")
                }
            }
        }
    }

    suspend fun addStock(stock: StockEntity) {
        stockDao.insertStock(stock)

        // Sync to Supabase in background
        if (SupabaseClient.isConfigured) {
            scope.launch {
                try {
                    val dto = SupabaseStockDto(
                        ticker = stock.ticker,
                        name = stock.name
                    )
                    SupabaseClient.api?.insertStock(dto)
                    Log.d(TAG, "Successfully synced stock ${stock.ticker} to Supabase")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not sync stock to Supabase: ${e.message}")
                }
            }
        }
    }

    suspend fun deleteStock(ticker: String) {
        stockDao.deleteStock(ticker)

        if (SupabaseClient.isConfigured) {
            scope.launch {
                try {
                    SupabaseClient.api?.deleteStock("eq.$ticker")
                    Log.d(TAG, "Deleted stock $ticker from Supabase")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not delete stock from Supabase: ${e.message}")
                }
            }
        }
    }

    suspend fun syncWithSupabase() {
        if (!SupabaseClient.isConfigured) return
        val api = SupabaseClient.api ?: return

        _isSyncing.value = true
        try {
            // Pull transactions
            val remoteTransactions = api.getTransactions()
            if (remoteTransactions.isNotEmpty()) {
                val entities = remoteTransactions.map { dto ->
                    TransactionEntity(
                        id = dto.id ?: 0,
                        type = dto.type,
                        amount = dto.amount,
                        category = dto.category,
                        note = dto.note,
                        date = dto.date,
                        timestamp = dto.timestamp
                    )
                }
                transactionDao.insertTransactions(entities)
            }

            // Pull investments
            val remoteInvestments = api.getInvestments()
            if (remoteInvestments.isNotEmpty()) {
                val entities = remoteInvestments.map { dto ->
                    InvestmentEntity(
                        id = dto.id ?: 0,
                        stock = dto.stock,
                        amount = dto.amount,
                        date = dto.date,
                        timestamp = dto.timestamp
                    )
                }
                investmentDao.insertInvestments(entities)
            }

            // Pull stocks
            val remoteStocks = api.getStocks()
            if (remoteStocks.isNotEmpty()) {
                val entities = remoteStocks.map { dto ->
                    StockEntity(ticker = dto.ticker, name = dto.name)
                }
                stockDao.insertDefaultStocks(entities)
            }

            Log.d(TAG, "Supabase sync complete!")
        } catch (e: Exception) {
            Log.w(TAG, "Supabase sync encountered exception: ${e.message}")
        } finally {
            _isSyncing.value = false
        }
    }

    suspend fun clearAllLocalTransactionsAndInvestments() {
        transactionDao.deleteAllTransactions()
        investmentDao.deleteAllInvestments()
    }
}
