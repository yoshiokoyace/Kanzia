package com.example.data.repository

import com.example.data.local.dao.InvestmentDao
import com.example.data.local.dao.StockDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.InvestmentEntity
import com.example.data.local.entity.StockEntity
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val investmentDao: InvestmentDao,
    private val stockDao: StockDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allInvestments: Flow<List<InvestmentEntity>> = investmentDao.getAllInvestments()
    val allStocks: Flow<List<StockEntity>> = stockDao.getAllStocks()

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransaction(id)
    }

    suspend fun addInvestment(investment: InvestmentEntity): Long {
        return investmentDao.insertInvestment(investment)
    }

    suspend fun deleteInvestment(id: Long) {
        investmentDao.deleteInvestment(id)
    }

    suspend fun addStock(stock: StockEntity) {
        stockDao.insertStock(stock)
    }

    suspend fun deleteStock(ticker: String) {
        stockDao.deleteStock(ticker)
    }
}
