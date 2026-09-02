package com.example.ui.components

import android.app.DatePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.Categories
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedExpense
import com.example.ui.theme.SophisticatedIncome
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceVariant
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.ui.util.FinanceFormatters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onSave: (type: String, amount: Double, category: String, note: String, date: String) -> Unit
) {
    var txType by remember { mutableStateOf("expense") }
    var txAmount by remember { mutableStateOf("") }
    var txCategory by remember { mutableStateOf(Categories.EXPENSE_CATEGORIES.first().name) }
    var txNote by remember { mutableStateOf("") }
    var txDate by remember { mutableStateOf(FinanceFormatters.todayISO()) }
    var txError by remember { mutableStateOf("") }

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
                txDate = sdf.format(selectedCal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SophisticatedSurface,
        contentColor = SophisticatedTextPrimary,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SophisticatedBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Transaction",
                    color = SophisticatedTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = SophisticatedTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expense / Income Segment Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SophisticatedSurfaceVariant)
                    .border(1.dp, SophisticatedBorder.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (txType == "expense") SophisticatedExpense else Color.Transparent)
                        .clickable {
                            txType = "expense"
                            txCategory = Categories.EXPENSE_CATEGORIES.first().name
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Expense",
                        color = if (txType == "expense") SophisticatedOnPrimary else SophisticatedTextSecondary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (txType == "income") SophisticatedIncome else Color.Transparent)
                        .clickable {
                            txType = "income"
                            txCategory = Categories.INCOME_CATEGORIES.first().name
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Income",
                        color = if (txType == "income") SophisticatedOnPrimary else SophisticatedTextSecondary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input
            OutlinedTextField(
                value = txAmount,
                onValueChange = {
                    txAmount = it
                    txError = ""
                },
                placeholder = {
                    Text("0.00", color = SophisticatedTextMuted, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                },
                leadingIcon = {
                    Text(
                        "AED",
                        color = SophisticatedPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SophisticatedSurfaceVariant,
                    unfocusedContainerColor = SophisticatedSurfaceVariant,
                    focusedBorderColor = SophisticatedPrimary,
                    unfocusedBorderColor = SophisticatedBorder.copy(alpha = 0.5f),
                    focusedTextColor = SophisticatedTextPrimary,
                    unfocusedTextColor = SophisticatedTextPrimary
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_amount_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Categories horizontal scroll
            Text(
                text = "Category",
                color = SophisticatedTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
            )

            val currentCategories = if (txType == "expense") Categories.EXPENSE_CATEGORIES else Categories.INCOME_CATEGORIES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentCategories.forEach { cat ->
                    val isSelected = txCategory.equals(cat.name, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) SophisticatedPrimary else SophisticatedSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SophisticatedPrimary else SophisticatedBorder.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                            .clickable { txCategory = cat.name }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = cat.name,
                            tint = if (isSelected) SophisticatedOnPrimary else cat.color,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cat.name,
                            color = if (isSelected) SophisticatedOnPrimary else SophisticatedTextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note & Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = txNote,
                    onValueChange = { txNote = it },
                    placeholder = { Text("Note (optional)", color = SophisticatedTextMuted, fontSize = 13.5.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SophisticatedSurfaceVariant,
                        unfocusedContainerColor = SophisticatedSurfaceVariant,
                        focusedBorderColor = SophisticatedPrimary,
                        unfocusedBorderColor = SophisticatedBorder.copy(alpha = 0.5f),
                        focusedTextColor = SophisticatedTextPrimary,
                        unfocusedTextColor = SophisticatedTextPrimary
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("transaction_note_input")
                )

                // Date Picker trigger box
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SophisticatedSurfaceVariant)
                        .border(1.dp, SophisticatedBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { datePickerDialog.show() }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Select Date",
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = FinanceFormatters.relativeDate(txDate),
                        color = SophisticatedTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (txError.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = txError,
                    color = SophisticatedExpense,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    val amt = txAmount.toDoubleOrNull()
                    if (amt == null || amt <= 0.0) {
                        txError = "Enter an amount greater than 0"
                        return@Button
                    }
                    onSave(txType, amt, txCategory, txNote, txDate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SophisticatedPrimary,
                    contentColor = SophisticatedOnPrimary
                )
            ) {
                Text(
                    text = "Save Transaction",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

