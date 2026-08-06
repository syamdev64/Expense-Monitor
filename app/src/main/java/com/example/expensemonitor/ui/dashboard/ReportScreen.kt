package com.example.expensemonitor.ui.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensemonitor.auth.AuthViewModel
import com.example.expensemonitor.categorylist.ExpenseCategory
import com.example.expensemonitor.expenseviewmodel.ExpenseViewModel
import com.example.expensemonitor.roomdb.ExpenseEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    val viewModel: ExpenseViewModel = hiltViewModel()
    val expenses by viewModel.expenses.observeAsState(emptyList())
    var selectedChartType by remember { mutableStateOf(ChartType.PIE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .padding(bottom = 110.dp)
    ) {
        Text(
            "Spend Analyzer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Chart Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChartType.entries.forEach { type ->
                FilterChip(
                    selected = selectedChartType == type,
                    onClick = { selectedChartType = type },
                    label = { Text(type.title) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.DarkGray,
                        labelColor = Color.Gray,
                        selectedContainerColor = Color(0xFF00C853),
                        selectedLabelColor = Color.White
                    ),
                    border = null
                )
            }
        }

        if (expenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No data available to analyze", color = Color.Gray)
            }
        } else {
            when (selectedChartType) {
                ChartType.PIE -> CategoryPieChart(expenses)
                ChartType.BAR -> WeeklyBarChart(expenses)
                ChartType.LINE -> MonthlyLineChart(expenses)
            }
        }
    }
}

enum class ChartType(val title: String) {
    PIE("Category"),
    BAR("Weekly"),
    LINE("Monthly")
}

@Composable
fun CategoryPieChart(expenses: List<ExpenseEntity>) {
    val categoryTotals = remember(expenses) {
        expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }
    val total = categoryTotals.values.sum()
    val entries = categoryTotals.entries.toList()

    // Animation progress
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(expenses) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(1000))
    }

    // Selected segment state
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                        .pointerInput(expenses) {
                            detectTapGestures { offset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val angle = Math.toDegrees(
                                    Math.atan2(
                                        (offset.y - center.y).toDouble(),
                                        (offset.x - center.x).toDouble()
                                    )
                                ).toFloat()
                                
                                // Normalize angle to 0-360 starting from -90 (top)
                                var normalizedAngle = (angle + 90f) % 360f
                                if (normalizedAngle < 0) normalizedAngle += 360f

                                var currentStartAngle = 0f
                                var found = false
                                entries.forEachIndexed { index, entry ->
                                    val sweep = (entry.value / total).toFloat() * 360f
                                    if (normalizedAngle >= currentStartAngle && normalizedAngle <= currentStartAngle + sweep) {
                                        selectedCategoryIndex = if (selectedCategoryIndex == index) null else index
                                        found = true
                                    }
                                    currentStartAngle += sweep
                                }
                                if (!found) selectedCategoryIndex = null
                            }
                        }
                ) {
                    val strokeWidth = 25f
                    
                    // Background Ring
                    drawArc(
                        color = Color.White.copy(alpha = 0.05f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    var currentStartAngle = -90f
                    entries.forEachIndexed { index, entry ->
                        val sweepAngle = (entry.value / total).toFloat() * 360f * animationProgress.value
                        val category = ExpenseCategory.fromName(entry.key)
                        val isSelected = selectedCategoryIndex == index
                        
                        drawArc(
                            color = category.color,
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(
                                width = if (isSelected) strokeWidth * 1.3f else strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                        currentStartAngle += sweepAngle
                    }
                }
                
                // Center Text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val label = selectedCategoryIndex?.let { entries[it].key } ?: "Total Spent"
                    val amount = selectedCategoryIndex?.let { entries[it].value } ?: total
                    
                    Text(
                        label,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "₹${String.format(Locale.getDefault(), "%,.0f", amount)}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Legend
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            items(entries.size) { index ->
                val entry = entries[index]
                val category = ExpenseCategory.fromName(entry.key)
                val isSelected = selectedCategoryIndex == index
                
                Surface(
                    onClick = { selectedCategoryIndex = if (isSelected) null else index },
                    color = if (isSelected) Color.White.copy(alpha = 0.05f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(category.color, RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                category.title,
                                color = if (isSelected) category.color else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "₹${String.format(Locale.getDefault(), "%,.0f", entry.value)}",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${String.format(Locale.getDefault(), "%.1f", (entry.value / total) * 100)}%",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyBarChart(expenses: List<ExpenseEntity>) {
    val days = remember {
        (0..6).map { i ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            cal.time
        }.reversed()
    }

    val dailyTotals = remember(expenses) {
        days.map { date ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = sdf.format(date)
            val dayName = SimpleDateFormat("E", Locale.getDefault()).format(date).take(1)
            val amount = expenses.filter {
                sdf.format(Date(it.date)) == dateStr
            }.sumOf { it.amount }
            Triple(dateStr, dayName, amount)
        }
    }

    val maxAmount = dailyTotals.maxOf { it.third }.toFloat().coerceAtLeast(1f)

    // Animation state
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(expenses) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(1000))
    }

    // Interaction state
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Last 7 Days", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                selectedBarIndex?.let { index ->
                    val data = dailyTotals[index]
                    Text(
                        "₹${String.format(Locale.getDefault(), "%,.0f", data.third)}",
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(expenses) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val barWidth = width / dailyTotals.size
                                val index = (offset.x / barWidth).toInt().coerceIn(0, dailyTotals.size - 1)
                                selectedBarIndex = if (selectedBarIndex == index) null else index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height - 30.dp.toPx() // Leave space for labels
                    val barWidth = width / dailyTotals.size
                    val spacing = 12.dp.toPx()
                    val actualBarWidth = barWidth - spacing

                    dailyTotals.forEachIndexed { index, data ->
                        val barHeightFactor = (data.third.toFloat() / maxAmount) * animationProgress.value
                        val barHeight = height * barHeightFactor
                        val x = index * barWidth + spacing / 2
                        val isSelected = selectedBarIndex == index

                        // Background track
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.05f),
                            topLeft = Offset(x, 0f),
                            size = Size(actualBarWidth, height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                        )

                        // Colorful Gradient for the bar
                        // Vary color slightly based on index for "colorfulness"
                        val hueShift = (index * 40f) % 360f
                        val barColor = Color.hsl(hueShift, 0.7f, 0.6f)
                        val barColorEnd = Color.hsl((hueShift + 40f) % 360f, 0.8f, 0.5f)

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = if (isSelected) {
                                    listOf(Color.White, Color(0xFF00E676))
                                } else {
                                    listOf(barColor, barColorEnd)
                                }
                            ),
                            topLeft = Offset(x, height - barHeight),
                            size = Size(actualBarWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                        )

                        // Day label
                        val textResult = textMeasurer.measure(
                            text = data.second,
                            style = TextStyle(
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        drawText(
                            textLayoutResult = textResult,
                            topLeft = Offset(
                                x + (actualBarWidth - textResult.size.width) / 2,
                                height + 8.dp.toPx()
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyLineChart(expenses: List<ExpenseEntity>) {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val monthlyData = remember(expenses) {
        (1..daysInMonth).map { day ->
            val amount = expenses.filter {
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.date
                cal.get(Calendar.MONTH) == currentMonth &&
                        cal.get(Calendar.YEAR) == currentYear &&
                        cal.get(Calendar.DAY_OF_MONTH) == day
            }.sumOf { it.amount }
            day to amount
        }
    }

    val maxAmount = monthlyData.maxOf { it.second }.toFloat().coerceAtLeast(1f)

    // Animation state
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animationProgress.animateTo(1f, tween(durationMillis = 1200))
    }

    // Interaction state
    var selectedX by remember { mutableStateOf<Float?>(null) }
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Monthly Trend",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> selectedX = offset.x },
                                onDragEnd = { selectedX = null },
                                onDragCancel = { selectedX = null },
                                onDrag = { change, _ ->
                                    selectedX = change.position.x
                                }
                            )
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val stepX = width / (daysInMonth - 1)

                    // Draw Horizontal Grid Lines
                    val gridCount = 4
                    for (i in 0..gridCount) {
                        val y = height - (i * height / gridCount)
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val points = monthlyData.mapIndexed { index, data ->
                        Offset(index * stepX, height - (data.second.toFloat() / maxAmount * height))
                    }

                    if (points.isNotEmpty()) {
                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 0 until points.size - 1) {
                                val p1 = points[i]
                                val p2 = points[i + 1]
                                // Cubic bezier for smoothness
                                val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2f, p1.y)
                                val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2f, p2.y)
                                cubicTo(
                                    controlPoint1.x, controlPoint1.y,
                                    controlPoint2.x, controlPoint2.y,
                                    p2.x, p2.y
                                )
                            }
                        }

                        // Entrance animation clip
                        clipRect(right = width * animationProgress.value) {
                            // Main Line
                            drawPath(
                                path = path,
                                color = Color(0xFF00E676),
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Gradient Area
                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(width, height)
                                lineTo(0f, height)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF00E676).copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                        }

                        // Interaction Logic
                        selectedX?.let { x ->
                            val boundedX = x.coerceIn(0f, width)
                            val index = (boundedX / stepX).toInt().coerceIn(0, daysInMonth - 1)
                            val dataPoint = points[index]
                            val dataValue = monthlyData[index]

                            // Vertical Indicator line
                            drawLine(
                                color = Color.White.copy(alpha = 0.3f),
                                start = Offset(dataPoint.x, 0f),
                                end = Offset(dataPoint.x, height),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Highlight point
                            drawCircle(
                                color = Color(0xFF00E676),
                                radius = 6.dp.toPx(),
                                center = dataPoint
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = dataPoint
                            )

                            // Tooltip info
                            val label = "Day ${dataValue.first}: ₹${dataValue.second}"
                            val textResult = textMeasurer.measure(
                                text = label,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            val tooltipX = (dataPoint.x - textResult.size.width / 2)
                                .coerceIn(0f, width - textResult.size.width)
                            val tooltipY = (dataPoint.y - 45.dp.toPx())
                                .coerceIn(0f, height - textResult.size.height)

                            drawText(
                                textLayoutResult = textResult,
                                topLeft = Offset(tooltipX, tooltipY)
                            )
                        }
                    }
                }
            }
        }
    }
}


