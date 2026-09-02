package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.InvestmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments ORDER BY date DESC, id DESC")
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: InvestmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestments(investments: List<InvestmentEntity>)

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteInvestment(id: Long)

    @Query("DELETE FROM investments")
    suspend fun deleteAllInvestments()
}
