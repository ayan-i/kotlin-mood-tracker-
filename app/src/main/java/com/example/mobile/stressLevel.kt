package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.theme.MobileTheme
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class StressLevel : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileTheme {
                val navController = rememberNavController() // Create a NavController instance
                StressScreen(navController = navController) // Pass the NavController instance here
            }
        }
    }
}

/**
 * StressScreen composable integrates StressLevelContent with navigation.
 */
@Composable
fun StressScreen(navController: NavController) {
    StressLevelContent(
        navController = navController,
        onSubmit = { level, notes ->
            saveStressData(level, notes, navController)
        }
    )
}

/**
 * StressLevelContent is the UI for the Stress Check-In screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StressLevelContent(
    navController: NavController,
    onSubmit: (String, String) -> Unit
) {
    var currentFeelingNotes by remember { mutableStateOf("") }
    var currentSelectedStressLevel by remember { mutableStateOf("Not Stressed") }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .clickable { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            Text(
                text = "Stress Check-In",
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select your stress level:",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val stressLevels = listOf(
                "Overwhelmed", "Worried", "Uneasy", "Irritated",
                "Distressed", "Tense", "Burned Out", "Calm",
                "Content", "Stressed"
            )

            stressLevels.forEach { level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { currentSelectedStressLevel = level },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (currentSelectedStressLevel == level),
                        onClick = { currentSelectedStressLevel = level },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = level,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Feeling Notes",
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = currentFeelingNotes,
                onValueChange = { currentFeelingNotes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.Black),
                placeholder = { Text("Enter your notes here", color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSubmit(currentSelectedStressLevel, currentFeelingNotes)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Data submitted successfully!")
                    }
                    focusManager.clearFocus()
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        )
    }
}
//hello
/**
 * Saves stress level data locally.
 */
fun saveStressData(level: String, notes: String, navController: NavController) {
    val context = navController.context
    val filename = "stress_history.txt"
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)

    if (userId.isNullOrEmpty()) {
        Log.e("SaveStressDataError", "User ID is missing. Cannot save stress data.")
        return
    }

    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(currentTime))
    val fileContent = """
        ID: $userId
        Date: $formattedDate
        Level: $level
        Notes: $notes
    """.trimIndent()

    try {
        context.openFileOutput(filename, Context.MODE_APPEND).use { output ->
            output.write("$fileContent\n".toByteArray())
        }
        Log.d("SaveStressData", "Data saved successfully for user ID: $userId")
    } catch (e: IOException) {
        Log.e("SaveStressDataError", "Failed to save stress data to file.", e)
    }
}

/**
 * Preview function for StressLevelContent.
 */
@Preview(showBackground = true, name = "StressLevelPreview")
@Composable
fun StressLevelPreview() {
    MobileTheme {
        StressLevelContent(
            navController = rememberNavController(),
            onSubmit = { _, _ -> }
        )
    }
}

