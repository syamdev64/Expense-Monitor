package com.example.expensemonitor.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val storageRef = storage.reference.child("profile_images/$userId")
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
