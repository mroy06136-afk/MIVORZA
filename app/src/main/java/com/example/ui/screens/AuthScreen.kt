package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.mivorza.social.auth.AuthManager
import kotlinx.coroutines.launch

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

    // Automatic silent auto-sign-in on startup if user already has an authorized account
    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser != null) {
            onAuthSuccess()
            return@LaunchedEffect
        }
        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Log.w("Auth", "default_web_client_id not yet resolved: ${e.message}")
            null
        }
        if (clientId != null) {
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
                // Silently fall through to interactive sign-in
                Log.d("Auth", "Auto sign-in bypassed: ${e.message}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("auth_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Brand Logo Graphic
            Surface(
                modifier = Modifier.size(88.dp),
                shape = RoundedCornerShape(26.dp),
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
                        fontSize = 48.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MIVORZA",
                style = MaterialTheme.typography.headlineLarge,
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
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "The global social platform and creator monetization ecosystem with instant 70/30 earnings.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Value proposition pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureBadge(icon = Icons.Default.MonetizationOn, label = "70/30 Split")
                FeatureBadge(icon = Icons.Default.SlowMotionVideo, label = "Reels Studio")
                FeatureBadge(icon = Icons.Default.LiveTv, label = "Live Broadcast")
            }

            Spacer(modifier = Modifier.height(36.dp))

            errorMessage?.let { error ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
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

            // Interactive Google Sign-In Button
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    val targetActivity = activity ?: context.findActivity() ?: (context as? Activity)
                    if (targetActivity == null) {
                        errorMessage = "Activity window not ready. Please tap again."
                        isLoading = false
                        return@Button
                    }

                    val clientId = try {
                        context.getString(R.string.default_web_client_id)
                    } catch (e: Exception) {
                        errorMessage = "Configuration error: default_web_client_id missing"
                        isLoading = false
                        return@Button
                    }

                    // Strict one-option-per-request builder pattern per API 36 contract
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
                                Log.e("Auth", "Error parsing ID token from credential", e)
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
                                Log.e("Auth", "Unknown credential type: ${credential::class.java.name}")
                                errorMessage = "Unable to process Google credential. Please retry."
                                isLoading = false
                            }
                        } catch (e: GetCredentialCancellationException) {
                            Log.w("Auth", "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
                            isLoading = false
                            errorMessage = "Sign-in was cancelled. Please select a Google account in the chooser."
                        } catch (e: Exception) {
                            Log.e("Auth", "Google Sign-In failed", e)
                            isLoading = false
                            val msg = e.localizedMessage ?: "Sign in failed"
                            errorMessage = if (msg.contains("No credentials", ignoreCase = true)) {
                                "No Google Account found on device. Please sign in to Google in device Settings."
                            } else {
                                msg
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("google_signin_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF4285F4),
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "By continuing, you agree to MIVORZA Community Standards and Creator Monetization Policy.",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
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
