package com.example.expensemonitor.ui.expensereport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensemonitor.categorylist.ExpenseCategory
import com.example.expensemonitor.expenseviewmodel.ExpenseViewModel
import com.example.expensemonitor.modelclass.Expense
import com.example.expensemonitor.modelclass.FilterType
import com.example.expensemonitor.roomdb.ExpenseEntity
import com.example.expensemonitor.ui.theme.ExpenseMonitorTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import kotlin.collections.emptyList

@AndroidEntryPoint
class ExpenseReportActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseMonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ExpenseScreen()
                    }


                }
            }
        }
    }
}

val expenses = listOf(

    Expense(
        1,
        "Food",
        "Burger King",
        250.0,
        "Today",
        "10:45 AM",
        Icons.Default.Fastfood,
        Color(0xFF4CAF50)
    ),

    Expense(
        2,
        "Travel",
        "Uber Ride",
        420.0,
        "Today",
        "09:30 AM",
        Icons.Default.DirectionsCar,
        Color(0xFF2196F3)
    ),

    Expense(
        3,
        "Shopping",
        "Nike Shoes",
        1999.0,
        "Yesterday",
        "07:20 PM",
        Icons.Default.ShoppingBag,
        Color(0xFFE91E63)
    )
)


@Composable
fun ExpenseCard(
    expense: ExpenseEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {

    }
) {
    val formatter = SimpleDateFormat(
        "dd MMM yyyy • hh:mm a",
        Locale.getDefault()
    )

    val category = remember(expense.category) {

        ExpenseCategory.fromName(expense.category)

    }
    val formattedDate = formatter.format(Date(expense.date))

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .6f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(.15f)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(26.dp)
                )

            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = category.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = expense.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formattedDate,
                    color = Color.Gray,
                    fontSize = 12.sp
                )

            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "- ₹${expense.amount.toInt()}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box {

                    IconButton(
                        onClick = {
                            expanded = true
                        }
                    ) {

                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )

                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {

                        DropdownMenuItem(
                            text = {
                                Text("Edit")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, null)
                            },
                            onClick = {
                                expanded = false
                                onEdit()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Delete")
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = Color.Red
                                )
                            },
                            onClick = {
                                expanded = false
                                onDelete()
                            }
                        )

                    }

                }

            }

        }

    }

}

@Composable
fun ExpenseScreen() {

    val viewModel: ExpenseViewModel = hiltViewModel()

    val expenses by viewModel
        .expenses
        .observeAsState(emptyList())

    var search by remember {
        mutableStateOf("")
    }

    var filter by remember {
        mutableStateOf(FilterType.TODAY)
    }

    val filteredExpenses = expenses.filter { expense ->

        val categoryTitle =
            ExpenseCategory.fromName(expense.category).title

        val searchMatches =
            categoryTitle.contains(search, true) ||
                    expense.description.contains(search, true)

        val dateMatches = when (filter) {

            FilterType.TODAY ->
                isToday(expense.date)

            FilterType.WEEK ->
                isThisWeek(expense.date)

            FilterType.MONTH ->
                isThisMonth(expense.date)

        }

        searchMatches && dateMatches
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070707))
            .padding(12.dp)

    ) {

        SearchAndFilterSection(
            searchQuery = search,
            selectedFilter = filter,
            onSearchChange = {
                search = it
            },
            onFilterSelected = {
                filter = it
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                top = 4.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        )
        {

            items(filteredExpenses) { expense ->
                ExpenseCard(expense, onDelete = {
                    viewModel.delete(expense)
                })
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    selectedFilter: FilterType,
    onSearchChange: (String) -> Unit,
    onFilterSelected: (FilterType) -> Unit
) {

    val filters = FilterType.entries

    Column {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),

            placeholder = {
                Text("Search category or description")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            filters.forEach { filter ->

                FilterChipItem(
                    text = filter.name,
                    selected = selectedFilter == filter,
                    onClick = {
                        onFilterSelected(filter)
                    }
                )

            }

        }

    }

}

@Composable
fun FilterChipItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val background by animateColorAsState(
        if (selected)
            Color(0xFF00C853)
        else
            Color.White.copy(alpha = .4f),
        label = ""
    )

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(40),
        color = background
    ) {

        Text(
            text = text,
            fontSize = 11.sp,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 7.dp
            ),
            color =
                if (selected)
                    Color.White
                else
                    Color.Black
        )

    }

}

/**
 * Date Helper Functions
 */
private fun isToday(timeInMillis: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
    return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isThisWeek(timeInMillis: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
    return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
}

private fun isThisMonth(timeInMillis: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
    return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
}


@Preview
@Composable
fun Expensereport() {
    ExpenseScreen()
}
