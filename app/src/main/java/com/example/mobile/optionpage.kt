package com.example.mobile


import android.os.Bundle
import android.provider.ContactsContract.Profile
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Card
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.font.createFontFamilyResolver

class optionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Option()
        }
    }
}

//discuss on Thrusday about it (finish that)
//maybe add an edit icon to change whats in that specific entry
//add bottom navigation bar with icons make it clickable
//to go to different links
//for example the add sign would go to mood page
//medication icon link to zainabs page
// setting page to change reminders and what not?


@Composable
fun Option() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    var checked by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Overview") }
    // research into selected .... look at android website

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Text(
            text = "History",
            fontSize = 40.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 50.dp, start = 135.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp, start = 20.dp, end = 30.dp)
                .size(width = 300.dp, height = 115.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sentiment_very_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24), // Load the drawable resource
                    contentDescription = "emotion",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 20.dp)
                )

                Column(
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = "Date 2024", // Date of the entry get that from database
                        color = Color.Black,
                        fontSize = 17.sp,
                    )
                    Text(
                        text = "Angry", // Emotion recorded from the database
                        color = Color.Black, // Should change based on emotion
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Anxiety", // Emotion recorded from the database
                        color = Color.Black, // Should change based on emotion
                        fontSize = 17.sp
                    )
                    Text(
                        text = "stress", // Emotion recorded from the database
                        color = Color.Black, // Should change based on emotion
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
    BottomNavigation(
        backgroundColor = colorResource(R.color.lightpurple),
        contentColor = Color.Black,
        modifier = Modifier.padding(top = 810.dp)
            .fillMaxWidth()
            .height(80.dp)

    ) {
        BottomNavigationItem(
            selected = false,
            onClick = {  },
            icon = { Icon(Icons.Filled.ShowChart, contentDescription = null,
                modifier = Modifier.size(45.dp).padding(top = 10.dp)) },
            label = { Text("Overview") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {  },
            icon = { Icon(Icons.Filled.History, contentDescription = null,
                modifier = Modifier.size(45.dp) .padding(top = 10.dp)) },
            label = { Text("History") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {  },
            icon = { Icon(Icons.Filled.AddCircleOutline, contentDescription = null,
                modifier = Modifier.size(60.dp) .padding(top = 10.dp)) },
        )
        BottomNavigationItem(
            selected = false,
            onClick = {  },
            icon = { Icon(Icons.Filled.Medication, contentDescription = null,
                modifier = Modifier.size(45.dp) .padding(top = 10.dp)) },
            label = { Text("Med") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {  },
            icon = { Icon(Icons.Filled.Person, contentDescription = null,
                modifier = Modifier.size(45.dp) .padding(top = 10.dp)) },
            label = { Text("Profile") }

        )
    }
}



    // do the bottom navigation bar design
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun option2() {
    MobileTheme {
        Option()
    }
}
