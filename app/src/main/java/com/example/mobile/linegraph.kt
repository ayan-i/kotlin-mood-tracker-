package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class Linegraph : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController() // Initialize the NavController
            val userID = getUserIdFromPreferences(this) // Dynamically fetch userID
            val data = readAnxietyData(this, userID) // Read the anxiety data from the file
            AnxietyLineChart(data) // Pass the data to the LineChart screen
        }
    }
}

/**
 * Function to get the user ID from shared preferences or a similar persistent storage.
 */
fun getUserIdFromPreferences(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userID", "") ?: ""
}

/**
 * Function to read anxiety data from the file and return it as a list of pairs (timestamp, anxietyLevel).
 * Filters data based on the given userID.
 */
fun readAnxietyData(context: Context, userID: String): List<Pair<Long, Int>> {
    val filename = "anxiety_data.txt"
    val dataList = mutableListOf<Pair<Long, Int>>()

    try {
        val fileInputStream = context.openFileInput(filename)
        val reader = BufferedReader(InputStreamReader(fileInputStream))
        var line: String?
        var currentId: String? = null
        var currentDate: String? = null
        var currentLevel: String? = null

        while (reader.readLine().also { line = it } != null) {
            line?.let {
                when {
                    it.startsWith("ID: ") -> currentId = it.substringAfter("ID: ").trim()
                    it.startsWith("Date: ") -> currentDate = it.substringAfter("Date: ").trim()
                    it.startsWith("Level: ") -> currentLevel = it.substringAfter("Level: ").trim()
                    it.startsWith("Notes: ") -> {
                        // Process the entry when Notes are encountered
                        if (currentId == userID && currentDate != null && currentLevel != null) {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val timestamp = dateFormat.parse(currentDate)?.time
                            val level = currentLevel?.let { convertAnxietyLevelToNumber(it) }

                            if (timestamp != null && level != null) {
                                dataList.add(timestamp to level)
                            }
                        }

                        // Reset for the next entry
                        currentId = null
                        currentDate = null
                        currentLevel = null
                    }
                }
            }
        }
        reader.close()
    } catch (e: Exception) {
        Log.e("ReadDataError", "Error reading anxiety data", e)
    }

    return dataList.reversed() // Reverse the list to show the newest entries first
}

/**
 * Helper function to map anxiety levels to numeric values.
 */
fun convertAnxietyLevelToNumber(level: String): Int? {
    val levels = listOf(
        "Not Anxious",            // 9
        "Very Bad",               // 8
        "Bad",                    // 7
        "Anxiety Comes and Goes", // 6
        "Mild Anxiety",           // 5
        "Anxiety Triggered",      // 4
        "Anxious but Survive",    // 3
        "Constant Fidgeting",     // 2
        "Anxiety Attack",         // 1
        "Panic Attack"            // 0
    )
    return levels.indexOf(level).takeIf { it != -1 }
}

/**
 * Draws a line chart based on anxiety data with labeled axes.
 */
@Composable
fun AnxietyLineChart(data: List<Pair<Long, Int>>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(text = "No data available", color = Color.Gray)
        }
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
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

    // Transform data points to canvas coordinates
    val points = data.map { (x, y) ->
        androidx.compose.ui.geometry.Offset(
            x = (x - minX) * xScale,
            y = size.height - (y - minY) * yScale
        )
    }

    // Draw lines between points
    for (i in 0 until points.size - 1) {
        drawLine(
            color = lineColor,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 4f
        )
    }

    // Draw points
    points.forEach { point ->
        drawCircle(
            color = pointColor,
            center = point,
            radius = 6f
        )
    }

    // Draw Y-axis
    drawLine(
        color = Color.Gray,
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(0f, size.height),
        strokeWidth = 2f
    )

    // Draw X-axis
    drawLine(
        color = Color.Gray,
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
                color = android.graphics.Color.GRAY
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
                color = android.graphics.Color.GRAY
                textSize = 30f
            }
        )
    }
}

/**
 * Preview function with properly structured dummy data.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAnxietyLineChart() {
    val dummyData = listOf(
        "ID: exampleUserID\nDate: 2024-12-25 17:12:46\nLevel: Not Anxious\nNotes: I was happy",
        "ID: exampleUserID\nDate: 2024-12-24 15:01:41\nLevel: Anxiety Attack\nNotes: I feel anxious",
        "ID: exampleUserID\nDate: 2024-12-23 12:30:00\nLevel: Constant Fidgeting\nNotes: Couldn't sit still",
        "ID: exampleUserID\nDate: 2024-12-22 08:45:22\nLevel: Panic Attack\nNotes: Had a bad episode"
    )

    val parsedData = dummyData.mapNotNull { entry ->
        val dateRegex = Regex("Date: (.+)")
        val levelRegex = Regex("Level: (.+)")

        val dateMatch = dateRegex.find(entry)?.groupValues?.get(1)
        val levelMatch = levelRegex.find(entry)?.groupValues?.get(1)

        if (dateMatch != null && levelMatch != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val timestamp = dateFormat.parse(dateMatch)?.time
            val level = convertAnxietyLevelToNumber(levelMatch)

            if (timestamp != null && level != null) {
                timestamp to level
            } else null
        } else null
    }

    AnxietyLineChart(data = parsedData)
}
