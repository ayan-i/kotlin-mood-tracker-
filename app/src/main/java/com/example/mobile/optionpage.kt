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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.IOException
import java.io.InputStream
import android.util.Log
import androidx.compose.ui.draw.shadow
import java.io.FileInputStream
import androidx.compose.animation.animateContentSize
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.merge
import java.io.BufferedReader
import java.io.InputStreamReader

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
data class StressEntry(val date:String,val stressLevel:String,val Notes:String)
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

fun readStressHistory(context: Context, userId: String): List<StressEntry> {
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
                    StressEntry(formattedDate, level, notes)
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
    stressEntries: List<StressEntry>,
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

private fun timeDiff(date1: String, date2: String, format: SimpleDateFormat): Long {
    val time1 = format.parse(date1)?.time ?: Long.MAX_VALUE
    val time2 = format.parse(date2)?.time ?: 0L
    return kotlin.math.abs(time1 - time2)
}

@Composable
fun Option(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color.Black)
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    // Retrieve userId from shared preferences
    val userId = sharedPreferences.getString("userId", null)
    var moodHistory by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var stressHistory by remember { mutableStateOf<List<StressEntry>>(emptyList()) }
    var anxietyHistory by remember { mutableStateOf<List<AnxietyEntry>>(emptyList()) }
    var combinedHistory by remember { mutableStateOf<List<CombinedEntry>>(emptyList()) }


    //doesn't delay the UI and runs it seperately not in the main thread
    LaunchedEffect(context) {
        if (!userId.isNullOrEmpty()) {
            Log.d("UserId", "Retrieved userId: $userId")
            moodHistory = readMoodHistory(context, userId)
            stressHistory= readStressHistory(context,userId)
            anxietyHistory= readAnxietyHistory(context,userId)
            combinedHistory = mergeEntries(moodHistory, stressHistory, anxietyHistory) // Fix



            Log.d("MoodHistory", "Loaded entries: ${moodHistory.size}")
            Log.d("StressHistory", "Loaded entries: ${stressHistory.size}")
            Log.d("AnxietyHistory", "Loaded entries: ${anxietyHistory.size}")
            Log.d("CombinedHostory", "Loaded entries: ${combinedHistory.size}")


        } else {
            Log.e("MoodHistory", "Error: User ID is null or empty")
        }
    }

    if (moodHistory.isEmpty()) {
        Text("No mood history available", color = Color.White)
    }

    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxSize()
    ) {
        Text(
            text = "History",
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 30.dp, start = 135.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, start = 20.dp, end = 20.dp, bottom = 70.dp)

        ) {
            items(combinedHistory) { entry ->
                MoodHistoryCard(entry)
                Spacer(modifier = Modifier.height(10.dp))

            }
        }

        BottomNavigation(
            backgroundColor = colorResource(R.color.lightpurple),
            contentColor = Color.Black,
            modifier = Modifier
                .padding(top = 820.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {
            BottomNavigationItem(
                selected = false ,
                onClick = {navController.navigate("overview_screen")},
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).padding(top = 10.dp)
                    )
                },
                label = { Text("Overview", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
            BottomNavigationItem(
                selected = false,
                onClick = { navController.navigate("history_screen")},
                icon = {
                    Icon(
                        Icons.Filled.History,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).padding(top = 10.dp),
                    )
                },
                label = { Text("History", fontSize = 16.sp, fontWeight = FontWeight.Medium)}
            )
            BottomNavigationItem(
                selected = false,
                onClick = { navController.navigate("mood_screen") },
                icon = {
                    Icon(
                        Icons.Filled.AddCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).padding(top = 10.dp)
                    )
                }
            )
            BottomNavigationItem(
                selected = false,
                onClick ={ },
                icon = {
                    Icon(
                        Icons.Filled.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).padding(top = 10.dp)
                    )
                },
                label = { Text("Med", fontSize = 16.sp) }
            )
            BottomNavigationItem(
                selected = false,
                onClick = {navController.navigate(route="advice_screen") },
                icon = {
                    Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Help",
                    modifier = Modifier.size(23.dp)
                )
                },
                label = { Text("Support", fontSize = 11.sp) }
            )
        }
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
                Image(
                    painter = painterResource(id = moodEmoji?.MoodEmoji ?: R.drawable.chill),
                    contentDescription = "Mood Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
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
}




