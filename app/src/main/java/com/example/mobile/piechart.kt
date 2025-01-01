package com.example.mobile
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Piechart : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController() // Initialize the NavController
            val userID = com.example.mobile.getUserIdFromPreferences(this)

            // Dynamically fetch userID
            val data = readStressData(this, userID) // Read the stress data from the file
            StressPieChart(data) // Pass the data to the PieChart screen
        }
    }
}

/**
 * Function to get the user ID from shared preferences or a similar persistent storage.
 */
fun getUserIdForPreferences(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userID", "") ?: ""
}


/**
 * Function to read stress data from the file and return it as a map of stress levels to counts.
 * Filters data based on the given userID.
 */
data class StressEntry(val userId: String, val stressLevel: String, val count: Int = 0)

fun readStressData(context: Context, userId: String): List<StressEntry> {
    return try {
        val inputStream = context.openFileInput("stress_history.txt")
        val lines = BufferedReader(InputStreamReader(inputStream))

        val stressCounts = mutableMapOf<String, Int>()

        lines.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 3 && parts[0] == userId) {
                val level = parts[2].trim()
                val count = stressCounts[level] ?: 0
                stressCounts[level] = count + 1
            }
        }

        stressCounts.map { (level, count) ->
            StressEntry(userId, level, count)
        }
    } catch (e: Exception) {
        Log.e("StressHistory", "Error reading or processing stress_history.txt", e)
        emptyList()
    }
}


/**
 * Draws a pie chart based on stress level data.
 */
@Composable
fun StressPieChart(data: List<StressEntry>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No stress data available", color = Color.Gray)
        }
        return
    }

    val total = data.sumOf { it.count.toDouble() }.toFloat()
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan,
        Color.Gray, Color.LightGray, Color(0xFFFFC0CB), // Pink
        Color(0xFFFFA500)  // Orange
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        var startAngle = 0f
        data.forEachIndexed { index, entry ->
            val sweepAngle = (entry.count / total) * 360f
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
            val sweepAngle = (entry.count / total) * 360f
            val angle = startAngle + sweepAngle / 2
            val radius = size.minDimension / 3
            val x = center.x + radius * cos(angle * PI / 180).toFloat()
            val y = center.y + radius * sin(angle * PI / 180).toFloat()

            drawContext.canvas.nativeCanvas.drawText(
                entry.stressLevel,
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
fun StressOverview(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val stressData = remember { mutableStateListOf<StressEntry>() } // Correct usage of stateful list

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            stressData.clear() // Clear existing data
            stressData.addAll(readStressData(context, userId)) // Add new data to the list
        } else {
            Log.e("StressOverview", "Error: User ID is null or empty")
        }
    }

    if (stressData.isEmpty()) {
        Text("No stress data available", color = Color.White)
    } else {
        Text(
            text = "Stress Overview",
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
            StressPieChart(data = stressData)
        }
    }
}


/**
 * Preview function with properly structured dummy data.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewStressPieChart() {
    val dummyData = listOf(
        StressEntry("1", "Overwhelmed", 5),
        StressEntry("1", "Calm", 10),
        StressEntry("1", "Content", 7)
    )

    StressPieChart(data = dummyData)
}
