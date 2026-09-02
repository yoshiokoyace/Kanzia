package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.InvestmentDao
import com.example.data.local.dao.StockDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.InvestmentEntity
import com.example.data.local.entity.StockEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        InvestmentEntity::class,
        StockEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun stockDao(): StockDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kanzia_ledger_v3.db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
