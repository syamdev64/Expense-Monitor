package com.example.expensemonitor.ui.dashboard

import android.R
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensemonitor.categorylist.ExpenseCategory
import com.example.expensemonitor.expenseviewmodel.ExpenseViewModel
import com.example.expensemonitor.ui.navigation.AppNavigation
import com.example.expensemonitor.ui.theme.ExpenseMonitorTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class ExpenseEnterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseMonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black)
                            .padding(innerPadding)
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenMain() {
    val viewModel: ExpenseViewModel = hiltViewModel()
    val datePickerState = rememberDatePickerState()
    val settings by viewModel.settings.observeAsState()
    val expenses by viewModel.expenses.observeAsState(emptyList())
    val totalSpent = expenses.sumOf { it.amount }


    val currentMonthExpense by viewModel.currentMonthExpense.observeAsState(0.0)

    val monthlyBudget = settings?.monthlyBudget ?: 0.0
    val remaining = monthlyBudget - currentMonthExpense
    val progress =
        if (monthlyBudget > 0)
            (currentMonthExpense / monthlyBudget).toFloat()
        else
            0f


    HomeScreenContent(
        monthlyBudget = monthlyBudget,
        //totalSpent = totalSpent,
        totalSpent = currentMonthExpense,
        remaining = remaining,
        progressValue = progress,
        onSaveBudget = { budget ->
            viewModel.saveBudget(budget)
        },

        onAddExpense = { amount, category, description, date ->
            viewModel.insert(amount, category, description, date)
            //   viewModel.saveExpenseToFirestore(amount, category, description, date)
        },
        datePickerState = datePickerState
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    monthlyBudget: Double,
    totalSpent: Double,
    remaining: Double,
    progressValue: Float,
    onSaveBudget: (Double) -> Unit,
    onAddExpense: (Double, String, String, Long) -> Unit,
    datePickerState: DatePickerState
) {

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isFlipped by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }

    val selectedDateText = datePickerState.selectedDateMillis?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Today"
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val categories = ExpenseCategory.entries

    var selectedCategory by remember {
        mutableStateOf(ExpenseCategory.FOOD)
    }

//    val budget = monthlyBudget.toFloat()
//    val spent = totalSpent.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progressValue.coerceIn(0f, 1f),
        animationSpec = tween(1200),
        label = "BudgetProgress"
    )
//    val progress by animateFloatAsState(
//        targetValue = if (budget > 0f) (spent / budget).coerceAtMost(1f) else 0f,
//        animationSpec = tween(1200),
//        label = "BudgetProgress"
//    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070707))
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 110.dp),

        ) {

        Text(
            text = "Expense Tracker",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Manage your daily expenses", color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))


        val rotation by animateFloatAsState(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(700),
            label = ""
        )

        var budgetText by remember(monthlyBudget) {
            mutableStateOf(monthlyBudget.toString())
        }
        // Glass Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .graphicsLayer {

                    rotationY = rotation
//for 3d animation like full screen
                    cameraDistance = 12 * density
                }
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        isFlipped = !isFlipped
                    }
                ),
            border = BorderStroke(
                1.dp, Color.White.copy(alpha = .4f)
            )
        ) {

            if (rotation <= 90f) {

                FrontBudgetCard(
                    budget = budgetText,
                    spent = totalSpent?.toInt().toString(),
                    remaining = remaining?.toInt().toString(),
                    progress = animatedProgress
                )

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        }
                ) {

                    BackBudgetCard(
                        budget = budgetText,

                        onBudgetChange = {
                            budgetText = it
                        },

                        onSave = {

                            budgetText.toDoubleOrNull()?.let {

                                onSaveBudget(it)

                            }

                            isFlipped = false
                        }
                    )

                }

            }

        }

        Spacer(Modifier.height(30.dp))

        // Circular Budget Progress
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {

                    drawArc(
                        color = Color.LightGray.copy(alpha = .3f),
                        startAngle = -90f,
//                        sweepAngle = 360f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(
                            width = 8f, cap = StrokeCap.Round
                        )
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF00C853), Color(0xFF64DD17), Color(0xFF00C853)
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360,
                        useCenter = false,
                        style = Stroke(
                            width = 8f, cap = StrokeCap.Round
                        )
                    )

                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "${(animatedProgress * 100).toInt()}%",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        "Budget Used", color = Color.White,
                        fontSize = 12.sp
                    )

                }

            }

        }

        Spacer(Modifier.height(30.dp))

        Text(
            "Expense Category", fontWeight = FontWeight.Bold, fontSize = 18.sp
        )

        Spacer(Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            categories.forEach { category ->

                FilterChip(

                    selected = selectedCategory == category,

                    onClick = {

                        selectedCategory = category

                    },

                    label = {

                        Text(category.title)

                    },

                    leadingIcon = {

                        Icon(

                            imageVector = category.icon,

                            contentDescription = null,

                            tint = category.color

                        )

                    }

                )

            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = amount, onValueChange = {
                amount = it.filter(Char::isDigit)
            }, modifier = Modifier.fillMaxWidth(), leadingIcon = {
                Text("₹")
            }, label = {
                Text("Expense Amount")
            }, keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ), shape = RoundedCornerShape(18.dp)
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = description, onValueChange = {
                description = it
            }, modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), label = {
                Text("Description")
            }, shape = RoundedCornerShape(18.dp)
        )

        Spacer(Modifier.height(20.dp))

        OutlinedCard(
            onClick = { showDatePicker = true }, // Add onClick listener
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(selectedDateText) // Use the dynamic date text

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {

                if (amount.isNotEmpty()) {

                    onAddExpense(
                        amount.toDouble(),
                        selectedCategory.name,
                        description,
                        datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    )

                    amount = ""
                    description = ""

                    Toast.makeText(
                        context, "Expense Added", Toast.LENGTH_SHORT
                    ).show()

                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C853)
            )
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(Modifier.width(8.dp))

            Text(
                "Add Expense", fontSize = 18.sp,
                color = Color.White
            )

        }

        Spacer(Modifier.height(40.dp))

    }

}

@Composable
fun FrontBudgetCard(
    budget: String,
    spent: String,
    remaining: String,
    progress: Float
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0C0E0E),
                        Color(0xFF383737)
                    )
                )
            )
            .padding(16.dp)
    ) {

        Column {

            Text(
                "Monthly Budget",
                color = Color.White
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "₹$budget",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color.Red.copy(alpha = .8f),
                trackColor = Color.White.copy(.25f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            Spacer(Modifier.height(10.dp))

            Text(
                "Spent ₹$spent",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Remaining ₹$remaining",
                color = Color(0xFF00E676),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

        }

    }

}

@Composable
fun BackBudgetCard(
    budget: String,
    onBudgetChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0C0E0E),
                        Color(0xFF383737)
                    )
                )
            )
            .padding(12.dp)
    ) {
        Text(
            "Edit Monthly Budget",
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = budget,
            onValueChange = onBudgetChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Budget")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C853)
                )
            ) {
                Text(
                    "Save",
                    color = Color.White
                )
            }

            Button(
                onClick = onSave, // You might want a separate onCancel later
                modifier = Modifier.weight(1f), // Share width equally
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    "Cancel",
                    color = Color.White
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ExpenseMonitorTheme {
//        HomeScreenContent(
//            onAddExpense = { _, _, _, _ -> },
//            datePickerState = rememberDatePickerState()
//        )
    }
}



