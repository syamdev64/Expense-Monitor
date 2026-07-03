package com.example.expensemonitor.expenseviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensemonitor.repository.ExpenseRepository
import com.example.expensemonitor.repository.SettingsRepository
import com.example.expensemonitor.roomdb.ExpenseDatabase
import com.example.expensemonitor.roomdb.ExpenseEntity
import kotlinx.coroutines.launch

class ExpenseViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: ExpenseRepository
    val expenses: LiveData<List<ExpenseEntity>>
    private val database =
        ExpenseDatabase.getDatabase(application)

    private val repositorysettings =
        SettingsRepository(database.settingsDao())

    val settings =
        repositorysettings.settings.asLiveData()

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
        description: String,date:Long
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

            repositorysettings.saveBudget(budget)

        }

    }

}