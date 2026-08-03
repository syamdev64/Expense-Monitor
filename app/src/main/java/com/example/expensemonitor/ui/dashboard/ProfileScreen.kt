package com.example.expensemonitor.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.net.Uri
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.expensemonitor.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensemonitor.auth.AuthViewModel
import com.example.expensemonitor.expenseviewmodel.ExpenseViewModel

import androidx.compose.material.icons.filled.Fingerprint

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    onNavigateToMpinSetup: () -> Unit = {}
) {
    val user by authViewModel.currentUser.collectAsState()
    val savedMpin by authViewModel.mpin.collectAsState(initial = "")
    val phoneNumber by authViewModel.userPhoneNumber.collectAsState()
    val updateStatus by authViewModel.updateStatus.collectAsState()
    val isBiometricEnabled by authViewModel.isBiometricEnabled.collectAsState(initial = false)
    
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var editedPhotoUrl by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(user, phoneNumber) {
        editedName = user?.displayName ?: "Guest User"
        editedPhone = phoneNumber ?: ""
        editedPhotoUrl = user?.photoUrl?.toString() ?: ""
        editedEmail = user?.email ?: ""
        editedPassword = ""
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            editedPhotoUrl = it.toString()
        }
    }

    LaunchedEffect(updateStatus) {
        if (updateStatus is AuthViewModel.UpdateStatus.Success) {
            Toast.makeText(context, (updateStatus as AuthViewModel.UpdateStatus.Success).message, Toast.LENGTH_SHORT).show()
            isEditMode = false
            authViewModel.resetUpdateStatus()
        } else if (updateStatus is AuthViewModel.UpdateStatus.Error) {
            Toast.makeText(context, (updateStatus as AuthViewModel.UpdateStatus.Error).message, Toast.LENGTH_SHORT).show()
            authViewModel.resetUpdateStatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Profile Picture and Header Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            val photoSource = if (isEditMode) editedPhotoUrl else user?.photoUrl
            
            if (photoSource != null && photoSource.toString().isNotEmpty()) {
                AsyncImage(
                    model = photoSource,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
            }
            
            if (isEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Picture",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditMode) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00C853),
                    unfocusedBorderColor = Color.Gray
                ),
                singleLine = true
            )
        } else {
            Text(
                text = user?.displayName ?: "Guest User",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { isEditMode = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color(0xFF00C853)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // User Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ProfileDetailRow(
                    icon = Icons.Default.Email,
                    label = "Email Address",
                    value = if (isEditMode) editedEmail else (user?.email ?: "guest@example.com"),
                    isEditable = isEditMode,
                    onValueChange = { editedEmail = it }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.Gray.copy(alpha = 0.2f)
                )
                
                ProfileDetailRow(
                    icon = Icons.Default.Phone,
                    label = "Phone Number",
                    value = if (isEditMode) editedPhone else (phoneNumber ?: "Not provided"),
                    isEditable = isEditMode,
                    onValueChange = { editedPhone = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.Gray.copy(alpha = 0.2f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        ProfileDetailRow(
                        icon = Icons.Default.Lock,
                        label = "Password",
                        value = if (isEditMode) editedPassword else "••••••••••••",
                        isEditable = isEditMode,
                        onValueChange = { editedPassword = it },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = if (isEditMode) {
                            {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!isEditMode && user != null) {
                        Button(
                            onClick = { authViewModel.sendPasswordReset() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Reset", color = Color(0xFF00C853), fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Security Section Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF00C853)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Biometric Lock",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Fingerprint or Face ID",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { authViewModel.toggleBiometric(it, context) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00C853),
                            checkedTrackColor = Color(0xFF00C853).copy(alpha = 0.5f)
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = Color.Gray.copy(alpha = 0.2f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF00C853)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "MPIN Lock",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (savedMpin.isNullOrEmpty()) "Not Set" else "MPIN is active",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    TextButton(onClick = onNavigateToMpinSetup) {
                        Text(
                            text = if (savedMpin.isNullOrEmpty()) "Set" else "Change",
                            color = Color(0xFF00C853),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (isEditMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { isEditMode = false },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancel", color = Color.White)
                }
                
                Button(
                    onClick = {
                        // 1. Basic UI Validation first
                        val validationError = when {
                            editedName.isBlank() -> "Please enter a display name"
                            editedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(editedEmail).matches() -> "Please enter a valid email"
                            editedPhone.length != 10 -> "Phone number must be 10 digits"
                            editedPassword != "••••••••••••" && editedPassword.length < 6 -> "Password must be at least 6 characters"
                            else -> null
                        }

                        if (validationError != null) {
                            Toast.makeText(context, validationError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // 2. Auth Guard for saving
                        if (user != null) {
                            authViewModel.updateProfile(
                                displayName = editedName,
                                photoUrl = editedPhotoUrl,
                                phoneNumber = editedPhone,
                                email = editedEmail,
                                password = editedPassword
                            )
                        } else {
                            // Mock success for guests so they can test validation
                            Toast.makeText(context, "Profile validated! (Login to save to cloud)", Toast.LENGTH_LONG).show()
                            isEditMode = false
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    if (updateStatus is AuthViewModel.UpdateStatus.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Save", color = Color.White)
                    }
                }
            }
        } else if (user != null) {
            Button(
                onClick = {
                    authViewModel.signOut()
                    expenseViewModel.clearDataOnLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444))
            ) {
                Text(
                    text = "Sign Out",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun ProfileDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    isEditable: Boolean = false,
    onValueChange: (String) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF00C853)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 12.sp
            )
            
            if (isEditable) {
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF00C853)),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}

