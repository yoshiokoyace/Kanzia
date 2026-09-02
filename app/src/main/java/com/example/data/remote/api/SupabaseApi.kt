package com.example.data.remote.api

import com.example.data.remote.model.SupabaseInvestmentDto
import com.example.data.remote.model.SupabaseStockDto
import com.example.data.remote.model.SupabaseTransactionDto
import com.example.data.remote.model.SupabaseUserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    // USERS
    @GET("rest/v1/users")
    suspend fun getUsersByEmail(
        @Query("email") emailFilter: String,
        @Query("select") select: String = "*"
    ): List<SupabaseUserDto>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/users")
    suspend fun insertUser(
        @Body user: SupabaseUserDto
    ): List<SupabaseUserDto>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/users")
    suspend fun updateUserPassword(
        @Query("email") emailFilter: String,
        @Body update: Map<String, String>
    ): List<SupabaseUserDto>

    // TRANSACTIONS
    @GET("rest/v1/transactions")
    suspend fun getTransactions(
        @Query("select") select: String = "*",
        @Query("order") order: String = "timestamp.desc"
    ): List<SupabaseTransactionDto>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/transactions")
    suspend fun insertTransaction(
        @Body transaction: SupabaseTransactionDto
    ): List<SupabaseTransactionDto>

    @DELETE("rest/v1/transactions")
    suspend fun deleteTransaction(
        @Query("id") idFilter: String
    ): Response<Unit>

    // INVESTMENTS
    @GET("rest/v1/investments")
    suspend fun getInvestments(
        @Query("select") select: String = "*",
        @Query("order") order: String = "timestamp.desc"
    ): List<SupabaseInvestmentDto>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/investments")
    suspend fun insertInvestment(
        @Body investment: SupabaseInvestmentDto
    ): List<SupabaseInvestmentDto>

    @DELETE("rest/v1/investments")
    suspend fun deleteInvestment(
        @Query("id") idFilter: String
    ): Response<Unit>

    // STOCKS
    @GET("rest/v1/stocks")
    suspend fun getStocks(
        @Query("select") select: String = "*"
    ): List<SupabaseStockDto>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/stocks")
    suspend fun insertStock(
        @Body stock: SupabaseStockDto
    ): List<SupabaseStockDto>

    @DELETE("rest/v1/stocks")
    suspend fun deleteStock(
        @Query("ticker") tickerFilter: String
    ): Response<Unit>
}
