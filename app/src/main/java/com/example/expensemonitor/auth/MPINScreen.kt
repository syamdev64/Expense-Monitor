package com.example.expensemonitor.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

@Composable
fun MPINScreen(
    isSetup: Boolean = false,
    onSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var mpin by remember { mutableStateOf("") }
    var confirmMpin by remember { mutableStateOf("") }
    
    val savedMpin by viewModel.mpin.collectAsState(initial = null)
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState(initial = false)
    val context = LocalContext.current

    val title = if (isSetup) "Set Your MPIN" else "Enter MPIN"
    val subTitle = if (isSetup) "Create a 4-digit code for quick access" else "Enter your 4-digit secure code"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(subTitle, color = Color.Gray, fontSize = 14.sp)

        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = mpin,
            onValueChange = { if (it.length <= 4) mpin = it },
            label = { Text(if (isSetup) "Enter 4-digit MPIN" else "MPIN") },
            modifier = Modifier.width(200.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF00C853)
            )
        )

        if (isSetup) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmMpin,
                onValueChange = { if (it.length <= 4) confirmMpin = it },
                label = { Text("Confirm MPIN") },
                modifier = Modifier.width(200.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00C853)
                )
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (isSetup) {
                    if (mpin.length == 4 && mpin == confirmMpin) {
                        viewModel.saveMpin(mpin)
                        onSuccess()
                    } else {
                        Toast.makeText(context, "Please enter matching 4-digit MPIN", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (mpin == savedMpin) {
                        onSuccess()
                    } else {
                        Toast.makeText(context, "Invalid MPIN", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.width(200.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
        ) {
            Text(if (isSetup) "Set MPIN" else "Unlock")
        }

        if (isSetup) {
            Spacer(Modifier.height(16.dp))
            TextButton(
                onClick = onSuccess,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Skip for now")
            }
        }

        if (!isSetup && isBiometricEnabled) {
            Spacer(Modifier.height(24.dp))
            IconButton(
                onClick = {
                    showBiometricPrompt(context as FragmentActivity, onSuccess)
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Login",
                    tint = Color(0xFF00C853),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

private fun showBiometricPrompt(activity: FragmentActivity, onAuthSuccess: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onAuthSuccess()
        }
    })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Login")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Use MPIN")
        .build()

    biometricPrompt.authenticate(promptInfo)
}
