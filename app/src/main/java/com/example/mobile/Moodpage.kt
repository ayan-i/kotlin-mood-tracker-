package com.example.mobile

import android.os.Bundle
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
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.theme.MobileTheme
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import java.text.DateFormat
import java.util.Calendar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.Composable

class MyAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MoodPage()
        }
    }
}
@Composable
fun MoodPage() {

    var selectedMood by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance().time
    val dateFormat = DateFormat.getDateInstance().format(calendar)
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    //make a function to take in from the database research???
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(16.dp),

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
            modifier = Modifier
                .padding(top = 240.dp, start = 110.dp)

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
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.veryhappy),
                    contentDescription = "Joyful",
                    modifier = Modifier
                        .size(65.dp)
                        .clickable { selectedMood = "Joyful" },
                    // should move on to anxiety page when clicked
                    colorFilter = ColorFilter.tint(colorResource(R.color.lightblue))
                )
                Text(
                    text = "Joyful",
                    color = colorResource(R.color.lightblue),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,

                    )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.sentiment_satisfied_24dp_b89230_fill0_wght400_grad0_opsz24),
                    contentDescription = "Happy",
                    modifier = Modifier
                        .size(65.dp)
                        .clickable { selectedMood = "Happy" },
                    // should move on to anxiety page when clicked
                    colorFilter = ColorFilter.tint(colorResource(R.color.green))
                )
                Text(
                    text = "Happy",
                    color = colorResource(R.color.green),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,

                    )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.neutral),
                    contentDescription = "Meh",
                    modifier = Modifier
                        .size(68.dp)
                        .clickable { selectedMood = "Meh" }
                    // should move on to anxiety page when clicked
                )
                Text(
                    text = "Meh",
                    color = colorResource(R.color.yellow),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.sentiment_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24),
                    contentDescription = "Bad",
                    modifier = Modifier
                        .size(68.dp)
                        .clickable { selectedMood = "Bad" },
                    colorFilter = ColorFilter.tint(colorResource(R.color.orange))
                )
                Text(
                    text = "Bad",
                    color = colorResource(R.color.orange),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.sentiment_very_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24),
                    contentDescription = "Down",
                    modifier = Modifier
                        .size(68.dp)
                        .clickable { selectedMood = "Down" },
                // should move on to anxiety page when clicked
                    colorFilter = ColorFilter.tint(colorResource(R.color.red))
                )
                Text(
                    text = "Down",
                    color = colorResource(R.color.red),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                }

            }
        }

    Button(
        onClick = {},//could go to anxiety page??
        //when they press continue thats when they should store the current
        // selected mood
        //maybe do currentMood=selectedmood and then navcontroller next page??
        //figure out how the database stuff works
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(R.color.darkpurple)
        ),
        modifier = Modifier
            .padding(top = 540.dp,start=120.dp)
            .size(width = 150.dp, height = 40.dp)
    ) {
        Text(text = "Continue", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
    Text(
        text= if (selectedMood.isNotEmpty())"$selectedMood" else "",
        color =  colorResource(R.color.lightpurple),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(top=490.dp, start = 160.dp)
    )
    }





@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMoodPage() {
    MobileTheme {
        MoodPage()
    }
}
