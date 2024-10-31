//package com.example.mobile
//
//
//import android.os.Bundle
//import android.provider.ContactsContract.Profile
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonDefaults
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Card
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.material.BottomNavigation
//import androidx.compose.material.BottomNavigationItem
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.CheckboxDefaults
//import androidx.compose.material.Icon
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.AddCircleOutline
//import androidx.compose.material.icons.filled.History
//import androidx.compose.material.icons.filled.Medication
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.ShowChart
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.navigation.compose.rememberNavController
//import com.example.mobile.ui.theme.MobileTheme
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import java.text.DateFormat
//import java.util.Calendar
//import com.google.accompanist.systemuicontroller.rememberSystemUiController
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.text.font.createFontFamilyResolver
//import java.io.FileInputStream
//import java.io.FileOutputStream
//import android.widget.Toast
//import java.io.IOException
//
//class optionActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        enableEdgeToEdge()
//        super.onCreate(savedInstanceState)
//        setContent {
//            Option()
//        }
//    }
//}
//
////to go to different links
////for example the add sign would go to mood page
////medication icon link to zainabs page
//// setting page to change reminders and what not?
////BOTTOM Navigation Bar done
////Need to generate history by doing the database
//// mood & anxiety from database work on login database table and emoji table
//
//
//@Composable
//fun readMoodHistory(context: Context): List<MoodEntry> {
//    val moodHistory = mutableListOf<MoodEntry>()
//    try {
//        val file = File(context.filesDir, "Mood.txt")
//        file.forEachLine { line ->
//            val parts = line.split(",")  // Assuming date and mood are separated by a comma
//            if (parts.size == 2) {
//                moodHistory.add(MoodEntry(parts[0], parts[1].trim()))
//            }
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//    return moodHistory
//}
//data class MoodEntry(val date: String, val mood: String)
//
//@Composable
//fun Option() {
//    val systemUiController = rememberSystemUiController()
//    systemUiController.setSystemBarsColor(color = Color.Transparent)
//    val context = LocalContext.current
//    val moodHistory = remember { readMoodHistory(context) }//reads moodshistory from file
//    data class MoodEntry(val date: String, val mood: String)
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = Color.Black)
//    ) {
//        Text(
//            text = "History",
//            fontSize = 40.sp,
//            color = Color.White,
//            modifier = Modifier.padding(top = 50.dp, start = 135.dp)
//        )
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 120.dp, start = 20.dp, end = 30.dp)
//        ) {
//            items(moodHistory) { entry ->
//                MoodHistoryCard(entry)
//            }
//        }
//    }
//
//    // BottomNavigation
//    BottomNavigation(
//        backgroundColor = colorResource(R.color.lightpurple),
//        contentColor = Color.Black,
//        modifier = Modifier
//            .padding(top = 810.dp)
//            .fillMaxWidth()
//            .height(100.dp)
//    ) {
//        BottomNavigationItem(
//            selected = false,
//            onClick = { /* Handle navigation */ },
//            icon = { Icon(Icons.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(50.dp).padding(top = 10.dp)) },
//            label = { Text("Overview", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
//        )
//        BottomNavigationItem(
//            selected = false,
//            onClick = { /* Handle navigation */ },
//            icon = { Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(50.dp).padding(top = 10.dp)) },
//            label = { Text("History", fontSize = 16.sp) }
//        )
//        BottomNavigationItem(
//            selected = false,
//            onClick = { /* Handle navigation */ },
//            icon = { Icon(Icons.Filled.AddCircleOutline, contentDescription = null, modifier = Modifier.size(60.dp).padding(top = 10.dp)) }
//        )
//        BottomNavigationItem(
//            selected = false,
//            onClick = { /* Handle navigation */ },
//            icon = { Icon(Icons.Filled.Medication, contentDescription = null, modifier = Modifier.size(50.dp).padding(top = 10.dp)) },
//            label = { Text("Med", fontSize = 16.sp) }
//        )
//        BottomNavigationItem(
//            selected = false,
//            onClick = { /* Handle navigation */ },
//            icon = { Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(50.dp).padding(top = 10.dp)) },
//            label = { Text("Profile", fontSize = 16.sp) }
//        )
//    }
//}
//
//@Composable
//fun MoodHistoryCard(entry: MoodEntry) {
//    val moodEmoji = getEmojiByMoodName(entry.mood)
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//            .size(width = 300.dp, height = 115.dp)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Image(
//                painter = painterResource(moodEmoji),
//                contentDescription = "emotion",
//                modifier = Modifier
//                    .size(80.dp)
//                    .padding(end = 20.dp)
//            )
//
//            Column {
//                Text(text = entry.date, color = Color.Black, fontSize = 17.sp)
//                Text(text = entry.mood, color = Color.Black, fontSize = 17.sp)
//                Text(text = "Anxiety", color = Color.Black, fontSize = 17.sp)
//                Text(text = "Stress", color = Color.Black, fontSize = 17.sp)
//            }
//        }
//    }
//}
//
//
//
//fun getEmojiByMoodName(moodName: String): Int {
//    return when (moodName) {
//        "Joyful" -> R.drawable.veryhappy
//        "Happy" -> R.drawable.sentiment_satisfied_24dp_b89230_fill0_wght400_grad0_opsz24
//        "Meh" -> R.drawable.neutral
//        "Bad" -> R.drawable.sentiment_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24
//        "Down" -> R.drawable.sentiment_very_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24
//    }
//}
//
//
//
////fun getEmojiByMoodName(moodName: String): Int {
////    return when (moodName) {
////        "Joyful" -> R.drawable.veryhappy,colorResource(R.color.lightblue)),
////        "Happy" -> R.drawable.sentiment_satisfied_24dp_b89230_fill0_wght400_grad0_opsz24,,colorResource(R.color.green)),
////        "Meh" -> R.drawable.neutral,
////        "Bad" -> R.drawable.sentiment_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24,colorResource(R.color.orange)),
////        "Down" -> R.drawable.sentiment_very_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24,,colorResource(R.color.red)),
////    }
////}
//    // do the bottom navigation bar design
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun option2() {
//    MobileTheme {
//        Option()
//    }
//}
