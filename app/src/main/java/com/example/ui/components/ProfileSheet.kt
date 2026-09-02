package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.SophisticatedBg
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedExpense
import com.example.ui.theme.SophisticatedHighlight
import com.example.ui.theme.SophisticatedIncome
import com.example.ui.theme.SophisticatedOnPrimary
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedPrimaryDark
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceContainer
import com.example.ui.theme.SophisticatedSurfaceVariant
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheet(
    sheetState: SheetState,
    currentUser: UserEntity?,
    isLoggedIn: Boolean,
    isSyncing: Boolean = false,
    isSupabaseConfigured: Boolean = true,
    onSyncClick: () -> Unit = {},
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit,
    onLoginClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onClearDataClick: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SophisticatedSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SophisticatedBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account Profile",
                    color = SophisticatedTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SophisticatedTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // User Avatar & Name Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurfaceVariant),
                border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar Circle with Glow
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = SophisticatedPrimary.copy(alpha = 0.4f),
                                spotColor = SophisticatedPrimary.copy(alpha = 0.4f)
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SophisticatedPrimary, SophisticatedPrimaryDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.name?.take(1)?.uppercase() ?: "G",
                            color = SophisticatedOnPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isLoggedIn) (currentUser?.name ?: "User") else "Guest Mode",
                            color = SophisticatedTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isLoggedIn) (currentUser?.email ?: "No email registered") else "Not signed in",
                            color = SophisticatedTextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Status Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isLoggedIn) SophisticatedIncome.copy(alpha = 0.15f)
                                    else SophisticatedTextMuted.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (isLoggedIn) Icons.Default.CheckCircle else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (isLoggedIn) SophisticatedIncome else SophisticatedTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isLoggedIn) "Verified Account" else "Offline Session",
                                color = if (isLoggedIn) SophisticatedIncome else SophisticatedTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Supabase Cloud Database Status Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurfaceContainer),
                border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_supabase_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SophisticatedIncome.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = SophisticatedIncome,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Supabase Cloud Database",
                            color = SophisticatedTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isSyncing) "Syncing data..." else if (isSupabaseConfigured) "Connected (wsrzdnxz...)" else "Not configured",
                            color = if (isSyncing) SophisticatedPrimary else SophisticatedIncome,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onSyncClick,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("profile_sync_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = SophisticatedPrimary
                            )
                        } else {
                            Text("Sync", fontSize = 12.sp, color = SophisticatedPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isLoggedIn) {
                    // Reset Password Button
                    Surface(
                        onClick = onResetPasswordClick,
                        shape = RoundedCornerShape(14.dp),
                        color = SophisticatedSurfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_reset_password_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                tint = SophisticatedPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Change / Reset Password",
                                    color = SophisticatedTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Update your login security credentials",
                                    color = SophisticatedTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Log Out Action Button
                    Surface(
                        onClick = onLogoutClick,
                        shape = RoundedCornerShape(14.dp),
                        color = SophisticatedExpense.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, SophisticatedExpense.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_logout_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = SophisticatedExpense,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Log Out",
                                    color = SophisticatedExpense,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "End session on this device",
                                    color = SophisticatedTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Clear Local Data Button
                    Surface(
                        onClick = onClearDataClick,
                        shape = RoundedCornerShape(14.dp),
                        color = SophisticatedSurfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_clear_data_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = SophisticatedTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Clear Local Data",
                                    color = SophisticatedTextPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Empty local ledger entries for a fresh test",
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                } else {
                    // Log In / Register Action Button
                    Button(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedOnPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("profile_login_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log In / Register Account",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
