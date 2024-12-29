package com.example.mobile

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.theme.MobileTheme
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.round

class overviewActivity : ComponentActivity() {
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
    val userId = UserSession.userId
    systemUiController.setStatusBarColor(color = Color.Black)
    systemUiController.setNavigationBarColor(color= colorResource(R.color.lightpurple))

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

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
                    modifier = Modifier.padding(top = 25.dp)
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)
                        .padding(padding)
                ) {
                    // Main Content of the Screen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f) // Ensures content takes available space above the bottom bar
                            .background(color = Color.Black)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            MoodGraph()
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                )
            }
        )
    }
}

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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun overview2() {
    MobileTheme {
        val navController = rememberNavController()
        overview(navController = navController)

    }
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

//hello