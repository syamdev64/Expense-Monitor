package com.example.expensemonitor.di

import com.example.expensemonitor.repository.ExpenseRepository
import com.example.expensemonitor.repository.FirestoreRepository
import com.example.expensemonitor.repository.SettingsRepository
import com.example.expensemonitor.roomdb.ExpenseDao
import com.example.expensemonitor.roomdb.SettingsDao
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideFirestoreRepository() =
        FirestoreRepository()

    @Provides
    @Singleton
    fun provideExpenseRepository(

        dao: ExpenseDao,

        firestoreRepository: FirestoreRepository

    ): ExpenseRepository {

        return ExpenseRepository(
            dao,
            firestoreRepository
        )

    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
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