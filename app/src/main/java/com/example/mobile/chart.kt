import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.round

class Chart : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
                MoodGraph()
            }
        }
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
                    color = Color.Black.toArgb()  // Use Color.Black directly and convert to ARGB
                    textAlign = android.graphics.Paint.Align.CENTER  // Correct text alignment
                    textSize = 12.sp.toPx()  // Set text size, make sure to convert it to pixels
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
                                    .background(color = Color.Gray)
                            )
                            Text(
                                modifier = Modifier.padding(bottom = 3.dp),
                                text = xAxisScaleData[index],
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.Black
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

    LaunchedEffect(context) {
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

        val normalizedMoodHistory = allMoods.map { mood ->
            moodHistory.find { it.mood == mood } ?: MoodEntry1(userId ?: "", mood, 0)
        }
        val maxCount = normalizedMoodHistory.maxOfOrNull { it.count } ?: 0
        val yaxisLabels = (0..maxCount).toList()

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarGraph(
                //gets the moodHistory data and retrieves the count of each mood
                // divide by max count
                graphBarData =  moodHistory.map { it.count.toFloat() / maxCount },
                xAxisScaleData = allMoods,
                bar = normalizedMoodHistory.map { it.count },
                height = 300.dp,
                roundType = BarType.TOP_CURVED,
                barWidth = 40.dp,
                barColor = barColors,
                barArrangement = Arrangement.SpaceEvenly
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    val moodHistory = listOf(
        MoodEntry1(userId = "1", mood = "Joyful", count = 5),
        MoodEntry1(userId = "1", mood = "Happy", count = 8),
        MoodEntry1(userId = "1", mood = "Meh", count = 2),
        MoodEntry1(userId = "1", mood = "Bad", count = 0),
        MoodEntry1(userId = "1", mood = "Down", count = 3)
    )

    val barColors = listOf(
        Color(0xFF64B5F6),
        Color(0xFF81C784),
        Color(0xFFFFEB3B),
        Color(0xFFFF7043),
        Color(0xFFEF5350)
    )

    val allMoods = listOf("Joyful", "Happy", "Meh", "Bad", "Down")
    val maxCount = moodHistory.maxOfOrNull { it.count } ?: 1
    val yaxisLabels = (0..maxCount).toList()

    moodHistory.forEach { moodEntry ->
        Log.d("BarChartPreview", "Mood: ${moodEntry.mood}, Count: ${moodEntry.count}")
    }


    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BarGraph(
            graphBarData =  moodHistory.map { it.count.toFloat() / maxCount },
            xAxisScaleData = allMoods,
            bar = moodHistory.map { it.count },
            height = 300.dp,
            roundType = BarType.TOP_CURVED,
            barWidth = 40.dp,
            barColor = barColors,
            barArrangement = Arrangement.SpaceEvenly
        )
    }
}
//hello

//make a function to read from the anxiety file and create it as a list in the same function
//make  function to make the graph
// make a preview function to test out dummy data in the graph
