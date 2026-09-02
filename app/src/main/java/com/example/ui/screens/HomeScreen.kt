package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.model.Categories
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedExpense
import com.example.ui.theme.SophisticatedHighlight
import com.example.ui.theme.SophisticatedIncome
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedPrimaryDark
import com.example.ui.theme.SophisticatedPrimaryGlow
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedSurfaceVariant
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextMutedDarker
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.util.FinanceFormatters
import com.example.ui.util.fadingEdge
import com.example.ui.viewmodel.FinanceTotals

@Composable
fun HomeScreen(
    totals: FinanceTotals,
    transactions: List<TransactionEntity>,
    currentUser: UserEntity?,
    isLoggedIn: Boolean,
    selectedFilter: String,
    seeAll: Boolean,
    onFilterSelect: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Unique categories used in transactions
    val usedCategories = remember(transactions) {
        transactions.map { it.category }.distinct()
    }

    val filteredList = remember(transactions, selectedFilter, seeAll) {
        val list = if (selectedFilter == "All") {
            transactions
        } else {
            transactions.filter { it.category.equals(selectedFilter, ignoreCase = true) }
        }
        if (seeAll) list else list.take(6)
    }

    val displayName = if (isLoggedIn && currentUser != null) currentUser.name else "Guest"
    val avatarInitial = displayName.take(1).uppercase()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fadingEdge(topFadeHeight = 16.dp, bottomFadeHeight = 36.dp)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Greeting & Profile Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${FinanceFormatters.getGreeting()} 👋",
                        color = SophisticatedTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = displayName,
                        color = SophisticatedTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Interactive Avatar Pill Badge with Glow & Click Action
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = SophisticatedPrimary.copy(alpha = 0.35f),
                            spotColor = SophisticatedPrimary.copy(alpha = 0.35f)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(SophisticatedPrimary, SophisticatedPrimaryDark)
                            )
                        )
                        .clickable { onProfileClick() }
                        .testTag("profile_avatar_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarInitial,
                        color = SophisticatedOnPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Hero Card: Total Balance & Income/Expense Sub-pills
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = SophisticatedPrimary.copy(alpha = 0.25f),
                        spotColor = SophisticatedPrimary.copy(alpha = 0.25f)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SophisticatedSurfaceContainer,
                                    SophisticatedSurface,
                                    SophisticatedSurfaceVariant
                                ),
                                radius = 700f
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = SophisticatedBorder.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Text(
                            text = "TOTAL BALANCE",
                            color = SophisticatedHighlight.copy(alpha = 0.8f),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FinanceFormatters.formatAED(totals.balance),
                            color = SophisticatedTextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Income & Expense Sub-pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Income Pill
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SophisticatedSurfaceVariant.copy(alpha = 0.7f))
                                    .border(1.dp, SophisticatedBorder.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SophisticatedPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SouthWest,
                                        contentDescription = "Income",
                                        tint = SophisticatedIncome,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Income",
                                        color = SophisticatedTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = FinanceFormatters.formatAED(totals.income),
                                        color = SophisticatedIncome,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Expenses Pill
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SophisticatedSurfaceVariant.copy(alpha = 0.7f))
                                    .border(1.dp, SophisticatedBorder.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SophisticatedExpense.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.NorthEast,
                                        contentDescription = "Expenses",
                                        tint = SophisticatedExpense,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Expenses",
                                        color = SophisticatedTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = FinanceFormatters.formatAED(totals.expense),
                                        color = SophisticatedExpense,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title & "See all" Action
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transactions",
                    color = SophisticatedTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSeeAllClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (seeAll) "Show less" else "See all",
                        color = SophisticatedPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "See all",
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        // Filter Chips Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" chip
                val isAll = selectedFilter == "All"
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isAll) SophisticatedPrimary else SophisticatedSurface)
                        .border(
                            1.dp,
                            if (isAll) SophisticatedPrimary else SophisticatedBorder.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .clickable { onFilterSelect("All") }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All",
                        color = if (isAll) SophisticatedOnPrimary else SophisticatedTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                usedCategories.forEach { categoryName ->
                    val isSelected = selectedFilter.equals(categoryName, ignoreCase = true)
                    val meta = Categories.getCategoryMeta(
                        if (Categories.EXPENSE_CATEGORIES.any { it.name.equals(categoryName, ignoreCase = true) }) "expense" else "income",
                        categoryName
                    )

                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) SophisticatedPrimary else SophisticatedSurface)
                            .border(
                                1.dp,
                                if (isSelected) SophisticatedPrimary else SophisticatedBorder.copy(alpha = 0.4f),
                                CircleShape
                            )
                            .clickable { onFilterSelect(categoryName) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = meta.icon,
                            contentDescription = categoryName,
                            tint = if (isSelected) SophisticatedOnPrimary else meta.color,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = categoryName,
                            color = if (isSelected) SophisticatedOnPrimary else SophisticatedTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Transactions List
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions yet — tap + to add one.",
                        color = SophisticatedTextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredList, key = { it.id }) { tx ->
                val meta = Categories.getCategoryMeta(tx.type, tx.category)
                val isIncome = tx.type.equals("income", ignoreCase = true)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_item_${tx.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Icon Circle
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(meta.color.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = meta.icon,
                                contentDescription = tx.category,
                                tint = meta.color,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Note & Category/Date
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.note.ifBlank { tx.category },
                                color = SophisticatedTextPrimary,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${tx.category} · ${FinanceFormatters.relativeDate(tx.date)}",
                                color = SophisticatedTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Amount (+ / - AED)
                        Text(
                            text = "${if (isIncome) "+" else "-"} ${FinanceFormatters.formatAED(tx.amount)}",
                            color = if (isIncome) SophisticatedIncome else SophisticatedExpense,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Delete Action
                        IconButton(
                            onClick = { onDeleteTransaction(tx.id) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_transaction_${tx.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = SophisticatedTextMutedDarker,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Padding for bottom nav bar to prevent touching or overlapping
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

