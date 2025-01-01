package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round
import android.graphics.Paint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class OverviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            overview(navController)


        }
    }
}

fun logout(navController: NavController, context: Context){
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    sharedPreferences.edit().remove("userId").commit();
    navController.navigate("main_page")
}

//got the code from https://medium.com/@developerchunk/create-custom-bargraph-with-scales-in-jetpack-compose-android-studio-kotlin-deadba24fd9b
//then made changes needed
data class MoodEntry1(val userId: String, val mood: String, val count: Int = 0)

enum class BarType {
    CIRCULAR_TYPE, TOP_CURVED
}

@Composable
fun BarGraph(
    graphBarData: List<Float>,
    xAxisScaleData: List<String>,
    bar: List<Int>,
    height: Dp,
    roundType: BarType,
    barWidth: Dp,
    barColor:List<Color>,
    barArrangement: Arrangement.Horizontal
) {
    val barData by remember { mutableStateOf(bar + 0) }


    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp

    val xAxisScaleHeight = 40.dp
    val yAxisScaleSpacing by remember { mutableStateOf(100f) }
    val yAxisTextWidth by remember { mutableStateOf(100.dp) }

    val barShape = when (roundType) {
        BarType.CIRCULAR_TYPE -> CircleShape
        BarType.TOP_CURVED -> RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
    }

    val density = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .padding(top = xAxisScaleHeight, end = 3.dp)
                .height(height)
                .fillMaxWidth(),
            horizontalAlignment = CenterHorizontally
        ) {
            Canvas(modifier = Modifier.padding(bottom = 10.dp).fillMaxSize()) {
                // Y-Axis Scale Text
                val yAxisScaleText = (barData.max()) / 3f

                // Create the textPaint object once outside of the loop
                val textPaint = android.graphics.Paint().apply {
                    color = Color.White.toArgb()
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 16.sp.toPx()
                }

                // Loop for drawing the y-axis labels
                (0..3).forEach { i ->
                    drawContext.canvas.nativeCanvas.apply {
                        val yPosition = size.height - yAxisScaleSpacing - i * size.height / 3f
                        // Draw text for each y-axis label
                        drawText(
                            round(barData.min() + yAxisScaleText * i).toString(),
                            30f,  // X position of the text
                            yPosition,  // Y position of the text
                            textPaint  // Paint object used for drawing
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 50.dp)
                .width(width - yAxisTextWidth)
                .height(height + xAxisScaleHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.width(width - yAxisTextWidth),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = barArrangement
            ) {
                graphBarData.forEachIndexed { index, value ->
                    var animationTriggered by remember { mutableStateOf(false) }

                    val graphBarHeight by animateFloatAsState(
                        targetValue = if (animationTriggered) value else 0f,
                        animationSpec = tween(durationMillis = 1000, delayMillis = 0)
                    )


                    LaunchedEffect(key1 = true) {
                        animationTriggered = true
                    }

                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .clip(barShape)
                                .width(barWidth)
                                .height(height - 10.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(barShape)
                                    .fillMaxWidth()
                                    .fillMaxHeight(graphBarHeight)
                                    .background(barColor[index])
                            )
                        }

                        Column(
                            modifier = Modifier
                                .height(xAxisScaleHeight),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                                    .width(5.dp)
                                    .height(10.dp)
                                    .background(color = Color.White)
                            )
                            Text(
                                modifier = Modifier.padding(bottom = 3.dp),
                                text = xAxisScaleData[index],
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// Function to read mood data from a file (for the real app)
fun readMoodBar(context: Context, userId: String): List<MoodEntry1> {
    return try {
        val inputStream = context.openFileInput("moodSELECT.txt")
        val lines = BufferedReader(InputStreamReader(inputStream))

        val moodcount = mutableMapOf<String, Int>()

        lines.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 3 && parts[0] == userId) {
                val mood = parts[2].trim()
                val count = moodcount[mood] ?: 0
                moodcount[mood] = count + 1
            }
        }

        moodcount.map { (mood, count) ->
            MoodEntry1(userId, mood, count)
        }

    } catch (e: Exception) {
        Log.e("MoodHistory", "Error reading or processing moodSELECT.txt", e)
        emptyList()
    }
}

@Composable
fun MoodGraph() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    var moodHistory by remember { mutableStateOf<List<MoodEntry1>>(emptyList()) }

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            moodHistory = readMoodBar(context, userId)
        } else {
            Log.e("MoodHistory", "Error: User ID is null or empty")
        }
    }

    if (moodHistory.isEmpty()) {
        Text("No mood history available", color = Color.White)
    } else {
        val allMoods = listOf("Joyful", "Happy", "Meh", "Bad", "Down")
        val barColors = listOf(
            Color(0xFF64B5F6),
            Color(0xFF81C784),
            Color(0xFFFFEB3B),
            Color(0xFFFF7043),
            Color(0xFFEF5350)
        )

        // Get the max value for count to dynamically scale graph
        val maxCount = moodHistory.maxOfOrNull { it.count } ?: 1

        // get the missing moods if they are 0 so they can come up
        val fixedMoodHistory = allMoods.map { mood ->
            moodHistory.find { it.mood == mood } ?: MoodEntry1(userId ?: "", mood, 0)
        }

        fixedMoodHistory.forEach { moodEntry ->
            Log.d("MoodGraph", "Mood: ${moodEntry.mood}, Count: ${moodEntry.count}")
        }

        Text(
            text = "Mood Count",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))



            BarGraph(
                // get the mood counts from Moodentry1 list
                //Highest bar would be the maxCount
                graphBarData = fixedMoodHistory.map { it.count.toFloat() / maxCount },
                xAxisScaleData = allMoods,
                bar = fixedMoodHistory.map { it.count },
                height = 300.dp,
                roundType = BarType.TOP_CURVED,
                barWidth = 40.dp,
                barColor = barColors,
                barArrangement = Arrangement.SpaceEvenly
            )
        }
    }
}

@Composable
fun overview(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)

    systemUiController.setStatusBarColor(color = Color.Black)
    systemUiController.setNavigationBarColor(color = colorResource(R.color.lightpurple))

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val anxietyData = remember { mutableStateOf<List<Pair<Long, Int>>>(emptyList()) }
    val stressData = remember { mutableStateListOf<Pair<String, Int>>() }

    // Load data for anxiety and stress
    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            anxietyData.value = readAnxietyData1(context, userId)
            stressData.clear()
            stressData.addAll(readStressDataForPieChart(context, userId).toList()) // Explicit conversion
            Log.d("Overview", "Loaded data for user: $userId")
        } else {
            Log.e("Overview", "Error: User ID is null or empty")
        }
    }



    ModalDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(navController, context) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Overview") },
                    backgroundColor = colorResource(R.color.lightpurple),
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                )
            },
            content = { padding ->
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .background(color = Color.Black)
                        .padding(padding)
                ) {
                    // Mood Graph Section
                    Text(
                        text = "Mood Overview",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                    MoodGraph()

                    Spacer(modifier = Modifier.height(32.dp))

                    // Stress Pie Chart Section
                    Text(
                        text = "Stress Overview",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                    if (stressData.isEmpty()) {
                        Text(
                            text = "No stress data available",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        StressPieChart(data = stressData)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Anxiety Line Graph Section
                    Text(
                        text = "Anxiety Trends",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                    if (anxietyData.value.isEmpty()) {
                        Text(
                            text = "No anxiety data available",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        AnxietyLineChart1(data = anxietyData.value)
                    }
                }
            },
            bottomBar = {
                BottomNavigationBar(navController)
            }
        )
    }
}



@Composable
fun AnxietyLineGraph() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    var anxietyData by remember { mutableStateOf<List<Pair<Long, Int>>>(emptyList()) }

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            anxietyData = readAnxietyData1(context, userId)
        } else {
            Log.e("AnxietyLineGraph", "Error: User ID is null or empty")
        }
    }

    if (anxietyData.isEmpty()) {
        Text("No anxiety data available", color = Color.White)
    } else {
        Text(
            text = "Anxiety Trends",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            AnxietyLineChart1(data = anxietyData)
        }
    }
}


/**
 * Reads anxiety data from a file and returns a list of (timestamp, anxietyLevel) pairs.
 */
fun readAnxietyData1(context: Context, userID: String): List<Pair<Long, Int>> {
    val filename = "anxiety_data.txt"
    val dataList = mutableListOf<Pair<Long, Int>>()

    try {
        val fileInputStream = context.openFileInput(filename)
        val reader = BufferedReader(InputStreamReader(fileInputStream))

        var currentId: String? = null
        var currentDate: String? = null
        var currentLevel: String? = null

        reader.forEachLine { line ->
            when {
                line.startsWith("ID: ") -> {
                    currentId = line.substringAfter("ID: ").trim()
                    Log.d("AnxietyData", "Parsed ID: $currentId")
                }
                line.startsWith("Date: ") -> {
                    currentDate = line.substringAfter("Date: ").trim()
                    Log.d("AnxietyData", "Parsed Date: $currentDate")
                }
                line.startsWith("Level: ") -> {
                    currentLevel = line.substringAfter("Level: ").trim()
                    Log.d("AnxietyData", "Parsed Level: $currentLevel")
                }
                line.startsWith("Notes: ") -> {
                    if (currentId == userID && currentDate != null && currentLevel != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val timestamp = dateFormat.parse(currentDate)?.time
                        val level = convertAnxietyLevelToNumber1(currentLevel!!)

                        if (timestamp != null && level != null) {
                            dataList.add(timestamp to level)
                            Log.d("AnxietyData", "Added data - Timestamp: $timestamp, Level: $level")
                        } else {
                            Log.e("AnxietyData", "Failed to parse timestamp or level")
                        }
                    } else {
                        Log.w(
                            "AnxietyData",
                            "Skipped entry - UserID: $currentId, Date: $currentDate, Level: $currentLevel"
                        )
                    }

                    // Reset fields for the next entry
                    currentId = null
                    currentDate = null
                    currentLevel = null
                }
            }
        }
    } catch (e: FileNotFoundException) {
        Log.e("FileCheck", "anxiety_data.txt not found")
    } catch (e: Exception) {
        Log.e("ReadDataError", "Error reading anxiety data", e)
    }

    // Log the final list of parsed data
    dataList.forEach { Log.d("FinalAnxietyData", "Timestamp: ${it.first}, Level: ${it.second}") }

    return dataList.reversed() // Return in reverse chronological order
}



fun convertAnxietyLevelToNumber1(level: String): Int? {
    val levels = listOf(
        "Not Anxious", "Very Bad", "Bad", "Anxiety Comes and Goes",
        "Mild Anxiety", "Anxiety Triggered", "Anxious but Survive",
        "Constant Fidgeting", "Anxiety Attack", "Panic Attack"
    )
    return levels.indexOf(level).takeIf { it != -1 }
}


/**
 * Draws a line chart based on anxiety data with labelled axes.
 */
@Composable
fun AnxietyLineChart1(data: List<Pair<Long, Int>>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No anxiety data available", color = Color.White)
        }
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    ) {
        drawLineChartWithAxes(data, Color.Blue, Color.Red)
    }
}

/**
 * Helper function to draw the chart inside the Canvas with axes and labels.
 */
private fun DrawScope.drawLineChartWithAxes(
    data: List<Pair<Long, Int>>,
    lineColor: Color,
    pointColor: Color
) {
    val maxX = data.maxOfOrNull { it.first } ?: 1L
    val minX = data.minOfOrNull { it.first } ?: 0L
    val maxY = 10 // Fixed Y-axis max value
    val minY = 0 // Fixed Y-axis min value

    val xScale = size.width / (maxX - minX).toFloat()
    val yScale = size.height / (maxY - minY).toFloat()

    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault()) // Format for X-axis labels

    Log.d("LineChartScale", "X-Axis Range: $minX to $maxX, Y-Axis Range: $minY to $maxY") // Debugging

    val points = data.map { (x, y) ->
        androidx.compose.ui.geometry.Offset(
            x = (x - minX) * xScale,
            y = size.height - (y - minY) * yScale
        )
    }

    points.forEach { point ->
        Log.d("LineChartPoints", "Point: $point") // Debugging
    }

    for (i in 0 until points.size - 1) {
        drawLine(
            color = lineColor,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 4f
        )
    }

    points.forEach { point ->
        drawCircle(
            color = pointColor,
            center = point,
            radius = 6f
        )
    }

    // Draw Y-axis
    drawLine(
        color = Color.White,
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(0f, size.height),
        strokeWidth = 2f
    )

    // Draw X-axis
    drawLine(
        color = Color.White,
        start = androidx.compose.ui.geometry.Offset(0f, size.height),
        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
        strokeWidth = 2f
    )

    // Add Y-axis labels
    for (i in minY..maxY) {
        val yPosition = size.height - (i - minY) * yScale
        drawContext.canvas.nativeCanvas.drawText(
            "$i",
            -30f, // Position slightly to the left of the axis
            yPosition,
            android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 30f
            }
        )
    }

    // Add X-axis labels
    val numberOfLabels = 5 // Number of labels to show on the X-axis
    val interval = (maxX - minX) / numberOfLabels
    for (i in 0..numberOfLabels) {
        val xValue = minX + i * interval
        val xPosition = (xValue - minX) * xScale
        val dateLabel = dateFormat.format(Date(xValue))

        drawContext.canvas.nativeCanvas.drawText(
            dateLabel,
            xPosition,
            size.height + 30f, // Position slightly below the axis
            android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 30f
            }
        )
    }
}



data class StressEntry2(val userId: String, val stressLevel: String, val count: Int = 0)

fun readStressDataForPieChart(context: Context, userId: String): List<Pair<String, Int>> {
    val filename = "stress_history.txt"
    val stressCounts = mutableMapOf<String, Int>()

    try {
        val fileInputStream = context.openFileInput(filename)
        val reader = BufferedReader(InputStreamReader(fileInputStream))

        var currentId: String? = null
        var currentLevel: String? = null

        reader.forEachLine { line ->
            when {
                line.startsWith("ID: ") -> {
                    currentId = line.substringAfter("ID: ").trim()
                }
                line.startsWith("Level: ") -> {
                    currentLevel = line.substringAfter("Level: ").trim()
                }
                line.startsWith("Notes: ") -> {
                    if (currentId == userId && currentLevel != null) {
                        val count = stressCounts[currentLevel] ?: 0
                        stressCounts[currentLevel!!] = count + 1
                    }
                    // Reset fields for the next entry
                    currentId = null
                    currentLevel = null
                }
            }
        }
    } catch (e: Exception) {
        Log.e("StressData", "Error reading stress data", e)
    }

    return stressCounts.toList()
}



@Composable
fun StressPieChart() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    var stressData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            stressData = readStressDataForPieChart(context, userId)
        } else {
            Log.e("StressPieChart", "Error: User ID is null or empty")
        }
    }

    if (stressData.isEmpty()) {
        Text("No stress data available", color = Color.White)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Stress Overview",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(16.dp))
            DrawStressPieChart(stressData)
        }
    }
}

@Composable
fun DrawStressPieChart(data: List<Pair<String, Int>>) {
    val total = data.sumOf { it.second.toDouble() }.toFloat()
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        var startAngle = 0f

        data.forEachIndexed { index, entry ->
            val sweepAngle = (entry.second / total) * 360f
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }

        // Add labels
        startAngle = 0f
        data.forEachIndexed { index, entry ->
            val sweepAngle = (entry.second / total) * 360f
            val angle = startAngle + sweepAngle / 2
            val radius = size.minDimension / 3
            val x = center.x + radius * cos(angle * PI / 180).toFloat()
            val y = center.y + radius * sin(angle * PI / 180).toFloat()

            drawContext.canvas.nativeCanvas.drawText(
                entry.first,
                x,
                y,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            startAngle += sweepAngle
        }
    }
}




@Composable
fun StressPieChart(data: List<Pair<String, Int>>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No stress data available", color = Color.White)
        }
        return
    }

    val total = data.sumOf { it.second.toDouble() }.toFloat()
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        var startAngle = 0f

        data.forEachIndexed { index, entry ->
            val sweepAngle = (entry.second / total) * 360f
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }

        // Add labels
        startAngle = 0f
        data.forEachIndexed { index, entry ->
            val sweepAngle = (entry.second / total) * 360f
            val angle = startAngle + sweepAngle / 2
            val radius = size.minDimension / 3
            val x = center.x + radius * cos(angle * PI / 180).toFloat()
            val y = center.y + radius * sin(angle * PI / 180).toFloat()

            drawContext.canvas.nativeCanvas.drawText(
                entry.first,
                x,
                y,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            startAngle += sweepAngle
        }
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun PreviewStressPieChart1() {
//    val dummyData = listOf(
//        StressEntry2(userId = "1", stressLevel = "Overwhelmed", count = "5"),
//        StressEntry2(userId = "1", stressLevel = "Calm", count = "10"),
//        StressEntry2(userId = "1", stressLevel = "Content", count = "7")
//    )
//
//    // Transform dummyData into a List<Pair<String, Int>>
//    val transformedData = dummyData.map { it.stressLevel to it.count.toInt() }
//
//    StressPieChart(data = transformedData)
//}


@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = colorResource(R.color.lightpurple),
        contentColor = Color.Black,
        modifier = Modifier.padding(bottom = 22.dp)
    ) {
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("overview_screen") },
            icon = {
                Icon(
                    Icons.Default.Dashboard,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            },
            label = { Text("Overview", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("history_screen") },
            icon = {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            },
            label = { Text("History", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("mood_screen") },
            icon = {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).padding(top = 10.dp)
                )
            }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("stress_screen") },
            icon = {
                Icon(
                    Icons.Default.Medication,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            },
            label = { Text("Med", fontSize = 16.sp) }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("advice_screen") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Help",
                    modifier = Modifier.size(48.dp).padding(top = 10.dp)
                )
            },
            label = { Text("Advice", fontSize = 13.sp) }
        )
    }
}

@Composable
fun DrawerContent(navController: NavController, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.lightpurple))
            .padding(16.dp)
    ) {
        Text(
            text = "Navigation Menu",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top= 20.dp,bottom = 10.dp)
        )
        Divider(color = Color.Black, thickness = 1.dp)

        DrawerItem("overview", Icons.Default.Dashboard) { navController.navigate("overview_screen") }
        DrawerItem("Advice", Icons.Default.Phone) { navController.navigate("advice_screen") }
        DrawerItem("Mood", Icons.Default.AddCircle) { navController.navigate("mood_screen") }
        DrawerItem("Stress Level", Icons.Default.BatteryAlert) { navController.navigate("stress_screen") }
        DrawerItem("Anxiety Level", Icons.Default.Warning) { navController.navigate("anxiety_screen") }
        DrawerItem("Logout", Icons.Default.Logout) { logout(navController, context) }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnxietyLineChartPreview() {
    AnxietyLineChart1(
        data = listOf(
            1672531200000L to 5, // Example timestamp and level
            1672617600000L to 7,
            1672704000000L to 3
        )
    )
}



//@Preview(showBackground = true)
//@Composable
//fun BarChartPreview() {
//    val moodHistory = listOf(
//        MoodEntry1(userId = "1", mood = "Joyful", count = 5),
//        MoodEntry1(userId = "1", mood = "Happy", count = 8),
//        MoodEntry1(userId = "1", mood = "Meh", count = 2),
//        MoodEntry1(userId = "1", mood = "Bad", count = 0),
//        MoodEntry1(userId = "1", mood = "Down", count = 3)
//    )
//
//    val barColors = listOf(
//        Color(0xFF64B5F6),
//        Color(0xFF81C784),
//        Color(0xFFFFEB3B),
//        Color(0xFFFF7043),
//        Color(0xFFEF5350)
//    )
//
//    val allMoods = listOf("Joyful", "Happy", "Meh", "Bad", "Down")
//    val maxCount = moodHistory.maxOfOrNull { it.count } ?: 1
//    val yaxisLabels = (0..maxCount).toList()
//
//    moodHistory.forEach { moodEntry ->
//        Log.d("BarChartPreview", "Mood: ${moodEntry.mood}, Count: ${moodEntry.count}")
//    }
//
//
//    Column(
//        modifier = Modifier
//            .padding(horizontal = 30.dp)
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        BarGraph(
//            graphBarData =  moodHistory.map { it.count.toFloat() / maxCount },
//            xAxisScaleData = allMoods,
//            bar = moodHistory.map { it.count },
//            height = 300.dp,
//            roundType = BarType.TOP_CURVED,
//            barWidth = 40.dp,
//            barColor = barColors,
//            barArrangement = Arrangement.SpaceEvenly
//        )
//    }
//}

//hello1