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
class OptionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Option()
        }
    }
}

data class MoodEntry(val date: String, val mood: String)


fun readMoodHistory(context: Context): List<MoodEntry> {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault())

        val fileInputStream: FileInputStream = context.openFileInput("mood1.txt")
        val lines = fileInputStream.bufferedReader().readLines()

        lines.mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size == 2) {
                val stored_date = parts[0].trim()
                val mood = parts[1].trim()
                val date = inputFormat.parse(stored_date)
                val formattedDate = if (date != null) outputFormat.format(date) else stored_date

                MoodEntry(formattedDate, mood)
            } else {
                null
            }
        }
    } catch (e: IOException) {
        Log.e("MoodHistory", "Error reading mood1.txt file", e)
        emptyList()
    }
}



@Composable
fun Option() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color.Black)
    systemUiController.setNavigationBarColor(colorResource(R.color.lightpurple))

    val context = LocalContext.current
    var moodHistory by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }

    //doesn't delay the UI and runs it seperately not in the main thread
    LaunchedEffect(context) {
        moodHistory = readMoodHistory(context)
        Log.d("MoodHistory", "Loaded entries: ${moodHistory.size}")

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
            items(moodHistory) { entry ->
                MoodHistoryCard(entry)
                Spacer(modifier = Modifier.height(10.dp))

            }
        }


        // BottomNavigation
        BottomNavigation(
            backgroundColor = colorResource(R.color.lightpurple),
            contentColor = Color.Black,
            modifier = Modifier
                .padding(top = 790.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {
            BottomNavigationItem(
                selected = false ,
                onClick = {},
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
                onClick = { },
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
                onClick = { /* Handle navigation */ },
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
                onClick = { /* Handle navigation */ },
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
                onClick = { /* Handle navigation */ },
                icon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).padding(top = 10.dp)
                    )
                },
                label = { Text("Profile", fontSize = 16.sp) }
            )
        }
    }
}

@Composable
fun MoodHistoryCard(entry: MoodEntry) {
    data class Mood(val id: Int,val Moodname: String, val MoodEmoji: Int,val color:Color)
//    val formattedDate = formatDateWithDayOfWeek(entry.date)
    var expands by remember { mutableStateOf(false) }

    val moods = listOf(
        Mood(1,"Joyful", R.drawable.veryhappy,colorResource(R.color.lightblue)),
        Mood(2,"Happy", R.drawable.sentiment_satisfied_24dp_61c52f_fill0_wght400_grad0_opsz24,colorResource(R.color.green)),
        Mood(3,"Meh", R.drawable.neutral,colorResource(R.color.yellow)),
        Mood(4,"Bad", R.drawable.sentiment_dissatisfied_24dp_dc602e_fill0_wght400_grad0_opsz24,colorResource(R.color.orange)),
        Mood(5,"Down", R.drawable.sentiment_very_dissatisfied_24dp_e73e3e_fill0_wght400_grad0_opsz24,colorResource(R.color.red))
    )
    val moodEmoji = moods.find { it.Moodname == entry.mood }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(elevation = 20.dp) // use this for the light mode
            .wrapContentHeight()
            .clickable { expands = !expands }
            .animateContentSize(),
        backgroundColor = colorResource(R.color.boxcolor),
        shape = RoundedCornerShape(25.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start=10.dp,top=5.dp)
        ) {

            Image(
                painter = painterResource(id = moodEmoji?.MoodEmoji ?: R.drawable.chill),
                contentDescription = "emotion",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 20.dp)
            )

            Column {
                Text(
                    text = entry.date,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.mood,
                    color = moodEmoji?.color ?: Color.Black,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                if (expands) {
                    Text(
                        text = "Stress:",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Anxiety:",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Feeling notes:",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }


        }
        }
    }



