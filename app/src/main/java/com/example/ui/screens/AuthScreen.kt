package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AuthMode
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

@Composable
fun AuthScreen(
    initialMode: AuthMode = AuthMode.LOGIN,
    savedEmail: String? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (name: String, email: String, pass: String, confirmPass: String) -> Unit,
    onResetPassword: (email: String, newPass: String, confirmPass: String) -> Unit
) {
    var mode by remember { mutableStateOf(if (savedEmail.isNullOrBlank()) AuthMode.REGISTER else initialMode) }
    val focusManager = LocalFocusManager.current

    // Form inputs with saved email pre-filled from cache
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(savedEmail ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(savedEmail) {
        if (!savedEmail.isNullOrBlank() && email.isBlank()) {
            email = savedEmail
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SophisticatedBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Brand Crest
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(SophisticatedPrimary, SophisticatedPrimaryDark)
                        )
                    )
                    .shadow(12.dp, CircleShape, ambientColor = SophisticatedPrimary, spotColor = SophisticatedPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SophisticatedOnPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "KANZIA",
                color = SophisticatedHighlight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                fontSize = 18.sp
            )

            Text(
                text = "Personal Financial Ledger",
                color = SophisticatedTextSecondary,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.6f)),
                        RoundedCornerShape(28.dp)
                    )
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = SophisticatedPrimary.copy(alpha = 0.25f),
                        spotColor = SophisticatedPrimary.copy(alpha = 0.25f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    SophisticatedSurfaceContainer.copy(alpha = 0.4f),
                                    SophisticatedSurface,
                                    SophisticatedSurfaceVariant
                                )
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Back button when in FORGOT_PASSWORD mode
                    if (mode == AuthMode.FORGOT_PASSWORD) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { mode = AuthMode.LOGIN },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back to Login",
                                    tint = SophisticatedTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Back to Login",
                                color = SophisticatedTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Title & Description
                    Text(
                        text = when (mode) {
                            AuthMode.LOGIN -> if (!savedEmail.isNullOrBlank()) "Welcome Back" else "Sign In"
                            AuthMode.REGISTER -> "Create Account"
                            AuthMode.FORGOT_PASSWORD -> "Reset Password"
                        },
                        color = SophisticatedTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (mode) {
                            AuthMode.LOGIN -> "Log in to securely access your transactions, stats, and portfolio."
                            AuthMode.REGISTER -> "Register your account to manage your wealth and investments."
                            AuthMode.FORGOT_PASSWORD -> "Enter your email and create a new password to restore access."
                        },
                        color = SophisticatedTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mode Switcher Tabs (Login vs Register)
                    if (mode != AuthMode.FORGOT_PASSWORD) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SophisticatedBg)
                                .border(1.dp, SophisticatedBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (mode == AuthMode.LOGIN) SophisticatedPrimary else Color.Transparent)
                                    .clickable { mode = AuthMode.LOGIN }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_login"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Log In",
                                    color = if (mode == AuthMode.LOGIN) SophisticatedOnPrimary else SophisticatedTextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (mode == AuthMode.LOGIN) FontWeight.Bold else FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (mode == AuthMode.REGISTER) SophisticatedPrimary else Color.Transparent)
                                    .clickable { mode = AuthMode.REGISTER }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_register"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Register",
                                    color = if (mode == AuthMode.REGISTER) SophisticatedOnPrimary else SophisticatedTextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (mode == AuthMode.REGISTER) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Input Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Full Name (only in REGISTER mode)
                        if (mode == AuthMode.REGISTER) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name", fontSize = 13.sp) },
                                placeholder = { Text("e.g. John Doe", color = SophisticatedTextMuted, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = SophisticatedHighlight)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SophisticatedHighlight,
                                    unfocusedBorderColor = SophisticatedBorder,
                                    focusedTextColor = SophisticatedTextPrimary,
                                    unfocusedTextColor = SophisticatedTextPrimary,
                                    focusedLabelColor = SophisticatedHighlight,
                                    unfocusedLabelColor = SophisticatedTextSecondary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )
                        }

                        // Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", fontSize = 13.sp) },
                            placeholder = { Text("user@example.com", color = SophisticatedTextMuted, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = SophisticatedHighlight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SophisticatedHighlight,
                                unfocusedBorderColor = SophisticatedBorder,
                                focusedTextColor = SophisticatedTextPrimary,
                                unfocusedTextColor = SophisticatedTextPrimary,
                                focusedLabelColor = SophisticatedHighlight,
                                unfocusedLabelColor = SophisticatedTextSecondary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(
                                    if (mode == AuthMode.FORGOT_PASSWORD) "New Password" else "Password",
                                    fontSize = 13.sp
                                )
                            },
                            placeholder = { Text("At least 6 characters", color = SophisticatedTextMuted, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    if (mode == AuthMode.FORGOT_PASSWORD) Icons.Default.LockReset else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = SophisticatedHighlight
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = SophisticatedTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SophisticatedHighlight,
                                unfocusedBorderColor = SophisticatedBorder,
                                focusedTextColor = SophisticatedTextPrimary,
                                unfocusedTextColor = SophisticatedTextPrimary,
                                focusedLabelColor = SophisticatedHighlight,
                                unfocusedLabelColor = SophisticatedTextSecondary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (mode == AuthMode.LOGIN) ImeAction.Done else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (mode == AuthMode.LOGIN) onLogin(email, password)
                                },
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )

                        // Confirm Password (in REGISTER & FORGOT_PASSWORD)
                        if (mode != AuthMode.LOGIN) {
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password", fontSize = 13.sp) },
                                placeholder = { Text("Re-type password", color = SophisticatedTextMuted, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = SophisticatedHighlight)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                            tint = SophisticatedTextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_confirm_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SophisticatedHighlight,
                                    unfocusedBorderColor = SophisticatedBorder,
                                    focusedTextColor = SophisticatedTextPrimary,
                                    unfocusedTextColor = SophisticatedTextPrimary,
                                    focusedLabelColor = SophisticatedHighlight,
                                    unfocusedLabelColor = SophisticatedTextSecondary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (mode == AuthMode.REGISTER) {
                                            onRegister(name, email, password, confirmPassword)
                                        } else {
                                            onResetPassword(email, password, confirmPassword)
                                        }
                                    }
                                )
                            )
                        }
                    }

                    // Forgot Password link in LOGIN mode
                    if (mode == AuthMode.LOGIN) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { mode = AuthMode.FORGOT_PASSWORD },
                                modifier = Modifier.testTag("btn_forgot_password")
                            ) {
                                Text(
                                    text = "Forgot password?",
                                    color = SophisticatedHighlight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Error & Success Feedback
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        errorMessage?.let { msg ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = SophisticatedExpense.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, SophisticatedExpense.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = SophisticatedExpense,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = msg,
                                        color = SophisticatedExpense,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = successMessage != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        successMessage?.let { msg ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = SophisticatedIncome.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, SophisticatedIncome.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SophisticatedIncome,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = msg,
                                        color = SophisticatedIncome,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary Submit Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            when (mode) {
                                AuthMode.LOGIN -> onLogin(email, password)
                                AuthMode.REGISTER -> onRegister(name, email, password, confirmPassword)
                                AuthMode.FORGOT_PASSWORD -> onResetPassword(email, password, confirmPassword)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedOnPrimary
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SophisticatedOnPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when (mode) {
                                        AuthMode.LOGIN -> Icons.Default.VpnKey
                                        AuthMode.REGISTER -> Icons.Default.Person
                                        AuthMode.FORGOT_PASSWORD -> Icons.Default.LockReset
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = when (mode) {
                                        AuthMode.LOGIN -> "Log In to Ledger"
                                        AuthMode.REGISTER -> "Create Account"
                                        AuthMode.FORGOT_PASSWORD -> "Update Password"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security badge & App Caching indicator
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SophisticatedSurface.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SophisticatedHighlight,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Encrypted Local Cache • Auto-Closes After 3m Inactivity",
                        color = SophisticatedTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
