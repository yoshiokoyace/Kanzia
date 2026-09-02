package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddTransactionSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InvestmentsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.SophisticatedBg
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

data class NavItem(
    val id: String,
    val icon: ImageVector,
    val contentDescription: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val totals by viewModel.totals.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.filter.collectAsStateWithLifecycle()
    val seeAll by viewModel.seeAll.collectAsStateWithLifecycle()
    val expenseByCategory by viewModel.expenseByCategory.collectAsStateWithLifecycle()
    val monthlyData by viewModel.monthlyData.collectAsStateWithLifecycle()
    val stocks by viewModel.stocks.collectAsStateWithLifecycle()
    val investmentsByStock by viewModel.investmentsByStock.collectAsStateWithLifecycle()

    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val navItems = remember {
        listOf(
            NavItem("home", Icons.Filled.Home, "Home"),
            NavItem("stats", Icons.Filled.PieChart, "Stats"),
            NavItem("investments", Icons.Filled.TrendingUp, "Investments")
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedBg)
    ) {
        // Main Screen Content with status bar padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(250),
                label = "screen_crossfade"
            ) { tab ->
                when (tab) {
                    "home" -> HomeScreen(
                        totals = totals,
                        transactions = transactions,
                        selectedFilter = selectedFilter,
                        seeAll = seeAll,
                        onFilterSelect = { viewModel.setFilter(it) },
                        onSeeAllClick = { viewModel.toggleSeeAll() },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) }
                    )
                    "stats" -> StatsScreen(
                        totals = totals,
                        expenseByCategory = expenseByCategory,
                        monthlyData = monthlyData
                    )
                    "investments" -> InvestmentsScreen(
                        totalInvested = totals.totalInvested,
                        stocks = stocks,
                        investmentsByStock = investmentsByStock,
                        onAddDeposit = { stock, amount, date ->
                            viewModel.addInvestment(stock, amount, date)
                        },
                        onDeleteInvestment = { viewModel.deleteInvestment(it) },
                        onAddStock = { ticker, name ->
                            viewModel.addStock(ticker, name)
                        }
                    )
                }
            }
        }

        // Top Subtle Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SophisticatedBg,
                            SophisticatedBg.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bottom Gradient Scrim & Floating Navigation Bar with Add FAB
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            SophisticatedBg.copy(alpha = 0.6f),
                            SophisticatedBg.copy(alpha = 0.92f),
                            SophisticatedBg
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(top = 22.dp, bottom = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation Pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SophisticatedSurface)
                        .border(1.dp, SophisticatedBorder.copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentTab == item.id
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) SophisticatedPrimary else Color.Transparent)
                                .clickable { viewModel.setTab(item.id) }
                                .testTag("nav_item_${item.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.contentDescription,
                                tint = if (isSelected) SophisticatedOnPrimary else SophisticatedTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Floating Add Action Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            ambientColor = SophisticatedPrimary.copy(alpha = 0.5f),
                            spotColor = SophisticatedPrimary.copy(alpha = 0.5f)
                        )
                        .clip(CircleShape)
                        .background(SophisticatedPrimary)
                        .clickable { showAddSheet = true }
                        .testTag("floating_add_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Transaction",
                        tint = SophisticatedOnPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        AddTransactionSheet(
            sheetState = sheetState,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showAddSheet = false
                }
            },
            onSave = { type, amount, category, note, date ->
                viewModel.addTransaction(type, amount, category, note, date)
                coroutineScope.launch {
                    sheetState.hide()
                    showAddSheet = false
                }
            }
        )
    }
}

