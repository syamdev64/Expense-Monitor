package com.example.expensemonitor.di

import com.example.expensemonitor.repository.AuthRepository
import com.example.expensemonitor.repository.ExpenseRepository
import com.example.expensemonitor.repository.FirestoreRepository
import com.example.expensemonitor.repository.SettingsRepository
import com.example.expensemonitor.repository.StorageRepository
import com.example.expensemonitor.roomdb.ExpenseDao
import com.example.expensemonitor.roomdb.SettingsDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirestoreRepository(firestore: FirebaseFirestore) =
        FirestoreRepository(firestore)

    @Provides
    @Singleton
    fun provideExpenseRepository(

        dao: ExpenseDao,

        firestoreRepository: FirestoreRepository,

        authRepository: AuthRepository

    ): ExpenseRepository {

        return ExpenseRepository(
            dao,
            firestoreRepository,
            authRepository
        )

    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return AuthRepository(auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository {
        return StorageRepository(storage)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dao: SettingsDao,
        firestore: FirebaseFirestore
    ): SettingsRepository {

        return SettingsRepository(
            dao,
            firestore
        )
    }

}