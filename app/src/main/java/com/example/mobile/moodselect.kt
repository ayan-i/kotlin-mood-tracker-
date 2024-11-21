package com.example.mobile


import android.content.Context
import android.os.Bundle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.ui.theme.MobileTheme
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.Alignment
import java.text.DateFormat
import java.util.Calendar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.Composable
import java.io.FileInputStream
import java.io.FileOutputStream
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Mood(navController)
        }
    }
}
@Composable
fun Mood(navController: NavController) {

    data class Mood(val id: Int,val Moodname: String, val MoodEmoji: Int,val color:Color)
    var selectedMood by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val calendar = Calendar.getInstance().time
    val dateFormat = DateFormat.getDateInstance().format(calendar)
    val dateFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentDateTime = dateFormat2.format(Date())
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)

    val moods = listOf(
        Mood(1,"Joyful", R.drawable.veryhappy,colorResource(R.color.lightblue)),
        Mood(2,"Happy", R.drawable.sentiment_satisfied_24dp_b89230_fill0_wght400_grad0_opsz24,colorResource(R.color.green)),
        Mood(3,"Meh", R.drawable.neutral,colorResource(R.color.yellow)),
        Mood(4,"Bad", R.drawable.sentiment_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24,colorResource(R.color.orange)),
        Mood(5,"Down", R.drawable.sentiment_very_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24,colorResource(R.color.red))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "How are you today?",
            color = Color.White,
            modifier = Modifier.padding(top = 170.dp, start = 10.dp),
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Rounded.CalendarToday,
            tint = colorResource(R.color.darkpurple),
            contentDescription = "currentCalender",
            modifier = Modifier.padding(top = 240.dp, start = 110.dp)
        )
        Text(
            text = dateFormat,
            modifier = Modifier.padding(top = 242.dp, start = 140.dp),
            fontSize = 19.sp,
            color = colorResource(R.color.lightpurple),
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 350.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moods.forEach { mood ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(mood.MoodEmoji),
                        contentDescription = mood.Moodname,
                        modifier = Modifier
                            .size(65.dp)
                            .clickable { selectedMood = mood.Moodname },
                                colorFilter = ColorFilter.tint(mood.color)
                    )
                    Text(
                        text = mood.Moodname,
                        color = mood.color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Button(
            onClick = {try {
                val fos: FileOutputStream =
                    context.openFileOutput("moodSELECT.txt", Context.MODE_APPEND)
                val entry = "$userId,$currentDateTime,$selectedMood\n"
                fos.write(entry .toByteArray())
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
                Toast.makeText(context, "Data saved successfully..", Toast.LENGTH_SHORT).show()

            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.darkpurple)
            ),
            modifier = Modifier
                .padding(top = 520.dp, start = 120.dp)
                .size(width = 150.dp, height = 40.dp)
        ) {
            Text(
                text = "Continue",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = if (selectedMood.isNotEmpty()) "$selectedMood" else "",
            color = colorResource(R.color.lightpurple),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 470.dp, start = 160.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMoodPage2() {
    MobileTheme {
        val navController = rememberNavController() // Mock navigation controller for preview
        Mood(navController = navController)

    }
}

