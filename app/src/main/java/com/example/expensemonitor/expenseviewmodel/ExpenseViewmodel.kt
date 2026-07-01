package com.example.expensemonitor.expenseviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensemonitor.repository.ExpenseRepository
import com.example.expensemonitor.roomdb.ExpenseDatabase
import com.example.expensemonitor.roomdb.ExpenseEntity
import kotlinx.coroutines.launch

class ExpenseViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    val expenses: LiveData<List<ExpenseEntity>>

    init {

        val dao = ExpenseDatabase
            .getDatabase(application)
            .expenseDao()

        repository = ExpenseRepository(dao)

        expenses = repository
            .expenses
            .asLiveData()
    }

    fun insert(
        amount: Double,
        category: String,
        description: String
    ) {

        viewModelScope.launch {

            repository.insert(
                ExpenseEntity(
                    amount = amount,
                    category = category,
                    description = description,
                    date = System.currentTimeMillis()
                )
            )

        }

    }

}