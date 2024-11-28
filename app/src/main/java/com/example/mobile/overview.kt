package com.example.mobile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.theme.MobileTheme

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
@Composable
fun overview(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val userId = UserSession.userId
    if (userId != null) {
        Text("Welcome back, User ID: $userId!")
    } else {
        Text("Error: No user session found.")
    }
    systemUiController.setStatusBarColor(color = Color.Black)
    systemUiController.setNavigationBarColor(colorResource(R.color.lightpurple))


    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxSize()
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Logout,
            tint = Color.White,
            contentDescription = null,
            modifier = Modifier.size(60.dp).padding(top = 30.dp)
                .clickable {
                    logout(navController, context) }
        )
        Text(
            text = "Overview",
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 30.dp, start = 135.dp)
        )


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
                onClick = { navController.navigate("overview_screen")},
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
                onClick = { navController.navigate(route = "mood_screen")},
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
                onClick = {navController.navigate(route="stress_screen")  },
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
                onClick = {navController.navigate(route="advice_screen")},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Help",
                        modifier = Modifier.size(44.dp).padding(top = 10.dp
                    ))
                },
                label = { Text("Advice", fontSize = 13.sp) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun overview2() {
    MobileTheme {
        val navController = rememberNavController() // Mock navigation controller for preview
        overview(navController = navController)

    }
}



//hello