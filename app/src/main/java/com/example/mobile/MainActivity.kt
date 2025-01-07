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

// MainActivity serves as the entry point for the app
class MainActivity : ComponentActivity() {
    // onCreate initializes the app's main components
    override fun onCreate(savedInstanceState: Bundle?) {
        // Enables edge-to-edge display
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // Applies the app's theme
            MobileTheme {
                // Creates a navigation controller for screen navigation
                val navController = rememberNavController()
                // Accesses shared preferences to retrieve user session data
                val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("userId", null)
                // Determines the start screen based on user session
                val startDestination = if (userId != null) "overview_screen" else "main_page"
                // Logs the user ID and start destination for debugging purposes
                Log.d("MainActivity", "Starting app with userId: $userId")
                Log.d("MainActivity", "Start destination: $startDestination")
                // Sets up the navigation host with the determined start destination
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    // Defining the navigation graph with composable destinations
                    // Main page of the app where users land after logging in
                    composable("main_page") { MainPage(navController) }
                    // Login screen for user authentication
                    composable("login_screen") { LoginScreen(navController) }
                    // Sign-up screen for new user registration
                    composable("signup_screen") { SignUp(navController) }
                    // Overview screen providing a summary of user data or app features
                    composable("overview_screen") { overview(navController) }
                    // Mood tracking screen for users to log their mood
                    composable("mood_screen") { Mood(navController) }
                    // History screen to review past mood logs or journal entries
                    composable("history_screen") { Option(navController) }
                    // Advice screen providing support resources and helpful links
                    composable("advice_screen") { HelpLine(navController) }
                    // Reminder screen for managing mental health reminders
                    composable("reminder_screen") { ReminderScreen(navController) }
                    // Stress management screen with coping strategies and tools
                    composable("stress_screen") { StressScreen(navController) }
                    // Anxiety support screen offering resources for anxiety relief
                    composable("anxiety_screen") { AnxietyScreen(navController) }


                }
            }
        }
    }
}
//hello