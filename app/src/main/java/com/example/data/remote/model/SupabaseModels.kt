package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseUserDto(
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "password_hash") val passwordHash: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseTransactionDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "type") val type: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "category") val category: String,
    @Json(name = "note") val note: String = "",
    @Json(name = "date") val date: String,
    @Json(name = "timestamp") val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class SupabaseInvestmentDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "stock") val stock: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "date") val date: String,
    @Json(name = "timestamp") val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class SupabaseStockDto(
    @Json(name = "ticker") val ticker: String,
    @Json(name = "name") val name: String
)
