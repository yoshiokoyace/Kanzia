package com.example.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.CatEntertainment
import com.example.ui.theme.CatFood
import com.example.ui.theme.CatHealth
import com.example.ui.theme.CatHousing
import com.example.ui.theme.CatIncome
import com.example.ui.theme.CatOther
import com.example.ui.theme.CatShopping
import com.example.ui.theme.CatTransport
import com.example.ui.theme.CatUtilities

data class CategoryMeta(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

object Categories {
    val EXPENSE_CATEGORIES = listOf(
        CategoryMeta("Food & Dining", Icons.Filled.Restaurant, CatFood),
        CategoryMeta("Transport", Icons.Filled.DirectionsCar, CatTransport),
        CategoryMeta("Housing", Icons.Filled.Home, CatHousing),
        CategoryMeta("Utilities", Icons.Filled.FlashOn, CatUtilities),
        CategoryMeta("Shopping", Icons.Filled.ShoppingBag, CatShopping),
        CategoryMeta("Health", Icons.Filled.LocalHospital, CatHealth),
        CategoryMeta("Entertainment", Icons.Filled.Movie, CatEntertainment),
        CategoryMeta("Other", Icons.Filled.MoreHoriz, CatOther)
    )

    val INCOME_CATEGORIES = listOf(
        CategoryMeta("Salary", Icons.Filled.AccountBalanceWallet, CatIncome),
        CategoryMeta("Freelance", Icons.Filled.Work, CatIncome),
        CategoryMeta("Gift", Icons.Filled.CardGiftcard, CatIncome),
        CategoryMeta("Investment", Icons.Filled.TrendingUp, CatIncome),
        CategoryMeta("Other", Icons.Filled.MoreHoriz, CatIncome)
    )

    fun getCategoryMeta(type: String, categoryName: String): CategoryMeta {
        val list = if (type.lowercase() == "expense") EXPENSE_CATEGORIES else INCOME_CATEGORIES
        return list.find { it.name.equals(categoryName, ignoreCase = true) }
            ?: (if (type.lowercase() == "expense") CategoryMeta(categoryName, Icons.Filled.MoreHoriz, CatOther)
                else CategoryMeta(categoryName, Icons.Filled.MoreHoriz, CatIncome))
    }
}
