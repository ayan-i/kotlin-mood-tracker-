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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// OverviewActivity serves as a screen for displaying an overview of the app's features
class OverviewActivity : ComponentActivity() {
    // Called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Initialises the navigation controller for managing navigation
            val navController = rememberNavController()
            // Displays the overview screen, passing the navigation controller
            overview(navController)


        }
    }
}
// Function to handle user logout
fun logout(navController: NavController, context: Context){
    // Access the shared preferences to manage user session data
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    // Retrieve the userId from shared preferences
    val userId = sharedPreferences.getString("userId", null)
    // Remove the userId from shared preferences to log the user out
    sharedPreferences.edit().remove("userId").commit();
    // Navigate back to the main page after logout
    navController.navigate("main_page")
}

//got the code from https://medium.com/@developerchunk/create-custom-bargraph-with-scales-in-jetpack-compose-android-studio-kotlin-deadba24fd9b
//then made changes needed

// Data class representing a mood entry with default count set to zero
data class MoodEntry1(val userId: String, val mood: String, val count: Int = 0)
// Enum representing different types of bar graph styles
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
    // Store bar data with an extra zero appended for scaling purposes
    val barData by remember { mutableStateOf(bar + 0) }

// Access device configuration for screen width calculation
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp
    // Define dimensions for x-axis and y-axis scaling
    val xAxisScaleHeight = 40.dp
    val yAxisScaleSpacing by remember { mutableStateOf(100f) }
    val yAxisTextWidth by remember { mutableStateOf(100.dp) }
    // Determine the shape of the bars based on the round type specified
    val barShape = when (roundType) {
        BarType.CIRCULAR_TYPE -> CircleShape
        BarType.TOP_CURVED -> RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
    }

    val density = LocalDensity.current
    // Outer container for the entire graph
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        // Column for the Y-axis labels and scaling
        Column(
            modifier = Modifier
                .padding(top = xAxisScaleHeight, end = 3.dp)
                .height(height)
                .fillMaxWidth(),
            horizontalAlignment = CenterHorizontally
        ) {

            Canvas(modifier = Modifier.padding(bottom = 10.dp).fillMaxSize()) {
                // Y-Axis Scale Text
                // Calculate Y-axis scale text based on the maximum value
                val yAxisScaleText = (barData.max()) / 3f

                // Initialize a Paint object for drawing Y-axis labels
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
        // Container for bars and X-axis labels
        Box(
            modifier = Modifier
                .padding(start = 50.dp)
                .width(width - yAxisTextWidth)
                .height(height + xAxisScaleHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Row for bar graph rendering
            Row(
                modifier = Modifier.width(width - yAxisTextWidth),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = barArrangement
            ) {

                graphBarData.forEachIndexed { index, value ->
                    // Control animation trigger state
                    var animationTriggered by remember { mutableStateOf(false) }
                    // Animate the bar height on trigger
                    val graphBarHeight by animateFloatAsState(
                        targetValue = if (animationTriggered) value else 0f,
                        animationSpec = tween(durationMillis = 1000, delayMillis = 0)
                    )

                    // Trigger the animation on initial composition
                    LaunchedEffect(key1 = true) {
                        animationTriggered = true
                    }
                    // Column for individual bars and their respective labels
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = CenterHorizontally
                    ) {
                        // Bar container with clipping and animation
                        Box(
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .clip(barShape)
                                .width(barWidth)
                                .height(height - 10.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Actual animated bar
                            Box(
                                modifier = Modifier
                                    .clip(barShape)
                                    .fillMaxWidth()
                                    .fillMaxHeight(graphBarHeight)
                                    .background(barColor[index])
                            )
                        }
                        // X-axis labels and markers
                        Column(
                            modifier = Modifier
                                .height(xAxisScaleHeight),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = CenterHorizontally
                        ) {
                            // X-axis tick mark
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                                    .width(5.dp)
                                    .height(10.dp)
                                    .background(color = Color.White)
                            )
                            // X-axis label text
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
    // Access the local context and retrieve shared preferences
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    // State to hold mood history data
    var moodHistory by remember { mutableStateOf<List<MoodEntry1>>(emptyList()) }
    // Load mood history when the user ID changes
    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            moodHistory = readMoodBar(context, userId)
        } else {
            Log.e("MoodHistory", "Error: User ID is null or empty")
        }
    }
    // Check if there is any mood history data available
    if (moodHistory.isEmpty()) {
        // Display message when there is no mood history
        Text(
            text = "Mood Count",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top=25.dp,start=16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("No mood history available", color = Color.White, fontSize = 17.sp, modifier = Modifier.padding(start=16.dp))
    } else {
        // Define all mood categories and corresponding bar colors
        val allMoods = listOf("Joyful", "Happy", "Meh", "Bad", "Down")
        val barColors = listOf(
            Color(0xFF64B5F6),
            Color(0xFF81C784),
            Color(0xFFFFEB3B),
            Color(0xFFFF7043),
            Color(0xFFEF5350)
        )

        // Calculate the maximum mood count for dynamic graph scaling
        val maxCount = moodHistory.maxOfOrNull { it.count } ?: 1

        // Ensure all mood categories are represented in the graph, even with zero counts
        val fixedMoodHistory = allMoods.map { mood ->
            moodHistory.find { it.mood == mood } ?: MoodEntry1(userId ?: "", mood, 0)
        }
        // Debug log for mood entries
        fixedMoodHistory.forEach { moodEntry ->
            Log.d("MoodGraph", "Mood: ${moodEntry.mood}, Count: ${moodEntry.count}")
        }
        // Display the mood count heading
        Text(
            text = "Mood Count",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top=25.dp,start=16.dp)
        )
        Spacer(modifier = Modifier.padding(20.dp))
        // Display the bar graph within a column layout
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Render the BarGraph component with normalized mood count data
            BarGraph(
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
    // Control system UI appearance (status bar and navigation bar)
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)

    // Set the status bar and navigation bar colors
    systemUiController.setStatusBarColor(color = Color.Black)
    systemUiController.setNavigationBarColor(color = colorResource(R.color.lightpurple))

    // Drawer and coroutine scope setup for UI interaction
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // State holders for anxiety and stress data
    val anxietyData = remember { mutableStateOf<List<Pair<Long, Int>>>(emptyList()) }
    val stressData = remember { mutableStateListOf<Pair<String, Int>>() }

    // Load anxiety and stress data when the user ID changes
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


    // Modal drawer component to hold the navigation drawer
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(navController, context) }
    ) {
        // Main scaffold layout for top bar, content, and bottom navigation
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                    modifier = Modifier.padding(top = 25.dp)
                )
            },
            content = { padding ->
                val scrollState = rememberScrollState()

                // Main column layout for content sections
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .background(color = Color.Black)
                        .padding(padding)

                ) {
                    // Mood Graph Section
                    MoodGraph()

                    Spacer(modifier = Modifier.height(32.dp))

                    // Stress Pie Chart Section
                    Text(
                        text = "Stress Overview",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                    if (stressData.isEmpty()) {
                        Text(
                            text = "No stress data available",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                            fontSize=17.sp
                        )
                    } else {
                        DrawStressPieChart(data = stressData)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Anxiety Line Graph Section
                    Text(
                        text = "Anxiety Trends",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                    if (anxietyData.value.isEmpty()) {
                        Text(
                            text = "No anxiety data available",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                            fontSize=17.sp
                        )
                    } else {
                        AnxietyLineChart1(data = anxietyData.value)
                    }
                    Spacer(modifier = Modifier.padding(15.dp))
                }
            },
            // Bottom navigation bar component
            bottomBar = {
                BottomNavigationBar(navController)
            }
        )
    }
}



//@Composable
//fun AnxietyLineGraph() {
//    val context = LocalContext.current
//    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
//    val userId = sharedPreferences.getString("userId", null)
//    var anxietyData by remember { mutableStateOf<List<Pair<Long, Int>>>(emptyList()) }
//
//    LaunchedEffect(userId) {
//        if (!userId.isNullOrEmpty()) {
//            anxietyData = readAnxietyData1(context, userId)
//        } else {
//            Log.e("AnxietyLineGraph", "Error: User ID is null or empty")
//        }
//    }
//
//    if (anxietyData.isEmpty()) {
//        Text("No anxiety data available", color = Color.White)
//    } else {
//        Text(
//            text = "Anxiety Trends",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.White,
//        )
//
//        Column(
//            modifier = Modifier
//                .padding(horizontal = 30.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(20.dp))
//
//            AnxietyLineChart1(data = anxietyData)
//        }
//    }
//}


/**
 * Reads anxiety data from a file and returns a list of (timestamp, anxietyLevel) pairs.
 */
fun readAnxietyData1(context: Context, userID: String): List<Pair<Long, Int>> {
    // Define the filename and initialize a mutable list to store data
    val filename = "anxiety_data.txt"
    val dataList = mutableListOf<Pair<Long, Int>>()

    try {
        // Open the file for reading
        val fileInputStream = context.openFileInput(filename)
        val reader = BufferedReader(InputStreamReader(fileInputStream))
        // Variables to hold the current data entry values
        var currentId: String? = null
        var currentDate: String? = null
        var currentLevel: String? = null

        // Read each line of the file and parse it
        reader.forEachLine { line ->
            when {
                // Parse the user ID
                line.startsWith("ID: ") -> {
                    currentId = line.substringAfter("ID: ").trim()
                    Log.d("AnxietyData", "Parsed ID: $currentId")
                }
                // Parse the date
                line.startsWith("Date: ") -> {
                    currentDate = line.substringAfter("Date: ").trim()
                    Log.d("AnxietyData", "Parsed Date: $currentDate")
                }
                // Parse the anxiety level
                line.startsWith("Level: ") -> {
                    currentLevel = line.substringAfter("Level: ").trim()
                    Log.d("AnxietyData", "Parsed Level: $currentLevel")
                }
                // Parse notes and finalize the data entry
                line.startsWith("Notes: ") -> {
                    // Validate data before adding to the list
                    if (currentId == userID && currentDate != null && currentLevel != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val timestamp = dateFormat.parse(currentDate)?.time
                        val level = convertAnxietyLevelToNumber1(currentLevel!!)

                        // If parsing is successful, add the entry
                        if (timestamp != null && level != null) {
                            dataList.add(timestamp to level)
                            Log.d("AnxietyData", "Added data - Timestamp: $timestamp, Level: $level")
                        } else {
                            Log.e("AnxietyData", "Failed to parse timestamp or level")
                        }
                    } else {
                        // Log warning for invalid entries
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
        // Log an error if the file is not found
        Log.e("FileCheck", "anxiety_data.txt not found")
    } catch (e: Exception) {
        // Log a generic error if an exception occurs
        Log.e("ReadDataError", "Error reading anxiety data", e)
    }

    // Log the final parsed data for debugging purposes
    dataList.forEach { Log.d("FinalAnxietyData", "Timestamp: ${it.first}, Level: ${it.second}") }
    // Return the data list in reverse chronological order
    return dataList.reversed() // Return in reverse chronological order
}



fun convertAnxietyLevelToNumber1(level: String): Int? {
    // Define a list of anxiety levels from least to most severe
    val levels = listOf(
        "Not Anxious", "Very Bad", "Bad", "Anxiety Comes and Goes",
        "Mild Anxiety", "Anxiety Triggered", "Anxious but Survive",
        "Constant Fidgeting", "Anxiety Attack", "Panic Attack"
    )
    // Return the index of the provided level if it exists in the list, otherwise return null
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
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No anxiety data available", color = Color.White, fontWeight = FontWeight.Bold)
        }
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(20.dp)

    ) {
        drawLineChartWithAxes(data, Color(0xFF64B5F6), Color.Red)

    }
}

/**
 * Helper function to draw the chart inside the Canvas with axes and labels.
 */
private fun DrawScope.drawLineChartWithAxes(
    data: List<Pair<Long, Int>>, // List of data points (x, y) pairs
    lineColor: Color, // Color for the line graph
    pointColor: Color // Color for the points on the graph
) {
    // Calculate the maximum and minimum values for the axes
    val maxX = data.maxOfOrNull { it.first } ?: 1L
    val minX = data.minOfOrNull { it.first } ?: 0L
    val maxY = 10 // Fixed Y-axis max value
    val minY = 0 // Fixed Y-axis min value

    // Calculate scaling factors for both axes
    val xScale = size.width / (maxX - minX).toFloat()
    val yScale = size.height / (maxY - minY).toFloat()

    // Format for X-axis date labels
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault()) // Format for X-axis labels

    // Debug log for scale ranges
    Log.d("LineChartScale", "X-Axis Range: $minX to $maxX, Y-Axis Range: $minY to $maxY") // Debugging

    // Convert data points into drawable coordinates
    val points = data.map { (x, y) ->
        androidx.compose.ui.geometry.Offset(
            x = (x - minX) * xScale,
            y = size.height - (y - minY) * yScale
        )
    }

    // Log the generated points for debugging
    points.forEach { point ->
        Log.d("LineChartPoints", "Point: $point") // Debugging
    }

    // Draw the line connecting the points
    for (i in 0 until points.size - 1) {
        drawLine(
            color = lineColor,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 6f
        )
    }

    // Draw circles at each data point
    points.forEach { point ->
        drawCircle(
            color = pointColor,
            center = point,
            radius = 10f
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
        strokeWidth = 4f
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
                textSize = 40f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)

            }
        )
    }

    // Add labels for the X-axis (date formatted)
    val numberOfLabels = 5 // Number of labels to show on the X-axis
    val interval = (maxX - minX) / numberOfLabels
    for (i in 0..numberOfLabels) {
        val xValue = minX + i * interval
        val xPosition = (xValue - minX) * xScale
        val dateLabel = dateFormat.format(Date(xValue))

        drawContext.canvas.nativeCanvas.drawText(
            dateLabel,
            xPosition,
            size.height + 40f, // Position slightly below the axis
            android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 40f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)

            }
        )
    }
}


// Data class representing a stress entry with a userId, stress level, and count
data class StressEntry2(val userId: String, val stressLevel: String, val count: Int = 0)

/**
 * Reads stress data from a file and aggregates it for a pie chart visualization.
 * @param context The application context used to access the file.
 * @param userId The user ID for filtering the stress data.
 * @return A list of pairs containing stress levels and their respective counts.
 */
fun readStressDataForPieChart(context: Context, userId: String): List<Pair<String, Int>> {
    // File where stress data is stored
    val filename = "stress_history.txt"
    // Mutable map to store counts for each stress level
    val stressCounts = mutableMapOf<String, Int>()

    try {
        // Open the file for reading
        val fileInputStream = context.openFileInput(filename)
        val reader = BufferedReader(InputStreamReader(fileInputStream))

        // Variables to hold current data fields during file parsing
        var currentId: String? = null
        var currentLevel: String? = null

        // Read the file line by line and parse the data
        reader.forEachLine { line ->
            when {
                // Capture the user ID
                line.startsWith("ID: ") -> {
                    currentId = line.substringAfter("ID: ").trim()
                }
                // Capture the stress level
                line.startsWith("Level: ") -> {
                    currentLevel = line.substringAfter("Level: ").trim()
                }
                // When notes are encountered, process the data if it matches the user ID
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
        // Log any errors encountered while reading the file
        Log.e("StressData", "Error reading stress data", e)
    }

    // Convert the map into a list of pairs for easier processing in charts
    return stressCounts.toList()
}



//@Composable
//fun StressPieChart() {
//    val context = LocalContext.current
//    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
//    val userId = sharedPreferences.getString("userId", null)
//    var stressData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
//
//    LaunchedEffect(userId) {
//        if (!userId.isNullOrEmpty()) {
//            stressData = readStressDataForPieChart(context, userId)
//        } else {
//            Log.e("StressPieChart", "Error: User ID is null or empty")
//        }
//    }
//
//    if (stressData.isEmpty()) {
//        Text("No stress data available", color = Color.White,fontSize = 40.sp)
//    } else {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Stress Overview",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.White,
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            DrawStressPieChart(stressData)
//        }
//    }
//}

@Composable
fun DrawStressPieChart(data: List<Pair<String, Int>>) {
    // Calculate the total value of all data points for percentage calculation
    val total = data.sumOf { it.second.toDouble() }.toFloat()
    // Define a list of colors for the pie chart segments
    val colors = listOf(

        Color(0xFF9575CD), // Purple
        Color(0xFF4DD0E1), // Cyan
        Color(0xFFFFD54F), // Amber
        Color(0xFFA1887F), // Brownish Gray
        Color(0xFFBA68C8) , // Lavender
        Color(0xFF64B5F6), // Light Blue
        Color(0xFF81C784), // Green
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFF7043), // Orange
        Color(0xFFEF5350), // Red
    )
    // Canvas to draw the pie chart
    Canvas(
        modifier = Modifier
            .width(350.dp)
            .height(300.dp)
            .padding(start=50.dp)
    ) {
        var startAngle = 0f

        // Loop through the data and draw the pie chart slices
        data.forEachIndexed { index, entry ->
            // Calculate the sweep angle for the current slice
            val sweepAngle = (entry.second / total) * 360f
            // Draw the slice using an arc
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            // Update the starting angle for the next slice
            startAngle += sweepAngle
        }

        // Reset start angle for label placement
        startAngle = 0f
        // Loop through the data again to draw labels on the slices
        data.forEachIndexed { index, entry ->
            val sweepAngle = (entry.second / total) * 360f
            val angle = startAngle + sweepAngle / 2
            val radius = size.minDimension / 3
            // Calculate the position for the label
            val x = center.x + radius * cos(angle * PI / 180).toFloat()
            val y = center.y + radius * sin(angle * PI / 180).toFloat()

            // Draw the label at the calculated position
            drawContext.canvas.nativeCanvas.drawText(
                entry.first,
                x,
                y,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 40f
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            // Update the start angle for the next slice
            startAngle += sweepAngle
        }
    }
}



//
//@Composable
//fun StressPieChart(data: List<Pair<String, Int>>) {
//    if (data.isEmpty()) {
//        Box(
//            modifier = Modifier
//                .width(50.dp)
//                .height(250.dp)
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("No stress data available", color = Color.White)
//        }
//        return
//    }
//
//    val total = data.sumOf { it.second.toDouble() }.toFloat()
//    val colors = listOf(
//        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan
//    )
//
//    Canvas(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp)
//    ) {
//        var startAngle = 0f
//
//        data.forEachIndexed { index, entry ->
//            val sweepAngle = (entry.second / total) * 360f
//            drawArc(
//                color = colors[index % colors.size],
//                startAngle = startAngle,
//                sweepAngle = sweepAngle,
//                useCenter = true
//            )
//            startAngle += sweepAngle
//        }
//
//        // Add labels
//        startAngle = 0f
//        data.forEachIndexed { index, entry ->
//            val sweepAngle = (entry.second / total) * 360f
//            val angle = startAngle + sweepAngle / 2
//            val radius = size.minDimension / 3
//            val x = center.x + radius * cos(angle * PI / 180).toFloat()
//            val y = center.y + radius * sin(angle * PI / 180).toFloat()
//
//            drawContext.canvas.nativeCanvas.drawText(
//                entry.first,
//                x,
//                y,
//                android.graphics.Paint().apply {
//                    color = android.graphics.Color.BLACK
//                    textSize = 30f
//                    textAlign = android.graphics.Paint.Align.CENTER
//                }
//            )
//            startAngle += sweepAngle
//        }
//    }
//}

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
    // Create a BottomNavigation bar with a defined background and content color
    BottomNavigation(
        backgroundColor = colorResource(R.color.lightpurple),
        contentColor = Color.Black,
        modifier = Modifier.padding(bottom = 22.dp)
    ) {
        // Navigation item for the overview screen
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
        // Navigation item for the history screen
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
        // Navigation item for adding a new mood entry
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
        // Navigation item for the stress management screen
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("MedMainPage") },
            icon = {
                Icon(
                    Icons.Default.Medication,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            },
            label = { Text("Med", fontSize = 16.sp) }
        )
        // Navigation item for the advice screen
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
    // Column layout for the entire drawer content
    Column(
        modifier = Modifier
            .fillMaxSize() // Occupies the full available space
            .background(color = colorResource(R.color.lightpurple)) // Background color for the drawer
            .padding(16.dp) // Padding around the content
    ) {
        // Header section of the drawer
        Text(
            text = "Navigation Menu",
            fontSize = 20.sp, // Font size for the header text
            fontWeight = FontWeight.Bold, // Bold font weight for emphasis
            color = Color.Black, // Text color
            modifier = Modifier.padding(top= 20.dp,bottom = 10.dp) // Padding for spacing
        )
        // Divider to separate the header from the navigation items
        Divider(color = Color.Black, thickness = 1.dp)

        // Navigation items with respective icons and click actions
        DrawerItem("overview", Icons.Filled.Home) { navController.navigate("overview_screen") }
        DrawerItem("Advice", Icons.Filled.Info) { navController.navigate("advice_screen") }
        DrawerItem("Mood", Icons.Filled.Mood) { navController.navigate("mood_screen") }
        DrawerItem("Stress Level", Icons.Filled.BatteryFull) { navController.navigate("stress_screen") }
        DrawerItem("Anxiety Level", Icons.Filled.ReportProblem) { navController.navigate("anxiety_screen") }
        DrawerItem("Reminder Activity", Icons.Filled.EventNote) { navController.navigate("reminder_screen") }
        DrawerItem("Logout", Icons.Default.Logout) { logout(navController, context) }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    // Row layout for displaying a single item in the drawer menu
    Row(
        modifier = Modifier
            .fillMaxWidth() // Fills the entire width of the parent
            .clickable { onClick() } // Makes the row clickable with the provided action
            .padding(vertical = 12.dp), // Adds vertical padding for spacing
        verticalAlignment = Alignment.CenterVertically // Centers the content vertically
    ) {
        // Icon displayed alongside the text
        Icon(
            imageVector = icon, // Icon image
            contentDescription = label, // Accessibility description
            tint = Color.White, // Icon color
            modifier = Modifier.size(24.dp) // Icon size
        )
        // Spacer for adding space between the icon and text
        Spacer(modifier = Modifier.width(16.dp))
        // Text label for the drawer item
        Text(
            text = label, // Label text
            fontSize = 16.sp, // Font size for readability
            color = Color.Black, // Text color
            fontWeight = FontWeight.Bold // Bold text for emphasis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnxietyLineChartPreview() {
    // Preview for the AnxietyLineChart component with example data
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