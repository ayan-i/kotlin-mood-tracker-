package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile.ui.theme.MobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MobileTheme {
                val navController = rememberNavController()
                val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("userId", null)

                val startDestination = if (userId != null) "overview_screen" else "main_page"
                Log.d("MainActivity", "Starting app with userId: $userId")
                Log.d("MainActivity", "Start destination: $startDestination")
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("main_page") { MainPage(navController) }
                    composable("login_screen") { LoginScreen(navController) }
                    composable("signup_screen") { SignUp(navController) }
                    composable("overview_screen") { overview(navController) }
                    composable("mood_screen") { Mood(navController) }
                    composable("history_screen") { Option(navController) }
                    composable("advice_screen") { HelpLine(navController) }
                    composable("reminder_screen") { ReminderScreen(navController) }
                    composable("stress_screen") { StressScreen(navController) }
                    composable("anxiety_screen") { AnxietyScreen(navController) }


                }
            }
        }
    }
}
//hello