package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@Composable
fun AuthModal(
    initialMode: AuthMode = AuthMode.LOGIN,
    isDismissable: Boolean = true,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onDismiss: () -> Unit,
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (name: String, email: String, pass: String, confirmPass: String) -> Unit,
    onResetPassword: (email: String, newPass: String, confirmPass: String) -> Unit
) {
    var mode by remember { mutableStateOf(initialMode) }
    val focusManager = LocalFocusManager.current

    // Form inputs
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            if (isDismissable) onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = isDismissable,
            dismissOnClickOutside = isDismissable
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        BorderStroke(1.dp, SophisticatedBorder.copy(alpha = 0.6f)),
                        RoundedCornerShape(28.dp)
                    )
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = SophisticatedPrimary.copy(alpha = 0.3f),
                        spotColor = SophisticatedPrimary.copy(alpha = 0.3f)
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
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Back / Dismiss
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (mode == AuthMode.FORGOT_PASSWORD) {
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
                        } else {
                            // Brand badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(SophisticatedPrimary, SophisticatedPrimaryDark)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = null,
                                        tint = SophisticatedOnPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "KANZIA",
                                    color = SophisticatedHighlight,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        if (isDismissable) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("auth_dismiss_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = SophisticatedTextSecondary
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(36.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title and Subtitle based on mode
                    Text(
                        text = when (mode) {
                            AuthMode.LOGIN -> "Welcome Back"
                            AuthMode.REGISTER -> "Create Account"
                            AuthMode.FORGOT_PASSWORD -> "Reset Password"
                        },
                        color = SophisticatedTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (mode) {
                            AuthMode.LOGIN -> "Enter your email and password to access your financial portfolio."
                            AuthMode.REGISTER -> "Sign up to track your expenses, investments, and net worth securely."
                            AuthMode.FORGOT_PASSWORD -> "Enter your registered email and choose a new password."
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
                                    fontWeight = if (mode == AuthMode.LOGIN) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
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
                                    text = "Sign Up",
                                    color = if (mode == AuthMode.REGISTER) SophisticatedOnPrimary else SophisticatedTextSecondary,
                                    fontWeight = if (mode == AuthMode.REGISTER) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Status Messages (Error or Success)
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        if (errorMessage != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SophisticatedExpense.copy(alpha = 0.15f))
                                    .border(1.dp, SophisticatedExpense.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                    text = errorMessage,
                                    color = SophisticatedExpense,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = successMessage != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        if (successMessage != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SophisticatedIncome.copy(alpha = 0.15f))
                                    .border(1.dp, SophisticatedIncome.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                    text = successMessage,
                                    color = SophisticatedIncome,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Input Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Name Field (Only on Register)
                        if (mode == AuthMode.REGISTER) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                placeholder = { Text("e.g. Alex Morgan") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = SophisticatedPrimary
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = getAuthTextFieldColors(),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_input")
                            )
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("name@example.com") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = SophisticatedPrimary
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = getAuthTextFieldColors(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input")
                        )

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(
                                    if (mode == AuthMode.FORGOT_PASSWORD) "New Password" else "Password"
                                )
                            },
                            placeholder = { Text("At least 6 characters") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (mode == AuthMode.FORGOT_PASSWORD) Icons.Default.LockReset else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = SophisticatedPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = SophisticatedTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (mode == AuthMode.LOGIN) ImeAction.Done else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = {
                                    focusManager.clearFocus()
                                    if (mode == AuthMode.LOGIN) {
                                        onLogin(email, password)
                                    }
                                }
                            ),
                            colors = getAuthTextFieldColors(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        // Confirm Password (Only on Register & Forgot Password)
                        if (mode != AuthMode.LOGIN) {
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                placeholder = { Text("Re-enter password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = SophisticatedPrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                            tint = SophisticatedTextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (mode == AuthMode.REGISTER) {
                                            onRegister(name, email, password, confirmPassword)
                                        } else if (mode == AuthMode.FORGOT_PASSWORD) {
                                            onResetPassword(email, password, confirmPassword)
                                        }
                                    }
                                ),
                                colors = getAuthTextFieldColors(),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_confirm_password_input")
                            )
                        }
                    }

                    // "Forgot Password?" prompt (Only in Login mode)
                    if (mode == AuthMode.LOGIN) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = SophisticatedHighlight,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable {
                                        mode = AuthMode.FORGOT_PASSWORD
                                    }
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                                    .testTag("forgot_password_button")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Action Button
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
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedOnPrimary
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = SophisticatedOnPrimary,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = when (mode) {
                                    AuthMode.LOGIN -> "Log In"
                                    AuthMode.REGISTER -> "Create Account"
                                    AuthMode.FORGOT_PASSWORD -> "Reset Password"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Toggle Link
                    Spacer(modifier = Modifier.height(10.dp))
                    when (mode) {
                        AuthMode.LOGIN -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Don't have an account? ",
                                    color = SophisticatedTextSecondary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Sign Up",
                                    color = SophisticatedPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clickable { mode = AuthMode.REGISTER }
                                        .padding(4.dp)
                                        .testTag("switch_to_register_link")
                                )
                            }
                        }
                        AuthMode.REGISTER -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Already have an account? ",
                                    color = SophisticatedTextSecondary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Log In",
                                    color = SophisticatedPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clickable { mode = AuthMode.LOGIN }
                                        .padding(4.dp)
                                        .testTag("switch_to_login_link")
                                )
                            }
                        }
                        AuthMode.FORGOT_PASSWORD -> {
                            TextButton(
                                onClick = { mode = AuthMode.LOGIN },
                                modifier = Modifier.testTag("back_to_login_button")
                            ) {
                                Text(
                                    text = "Remembered your password? Log In",
                                    color = SophisticatedPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getAuthTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SophisticatedPrimary,
    unfocusedBorderColor = SophisticatedBorder,
    focusedLabelColor = SophisticatedPrimary,
    unfocusedLabelColor = SophisticatedTextSecondary,
    focusedTextColor = SophisticatedTextPrimary,
    unfocusedTextColor = SophisticatedTextPrimary,
    cursorColor = SophisticatedPrimary,
    focusedContainerColor = SophisticatedSurfaceVariant.copy(alpha = 0.7f),
    unfocusedContainerColor = SophisticatedSurfaceVariant.copy(alpha = 0.5f)
)
