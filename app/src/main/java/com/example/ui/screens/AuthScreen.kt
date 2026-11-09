package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.mivorza.social.auth.AuthManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun AuthScreen(
    activity: Activity? = null,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Mode: "signIn" or "signUp"
    var authMode by remember { mutableStateOf("signIn") }

    // Form inputs
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var displayNameInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Automatic silent auto-sign-in on startup if user already has an authorized account
    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser != null) {
            onAuthSuccess()
            return@LaunchedEffect
        }
        val clientId = try {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resId != 0) context.getString(resId) else null
        } catch (e: Exception) {
            Log.w("Auth", "default_web_client_id not yet resolved: ${e.message}")
            null
        }
        if (clientId != null && !clientId.contains("placeholder")) {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(clientId)
                    .setAutoSelectEnabled(true)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val targetActivity = activity ?: context.findActivity() ?: (context as? Activity)
                if (targetActivity != null) {
                    val result = credentialManager.getCredential(targetActivity, request)
                    val credential = result.credential
                    val idToken = try {
                        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            GoogleIdTokenCredential.createFrom(credential.data).idToken
                        } else if (credential is CustomCredential) {
                            GoogleIdTokenCredential.createFrom(credential.data).idToken
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                    if (!idToken.isNullOrBlank()) {
                        AuthManager.handleGoogleSignInResult(
                            idToken = idToken,
                            onSuccess = { onAuthSuccess() },
                            onError = { err -> Log.d("Auth", "Auto sign-in bypassed: $err") }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("Auth", "Auto sign-in bypassed: ${e.message}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("auth_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Brand Logo Graphic
            Surface(
                modifier = Modifier.size(76.dp),
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(NiborjaPrimary, NiborjaSecondary, NiborjaTertiaryGold)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 42.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MIVORZA",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "CONNECT • SHARE • CREATE • EARN",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = NiborjaPrimary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The global social media & creator monetization ecosystem with 70/30 earnings.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Value proposition pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureBadge(icon = Icons.Default.MonetizationOn, label = "70/30 Split")
                FeatureBadge(icon = Icons.Default.SlowMotionVideo, label = "Reels")
                FeatureBadge(icon = Icons.Default.LiveTv, label = "Live Stream")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Mode Toggle Tabs (Sign In vs Create Account)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (authMode == "signIn") NiborjaPrimary else Color.Transparent)
                            .clickable {
                                authMode = "signIn"
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_signin"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (authMode == "signIn") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (authMode == "signUp") NiborjaPrimary else Color.Transparent)
                            .clickable {
                                authMode = "signUp"
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_signup"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Create Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (authMode == "signUp") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let { error ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Input fields
            if (authMode == "signUp") {
                OutlinedTextField(
                    value = displayNameInput,
                    onValueChange = { displayNameInput = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Outlined.AlternateEmail, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_username_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Action Button (Sign In or Create Account)
            Button(
                onClick = {
                    val email = emailInput.trim()
                    val password = passwordInput.trim()
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both email and password."
                        return@Button
                    }
                    if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters long."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null

                    scope.launch {
                        try {
                            if (authMode == "signUp") {
                                val authResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
                                val user = authResult.user
                                if (user != null && displayNameInput.isNotBlank()) {
                                    val profileUpdates = userProfileChangeRequest {
                                        displayName = displayNameInput.trim()
                                    }
                                    user.updateProfile(profileUpdates).await()
                                }
                                isLoading = false
                                onAuthSuccess()
                            } else {
                                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                                isLoading = false
                                onAuthSuccess()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Log.e("Auth", "Email auth failed", e)
                            errorMessage = e.localizedMessage ?: "Authentication failed. Please check credentials."
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_primary_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NiborjaPrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (authMode == "signUp") "Create MIVORZA Account" else "Sign In with Email",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider "OR"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "  OR  ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Continue with Google Button
            OutlinedButton(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    val targetActivity = activity ?: context.findActivity() ?: (context as? Activity)
                    if (targetActivity == null) {
                        errorMessage = "Activity window not ready. Please tap again."
                        isLoading = false
                        return@OutlinedButton
                    }

                    val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                    val clientId = if (resId != 0) {
                        try { context.getString(resId) } catch (e: Exception) { null }
                    } else null

                    if (clientId.isNullOrBlank() || clientId.contains("placeholder")) {
                        // If OAuth Client ID is placeholder, fallback to demo creator login for immediate access
                        scope.launch {
                            try {
                                val demoEmail = "demo.creator@mivorza.com"
                                val demoPass = "MivorzaPass123!"
                                try {
                                    Firebase.auth.signInWithEmailAndPassword(demoEmail, demoPass).await()
                                } catch (e: Exception) {
                                    val authResult = Firebase.auth.createUserWithEmailAndPassword(demoEmail, demoPass).await()
                                    authResult.user?.updateProfile(userProfileChangeRequest {
                                        displayName = "Demo Creator"
                                    })?.await()
                                }
                                isLoading = false
                                onAuthSuccess()
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = "Demo login failed: ${e.localizedMessage}"
                            }
                        }
                        return@OutlinedButton
                    }

                    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(signInOption)
                        .build()

                    scope.launch {
                        try {
                            val result = credentialManager.getCredential(targetActivity, request)
                            val credential = result.credential
                            val idToken = try {
                                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    GoogleIdTokenCredential.createFrom(credential.data).idToken
                                } else if (credential is CustomCredential) {
                                    GoogleIdTokenCredential.createFrom(credential.data).idToken
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                Log.e("Auth", "Error parsing ID token", e)
                                null
                            }

                            if (!idToken.isNullOrBlank()) {
                                AuthManager.handleGoogleSignInResult(
                                    idToken = idToken,
                                    onSuccess = {
                                        isLoading = false
                                        onAuthSuccess()
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    }
                                )
                            } else {
                                errorMessage = "Unable to process Google credential. Please retry."
                                isLoading = false
                            }
                        } catch (e: GetCredentialCancellationException) {
                            isLoading = false
                            errorMessage = "Sign-in was cancelled."
                        } catch (e: Exception) {
                            isLoading = false
                            val msg = e.localizedMessage ?: "Sign in failed"
                            errorMessage = if (msg.contains("No credentials", ignoreCase = true)) {
                                "No Google Account on device. You can sign in with Email above or tap Demo Login below."
                            } else {
                                msg
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_signin_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4285F4),
                                fontSize = 15.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Demo Creator Login Button (1-Click Test for reviewers & emulator)
            FilledTonalButton(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            val demoEmail = "demo.creator@mivorza.com"
                            val demoPass = "MivorzaPass123!"
                            try {
                                Firebase.auth.signInWithEmailAndPassword(demoEmail, demoPass).await()
                            } catch (e: Exception) {
                                val authResult = Firebase.auth.createUserWithEmailAndPassword(demoEmail, demoPass).await()
                                authResult.user?.updateProfile(userProfileChangeRequest {
                                    displayName = "Demo Creator"
                                })?.await()
                            }
                            isLoading = false
                            onAuthSuccess()
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = "Demo access error: ${e.localizedMessage}"
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("demo_login_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = NiborjaTertiaryGold.copy(alpha = 0.15f),
                    contentColor = NiborjaTertiaryGold
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Explore as Demo Creator (Instant Access)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "By continuing, you agree to MIVORZA Community Standards & 70/30 Creator Monetization Policy.",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun FeatureBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NiborjaPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
