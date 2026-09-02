package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StockEntity
import com.example.ui.components.AddStockDialog
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedDropdownBg
import com.example.ui.theme.SophisticatedExpense
import com.example.ui.theme.SophisticatedHighlight
import com.example.ui.theme.SophisticatedIncome
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedSurfaceVariant
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextMutedDarker
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.util.FinanceFormatters
import com.example.ui.util.fadingEdge
import com.example.ui.viewmodel.StockGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun InvestmentsScreen(
    totalInvested: Double,
    stocks: List<StockEntity>,
    investmentsByStock: List<StockGroup>,
    onAddDeposit: (stock: String, amount: Double, date: String) -> Unit,
    onDeleteInvestment: (Long) -> Unit,
    onAddStock: (ticker: String, name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var depStock by remember { mutableStateOf("") }
    var depAmount by remember { mutableStateOf("") }
    var depDate by remember { mutableStateOf(FinanceFormatters.todayISO()) }
    var depError by remember { mutableStateOf("") }
    var showStockDropdown by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var expandedStock by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                depDate = sdf.format(selectedCal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val filteredStocks = remember(stocks, depStock) {
        val q = depStock.trim().uppercase()
        if (q.isBlank()) stocks
        else stocks.filter { it.ticker.contains(q) || it.name.contains(q) }
    }

    if (showAddStockDialog) {
        AddStockDialog(
            onDismiss = { showAddStockDialog = false },
            onSave = { ticker, name ->
                onAddStock(ticker, name)
                depStock = ticker
                showAddStockDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fadingEdge(topFadeHeight = 16.dp, bottomFadeHeight = 36.dp)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Investments",
                color = SophisticatedTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "IBKR deposits by stock",
                color = SophisticatedTextSecondary,
                fontSize = 13.5.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
            )
        }

        // Hero Card: Total Invested
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 14.dp,
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
                                radius = 650f
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
                            text = "TOTAL INVESTED",
                            color = SophisticatedHighlight.copy(alpha = 0.8f),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FinanceFormatters.formatAED(totalInvested),
                            color = SophisticatedTextPrimary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Log a Deposit Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Log a deposit",
                            color = SophisticatedTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(
                            onClick = { showAddStockDialog = true },
                            modifier = Modifier.testTag("add_stock_open_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add a stock",
                                tint = SophisticatedPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add a stock",
                                color = SophisticatedPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stock Selector & Amount & Date
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Stock Selector with Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = depStock,
                                onValueChange = {
                                    depStock = it.uppercase()
                                    depError = ""
                                    showStockDropdown = true
                                },
                                placeholder = { Text("Stock ticker (e.g. KO, V)", color = SophisticatedTextMuted, fontSize = 13.5.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SophisticatedSurfaceVariant,
                                    unfocusedContainerColor = SophisticatedSurfaceVariant,
                                    focusedBorderColor = SophisticatedPrimary,
                                    unfocusedBorderColor = SophisticatedBorder.copy(alpha = 0.5f),
                                    focusedTextColor = SophisticatedTextPrimary,
                                    unfocusedTextColor = SophisticatedTextPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("deposit_stock_input")
                            )

                            DropdownMenu(
                                expanded = showStockDropdown && filteredStocks.isNotEmpty(),
                                onDismissRequest = { showStockDropdown = false },
                                modifier = Modifier
                                    .background(SophisticatedDropdownBg)
                                    .border(1.dp, SophisticatedBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .heightIn(max = 200.dp)
                            ) {
                                filteredStocks.forEach { stock ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${stock.ticker} - ${stock.name}",
                                                color = SophisticatedTextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        },
                                        onClick = {
                                            depStock = stock.ticker
                                            showStockDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Amount and Date row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = depAmount,
                                onValueChange = {
                                    depAmount = it
                                    depError = ""
                                },
                                placeholder = { Text("Amount (AED)", color = SophisticatedTextMuted, fontSize = 13.5.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SophisticatedSurfaceVariant,
                                    unfocusedContainerColor = SophisticatedSurfaceVariant,
                                    focusedBorderColor = SophisticatedPrimary,
                                    unfocusedBorderColor = SophisticatedBorder.copy(alpha = 0.5f),
                                    focusedTextColor = SophisticatedTextPrimary,
                                    unfocusedTextColor = SophisticatedTextPrimary
                                ),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("deposit_amount_input")
                            )

                            // Date selector box
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SophisticatedSurfaceVariant)
                                    .border(1.dp, SophisticatedBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { datePickerDialog.show() }
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = "Date",
                                    tint = SophisticatedPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = FinanceFormatters.relativeDate(depDate),
                                    color = SophisticatedTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (depError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = depError,
                            color = SophisticatedExpense,
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val stock = depStock.trim().uppercase()
                            if (stock.isBlank()) {
                                depError = "Enter a stock name or ticker"
                                return@Button
                            }
                            val amt = depAmount.toDoubleOrNull()
                            if (amt == null || amt <= 0.0) {
                                depError = "Enter an amount greater than 0"
                                return@Button
                            }
                            onAddDeposit(stock, amt, depDate)
                            depStock = ""
                            depAmount = ""
                            depDate = FinanceFormatters.todayISO()
                            depError = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("add_deposit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedOnPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Deposit",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Deposit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Holdings List Grouped by Stock
        if (investmentsByStock.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No deposits logged yet — add one above.",
                        color = SophisticatedTextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(investmentsByStock, key = { it.stock }) { stockGroup ->
                val isExpanded = expandedStock == stockGroup.stock
                val chevronRotation by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f, label = "chevron")

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stock_holding_${stockGroup.stock}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header Row (Clickable to expand)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedStock = if (isExpanded) null else stockGroup.stock
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SophisticatedPrimary.copy(alpha = 0.16f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TrendingUp,
                                        contentDescription = "Stock",
                                        tint = SophisticatedPrimary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stockGroup.stock,
                                        color = SophisticatedTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${stockGroup.entries.size} deposit${if (stockGroup.entries.size != 1) "s" else ""}",
                                        color = SophisticatedTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = FinanceFormatters.formatAED(stockGroup.total),
                                    color = SophisticatedIncome,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Expand",
                                    tint = SophisticatedTextMuted,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .rotate(chevronRotation)
                                )
                            }
                        }

                        // Expanded Deposits List
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .border(
                                        width = 1.dp,
                                        color = SophisticatedBorder.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(
                                        color = SophisticatedSurfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                stockGroup.entries.forEach { entry ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = FinanceFormatters.relativeDate(entry.date),
                                            color = SophisticatedTextSecondary,
                                            fontSize = 13.sp
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = FinanceFormatters.formatAED(entry.amount),
                                                color = SophisticatedIncome,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = { onDeleteInvestment(entry.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Delete entry",
                                                    tint = SophisticatedTextMutedDarker,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom nav spacer
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

