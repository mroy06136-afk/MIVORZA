package com.mivorza.social.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun handleGoogleSignInResult(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("MIVORZA_Auth", "Google Sign-In Successful")
                    onSuccess()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Authentication failed."
                    Log.e("MIVORZA_Auth", "Sign-In Error: $errorMessage")
                    onError(errorMessage)
                }
            }
    }
}
