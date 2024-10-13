package com.example.mobile

import android.os.Bundle
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
                NavHost(navController = navController, startDestination = "main_page") {
                    composable("main_page") { MainPage(navController) }
                    composable("login_screen") { LoginScreen(navController) }
                    composable("signup_screen") { SignUp(navController) }
                }
            }
        }
    }
}
