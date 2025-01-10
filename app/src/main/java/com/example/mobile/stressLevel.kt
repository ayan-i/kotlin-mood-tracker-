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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
 * @param navController Handles navigation between screens.
 */
@Composable
fun StressScreen(navController: NavController) {
    StressLevelContent(
        navController = navController,
        onSubmit = { level, notes ->
            saveStressData(level, notes, navController) // Save stress data on form submission
        }
    )
}

/**
 * StressLevelContent is the UI for the Stress Check-In screen.
 * @param navController Handles navigation actions.
 * @param onSubmit Callback for submitting the stress level and notes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StressLevelContent(
    navController: NavController,
    onSubmit: (String, String) -> Unit
) {
    // State to track the entered notes
    var currentFeelingNotes by remember { mutableStateOf("") }

    // State to track the selected stress level
    var currentSelectedStressLevel by remember { mutableStateOf("Not Stressed") }

    // Scroll state for the vertical layout
    val scrollState = rememberScrollState()

    // State for displaying snackbars
    val snackbarHostState = remember { SnackbarHostState() }

    // Coroutine scope for handling asynchronous operations
    val coroutineScope = rememberCoroutineScope()

    // System UI Controller for customising the system bar appearance
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent) // Set system bars to transparent

    // Focus manager for clearing focus when clicking outside inputs
    val focusManager = LocalFocusManager.current

    // Main UI container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .clickable { focusManager.clearFocus() } // Clear focus when the background is clicked
    ) {
        // Scrollable column for the content
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            androidx.compose.material.Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(45.dp)
                    .padding(top =15.dp, start = 3.dp)
                    .clickable {
                        navController.navigate(route = "overview_screen")
                    },
                tint = Color.White
            )
            // Title
            Text(
                text = "Stress Check-In",
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle for selecting stress level
            Text(
                text = "Select your stress level:",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List of stress levels for selection
            val stressLevels = listOf(
                "Overwhelmed", "Worried", "Uneasy", "Irritated",
                "Distressed", "Tense", "Burned Out", "Calm",
                "Content", "Stressed"
            )

            // Radio buttons for each stress level
            stressLevels.forEach { level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { currentSelectedStressLevel = level }, // Update selected level
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

                    // Label for the radio button
                    Text(
                        text = level,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notes section title
            Text(
                text = "Feeling Notes",
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text field for entering notes
            TextField(
                value = currentFeelingNotes,
                onValueChange = { currentFeelingNotes = it }, // Update notes state
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

            // Submit button
            // Submit button
            Button(
                onClick = {
                    if (currentSelectedStressLevel.isEmpty()) { // Check if a stress level is selected
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please select a stress level before continuing.")
                        }
                    } else {
                        onSubmit(currentSelectedStressLevel, currentFeelingNotes) // Submit stress level with optional notes
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Data submitted successfully!") // Show confirmation
                        }
                        focusManager.clearFocus() // Clear focus after submission

                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }



        }

        // Snackbar host for displaying messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        )
    }
}

/**
 * Saves stress level data locally.
 * @param level The stress level selected by the user.
 * @param notes Notes entered by the user.
 * @param navController NavController for navigation.
 */
fun saveStressData(level: String, notes: String, navController: NavController) {
    val context = navController.context // Get the context from the NavController
    val filename = "stress_history.txt" // File name for saving data
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null) // Retrieve the user ID from shared preferences

    if (userId.isNullOrEmpty()) {
        Log.e("SaveStressDataError", "User ID is missing. Cannot save stress data.") // Log an error if user ID is missing
        return
    }

    val currentTime = System.currentTimeMillis() // Get the current time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Format the date and time
    val formattedDate = dateFormat.format(Date(currentTime)) // Convert timestamp to readable date
    val fileContent = """
        ID: $userId
        Date: $formattedDate
        Level: $level
        Notes: $notes
    """.trimIndent() // Create the file content

    try {
        context.openFileOutput(filename, Context.MODE_APPEND).use { output ->
            output.write("$fileContent\n".toByteArray()) // Append the data to the file
        }
        Log.d("SaveStressData", "Data saved successfully for user ID: $userId") // Log success
    } catch (e: IOException) {
        Log.e("SaveStressDataError", "Failed to save stress data to file.", e) // Log any file errors
    }
}

/**
 * Preview function for StressLevelContent.
 * Used to display a preview in Android Studio's design tools.
 */
@Preview(showBackground = true, name = "StressLevelPreview")
@Composable
fun StressLevelPreview() {
    MobileTheme {
        StressLevelContent(
            navController = rememberNavController(),
            onSubmit = { _, _ -> } // Empty lambda for preview purposes
        )
    }
}
