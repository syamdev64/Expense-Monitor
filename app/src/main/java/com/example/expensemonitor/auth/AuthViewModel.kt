package com.example.expensemonitor.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemonitor.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.expensemonitor.repository.FirestoreRepository
import com.example.expensemonitor.repository.SettingsRepository
import com.example.expensemonitor.repository.StorageRepository
import android.net.Uri
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository,
    private val settingsRepository: SettingsRepository,
    private val storageRepository: StorageRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser

    val isBiometricEnabled = authPreferences.isBiometricEnabled
    val isRememberMeEnabled = authPreferences.isRememberMeEnabled
    val mpin = authPreferences.mpin
    val isFirstTime = authPreferences.isFirstTime

    private val _userPhoneNumber = MutableStateFlow<String?>(null)
    val userPhoneNumber = _userPhoneNumber.asStateFlow()

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus = _updateStatus.asStateFlow()

    sealed class UpdateStatus {
        object Idle : UpdateStatus()
        object Loading : UpdateStatus()
        data class Success(val message: String) : UpdateStatus()
        data class Error(val message: String) : UpdateStatus()
    }

    init {
        viewModelScope.launch {
            // Handle Remember Me logic on start
            val rememberMe = authPreferences.isRememberMeEnabled.first()
            if (!rememberMe && authRepository.isUserLoggedIn) {
                authRepository.signOut()
            }

            currentUser.collect { user ->
                user?.uid?.let { uid ->
                    _userPhoneNumber.value = firestoreRepository.getUserPhoneNumber(uid)
                }
            }
        }
    }

    fun updateProfile(
        displayName: String,
        photoUrl: String?,
        phoneNumber: String,
        email: String,
        password: String? = null
    ) {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val uid = currentUser.value?.uid ?: return@launch

            // --- Validation ---
            if (displayName.isBlank()) {
                _updateStatus.value = UpdateStatus.Error("Display Name cannot be empty")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _updateStatus.value = UpdateStatus.Error("Invalid Email Address")
                return@launch
            }
            if (phoneNumber.length != 10) {
                _updateStatus.value = UpdateStatus.Error("Phone Number must be 10 digits")
                return@launch
            }
            if (!password.isNullOrEmpty() && password != "••••••••••••" && password.length < 6) {
                _updateStatus.value = UpdateStatus.Error("Password must be at least 6 characters")
                return@launch
            }

            var finalPhotoUrl = photoUrl
            
            // If photoUrl is a local content URI, upload it first
            if (photoUrl?.startsWith("content://") == true) {
                val uploadResult = storageRepository.uploadProfileImage(uid, Uri.parse(photoUrl))
                if (uploadResult.isSuccess) {
                    finalPhotoUrl = uploadResult.getOrNull()
                } else {
                    _updateStatus.value = UpdateStatus.Error("Failed to upload image")
                    return@launch
                }
            }
            
            val profileResult = authRepository.updateProfile(displayName, finalPhotoUrl)
            
            // Handle Email Update if changed
            if (email != currentUser.value?.email) {
                val emailResult = authRepository.updateEmail(email)
                if (emailResult.isFailure) {
                    _updateStatus.value = UpdateStatus.Error(emailResult.exceptionOrNull()?.message ?: "Failed to update email")
                    return@launch
                }
            }

            // Handle Password Update if provided
            if (!password.isNullOrEmpty() && password != "••••••••••••") {
                val passwordResult = authRepository.updatePassword(password)
                if (passwordResult.isFailure) {
                    _updateStatus.value = UpdateStatus.Error(passwordResult.exceptionOrNull()?.message ?: "Failed to update password")
                    return@launch
                }
            }

            // Sync all changes to Firestore
            firestoreRepository.saveUserProfile(
                uid = uid,
                name = displayName,
                email = email,
                phoneNumber = phoneNumber,
                photoUrl = finalPhotoUrl
            )
            _userPhoneNumber.value = phoneNumber

            if (profileResult.isSuccess) {
                _updateStatus.value = UpdateStatus.Success("Profile updated successfully")
            } else {
                _updateStatus.value = UpdateStatus.Error(profileResult.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val uid = currentUser.value?.uid ?: return@launch
            
            val uploadResult = storageRepository.uploadProfileImage(uid, imageUri)
            if (uploadResult.isSuccess) {
                val downloadUrl = uploadResult.getOrNull()
                _updateStatus.value = UpdateStatus.Success("Image uploaded successfully")
                // We don't update the profile here, we just provide the URL back to the UI
                // so the user can see the change before hitting 'Save'
                _photoUploadUrl.value = downloadUrl
            } else {
                _updateStatus.value = UpdateStatus.Error(uploadResult.exceptionOrNull()?.message ?: "Upload failed")
            }
        }
    }

    private val _photoUploadUrl = MutableStateFlow<String?>(null)
    val photoUploadUrl = _photoUploadUrl.asStateFlow()

    fun resetPhotoUploadUrl() {
        _photoUploadUrl.value = null
    }

    fun sendPasswordReset() {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val result = authRepository.sendPasswordResetEmail()
            if (result.isSuccess) {
                _updateStatus.value = UpdateStatus.Success("Password reset email sent")
            } else {
                _updateStatus.value = UpdateStatus.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = UpdateStatus.Idle
    }

    fun toggleBiometric(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            if (enabled) {
                val biometricManager = BiometricManager.from(context)
                val canAuthenticate = biometricManager.canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )

                when (canAuthenticate) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        authPreferences.setBiometricEnabled(true)
                        settingsRepository.updateBiometricPreference(true)
                    }
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        _updateStatus.value = UpdateStatus.Error("No biometric hardware detected")
                    }
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        _updateStatus.value = UpdateStatus.Error("Biometric hardware is currently unavailable")
                    }
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        _updateStatus.value = UpdateStatus.Error("Please set up biometrics in your device settings")
                    }
                    else -> {
                        _updateStatus.value = UpdateStatus.Error("Biometric authentication is not supported")
                    }
                }
            } else {
                authPreferences.setBiometricEnabled(false)
                settingsRepository.updateBiometricPreference(false)
            }
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val result = authRepository.signIn(email, password)
            if (result.isSuccess) {
                authPreferences.setRememberMe(rememberMe)
                authPreferences.setFirstTime(false)
                val user = result.getOrNull()
                user?.uid?.let { uid ->
                    _userPhoneNumber.value = firestoreRepository.getUserPhoneNumber(uid)
                }
                _updateStatus.value = UpdateStatus.Success("Login successful")
            } else {
                _updateStatus.value = UpdateStatus.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, phoneNumber: String = "") {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val result = authRepository.signUp(email, password, name)
            if (result.isSuccess) {
                val user = result.getOrNull()
                user?.let {
                    firestoreRepository.saveUserProfile(
                        uid = it.uid,
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber,
                        photoUrl = null
                    )
                    _userPhoneNumber.value = phoneNumber
                }
                authPreferences.setRememberMe(true)
                authPreferences.setFirstTime(false)
                _updateStatus.value = UpdateStatus.Success("Registration successful")
            } else {
                _updateStatus.value = UpdateStatus.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun saveMpin(newMpin: String) {
        viewModelScope.launch {
            authPreferences.saveMpin(newMpin)
            _updateStatus.value = UpdateStatus.Success("MPIN saved successfully")
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            authPreferences.setRememberMe(false)
            authPreferences.saveMpin("")
        }
    }

    val isUserLoggedIn: Boolean
        get() = authRepository.isUserLoggedIn
}
