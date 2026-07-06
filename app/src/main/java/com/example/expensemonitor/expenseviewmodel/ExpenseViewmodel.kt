package com.example.expensemonitor.expenseviewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensemonitor.cloud.ExpenseFirestore
import com.example.expensemonitor.repository.ExpenseRepository
import com.example.expensemonitor.repository.FirestoreRepository
import com.example.expensemonitor.repository.SettingsRepository
import com.example.expensemonitor.roomdb.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ExpenseViewModel @Inject constructor(

    private val repository: ExpenseRepository,

    private val settingsRepository: SettingsRepository,

    private val firestoreRepository: FirestoreRepository

) : ViewModel() {

    val expenses: LiveData<List<ExpenseEntity>> =
        repository.expenses.asLiveData()

    val settings =
        settingsRepository.settings.asLiveData()

    init {

        repository.startFirestoreSync()

    }

    fun insert(
        amount: Double,
        category: String,
        description: String,
        date: Long
    ) {

        viewModelScope.launch {

            repository.insert(
                ExpenseEntity(
                    amount = amount,
                    category = category,
                    description = description,
                    date = date
                )
            )

        }

    }

    fun delete(expense: ExpenseEntity) {

        viewModelScope.launch {

            repository.delete(expense)

        }

    }

    fun saveBudget(budget: Double) {

        viewModelScope.launch {

            settingsRepository.saveBudget(budget)

        }

    }

//    fun saveExpenseToFirestore(
//        amount: Double,
//        category: String,
//        description: String,
//        date: Long
//    ) {
//
//        firestoreRepository.saveExpense(
//
//            ExpenseFirestore(
//
//                amount,
//                category,
//                description,
//                date
//            ),
//
//            onSuccess = {
//
//            },
//
//            onFailure = {
//
//                Log.e(
//                    "Firestore",
//                    it.message ?: ""
//                )
//
//            }
//
//        )
//
//    }

}