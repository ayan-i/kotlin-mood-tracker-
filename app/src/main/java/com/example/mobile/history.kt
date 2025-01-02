package com.example.mobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.IOException
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.abs


class OptionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Option(navController)
        }
    }
}

data class MoodEntry(val date: String, val mood: String)
data class StressEntry21(val date:String,val stressLevel:String,val Notes:String)
data class AnxietyEntry(val date:String,val anxietyLevel:String,val Notes:String)

data class CombinedEntry(
    val date: String,
    val mood: String,
    val stressLevel: String?,
    val stressNotes: String?,
    val anxietyLevel: String?,
    val anxietyNotes: String?
)

//reading internal storage uwe week activity
//also geeks for geeks
fun readMoodHistory(context: Context, userId: String): List<MoodEntry> {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault())

        val inputStream = context.openFileInput("moodSELECT.txt")
        val lines = BufferedReader(InputStreamReader(inputStream))

        val moodEntries = mutableListOf<MoodEntry>()

        lines.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 3 && parts[0] == userId) {
                val storedDate = parts[1].trim()
                val mood = parts[2].trim()
                val date = inputFormat.parse(storedDate)
                val formattedDate =
                    if (date != null) {
                        outputFormat.format(date)
                    }
                    else {
                        storedDate
                    }
                moodEntries.add(MoodEntry(formattedDate, mood))
            }
        }

        moodEntries
    } catch (e: Exception) {
        Log.e("MoodHistory", "Error reading or processing moodSELECT.txt", e)
        emptyList()
    }
}

fun readStressHistory(context: Context, userId: String): List<StressEntry21> {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Original format
        val outputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault()) // Desired format

        val inputStream = context.openFileInput("stress_history.txt")
        val lines = BufferedReader(InputStreamReader(inputStream)).readLines()
        lines.chunked(4).mapNotNull { chunk ->
            if (chunk.size == 4) {
                val id = chunk[0].substringAfter("ID:").trim()
                val stored_date = chunk[1].substringAfter("Date:").trim()
                val level = chunk[2].substringAfter("Level:").trim()
                val notes = chunk[3].substringAfter("Notes:").trim()

                if (id == userId) {
                    val date = inputFormat.parse(stored_date)
                    val formattedDate = if (date != null) outputFormat.format(date) else stored_date
                    StressEntry21(formattedDate, level, notes)
                } else {
                    null
                }
            } else {
                null
            }
        }
    } catch (e: IOException) {
        Log.e("StressHistory", "Error reading stress_history.txt file", e)
        emptyList()
    }
}


fun readAnxietyHistory(context: Context, userId: String): List<AnxietyEntry> {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Original format
        val outputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault()) // Desired format

        val inputStream = context.openFileInput("anxiety_data.txt")
        val lines = BufferedReader(InputStreamReader(inputStream)).readLines()

        lines.chunked(4).mapNotNull { chunk ->
            if (chunk.size == 4) {
                val id = chunk[0].substringAfter("ID:").trim()
                val stored_date = chunk[1].substringAfter("Date:").trim()
                val level = chunk[2].substringAfter("Level:").trim()
                val notes = chunk[3].substringAfter("Notes:").trim()

                if (id == userId) {
                    val date = inputFormat.parse(stored_date)
                    val formattedDate = if (date != null) outputFormat.format(date) else stored_date
                    AnxietyEntry(formattedDate, level, notes)
                } else {
                    null
                }
            } else {
                null
            }
        }
    } catch (e: IOException) {
        Log.e("AnxietyHistory", "Error reading anxiety_data.txt file", e)
        emptyList()
    }
}

//go back to it and rework it
fun mergeEntries(
    moodEntries: List<MoodEntry>,
    stressEntries: List<StressEntry21>,
    anxietyEntries: List<AnxietyEntry>
): List<CombinedEntry> {
    val inputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault())
    val dateOnlyFormat = SimpleDateFormat("EEEE MMM d", Locale.getDefault())

    val moodsByDay = moodEntries.groupBy { inputFormat.parse(it.date)?.let { dateOnlyFormat.format(it) } }
    val stressByDay = stressEntries.groupBy { inputFormat.parse(it.date)?.let { dateOnlyFormat.format(it) } }
    val anxietyByDay = anxietyEntries.groupBy { inputFormat.parse(it.date)?.let { dateOnlyFormat.format(it) } }

    val mergedList = mutableListOf<CombinedEntry>()


    moodsByDay.forEach { (day, moods) ->
        if (day != null) {
            //use of ?: to show if it is null from kotlin website as elvis operator
            val dailyStress = stressByDay[day]?.toMutableList() ?: mutableListOf()
            val dailyAnxiety = anxietyByDay[day]?.toMutableList() ?: mutableListOf()

            moods.forEach { mood ->
                val stress = dailyStress.minByOrNull { timeDiff(it.date, mood.date, inputFormat) }
                val anxiety = dailyAnxiety.minByOrNull { timeDiff(it.date, mood.date, inputFormat) }

                dailyStress.remove(stress)
                dailyAnxiety.remove(anxiety)

                mergedList.add(
                    CombinedEntry(
                        date = mood.date,
                        mood = mood.mood,
                        stressLevel = stress?.stressLevel,
                        stressNotes = stress?.Notes,
                        anxietyLevel = anxiety?.anxietyLevel,
                        anxietyNotes = anxiety?.Notes
                    )
                )
            }
        }
    }

    return mergedList
}

//used to calculate time difference
//use of ?: to show if it is null from kotlin website as elvis operator
//Long.MAX_VALUE means gets the highest value
//0l long value default to 0
private fun timeDiff(date1: String, date2: String, format: SimpleDateFormat): Long {
    val time1 = format.parse(date1)?.time ?: Long.MAX_VALUE
    val time2 = format.parse(date2)?.time ?: 0L
    return abs(time1 - time2)
}

@Composable
fun Option(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color.Black)
    systemUiController.setNavigationBarColor(color = colorResource(R.color.lightpurple))

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Retrieve userId from shared preferences
    val userId = sharedPreferences.getString("userId", null)
    var moodHistory by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var stressHistory by remember { mutableStateOf<List<StressEntry21>>(emptyList()) }
    var anxietyHistory by remember { mutableStateOf<List<AnxietyEntry>>(emptyList()) }
    var combinedHistory by remember { mutableStateOf<List<CombinedEntry>>(emptyList()) }

    // Load data in a non-blocking way
    LaunchedEffect(context) {
        if (!userId.isNullOrEmpty()) {
            Log.d("UserId", "Retrieved userId: $userId")
            moodHistory = readMoodHistory(context, userId)
            stressHistory = readStressHistory(context, userId)
            anxietyHistory = readAnxietyHistory(context, userId)
            combinedHistory = mergeEntries(moodHistory, stressHistory, anxietyHistory)

            Log.d("MoodHistory", "Loaded entries: ${moodHistory.size}")
            Log.d("StressHistory", "Loaded entries: ${stressHistory.size}")
            Log.d("AnxietyHistory", "Loaded entries: ${anxietyHistory.size}")
            Log.d("CombinedHistory", "Loaded entries: ${combinedHistory.size}")
        } else {
            Log.e("MoodHistory", "Error: User ID is null or empty")
        }
    }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent1(navController, context) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("History",fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                    if (combinedHistory.isEmpty()) {
                        Text(
                            text = "No mood history available",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                        ) {
                            items(combinedHistory) { entry ->
                                MoodHistoryCard(entry)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavigationBar1(navController = navController)
            }
        )
    }
}

@Composable
fun BottomNavigationBar1(navController: NavController) {
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
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 10.dp)
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
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 10.dp)
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
                    modifier = Modifier
                        .size(60.dp)
                        .padding(top = 10.dp)
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
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 10.dp)
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
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 10.dp)
                )
            },
            label = { Text("Advice", fontSize = 13.sp) }
        )
    }
}

@Composable
fun DrawerContent1(navController: NavController, context: Context) {
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
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
        )
        Divider(color = Color.Black, thickness = 1.dp)

        DrawerItem1("Overview", Icons.Default.Dashboard) { navController.navigate("overview_screen") }
        DrawerItem1("Advice", Icons.Default.Phone) { navController.navigate("advice_screen") }
        DrawerItem1("Mood", Icons.Default.AddCircle) { navController.navigate("mood_screen") }
        DrawerItem1("Stress Level", Icons.Default.BatteryAlert) { navController.navigate("stress_screen") }
        DrawerItem1("Anxiety Level", Icons.Default.Warning) { navController.navigate("anxiety_screen") }
        DrawerItem1("Logout", Icons.Default.Logout) { logout(navController, context) }
    }
}

@Composable
fun DrawerItem1(label: String, icon: ImageVector, onClick: () -> Unit) {
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



@Composable
fun MoodHistoryCard(entry: CombinedEntry) {
    data class Mood(val id: Int, val Moodname: String, val MoodEmoji: Int, val color: Color)

    val moods = listOf(
        Mood(1, "Joyful", R.drawable.veryhappy, colorResource(R.color.lightblue)),
        Mood(
            2,
            "Happy",
            R.drawable.sentiment_satisfied_24dp_61c52f_fill0_wght400_grad0_opsz24,
            colorResource(R.color.green)
        ),
        Mood(3, "Meh", R.drawable.neutral, colorResource(R.color.yellow)),
        Mood(
            4,
            "Bad",
            R.drawable.sentiment_dissatisfied_24dp_dc602e_fill0_wght400_grad0_opsz24,
            colorResource(R.color.orange)
        ),
        Mood(
            5,
            "Down",
            R.drawable.sentiment_very_dissatisfied_24dp_e73e3e_fill0_wght400_grad0_opsz24,
            colorResource(R.color.red)
        )
    )
    val moodEmoji = moods.find { it.Moodname == entry.mood }
    var expands by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clickable { expands = !expands }
            .animateContentSize(),//makes the animation
        backgroundColor = colorResource(R.color.boxcolor),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                moodEmoji?.let {
                    Image(
                        painter = painterResource(id = it.MoodEmoji),
                        contentDescription = "Mood Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 16.dp)
                    )
                }
                Column {
                    Text(
                        text = entry.date,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = entry.mood,
                        color = moodEmoji?.color ?: Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                }
            }

            //expands only when there is stress or anxiety info
            if (expands) {
                // Displaying stress information, regardless of expands
                entry.stressLevel?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Stress Level: $it",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                entry.stressNotes?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Stress Notes: $it",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

// Displaying anxiety information, regardless of expands
                entry.anxietyLevel?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Anxiety Level: $it",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                entry.anxietyNotes?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Anxiety Notes: $it",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }
        }
    }



//hello1