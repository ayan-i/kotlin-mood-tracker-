package com.example.mobile

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnxietyScreen(navController: NavController) {
    // State to hold the current feeling notes entered by the user
    var currentFeelingNotes by remember { mutableStateOf("") }

    // State to track the currently selected anxiety level
    var currentSelectedAnxietyLevel by remember { mutableStateOf("Anxiety Triggered") }

    // State to control whether the description dialog is shown
    var showDescription by remember { mutableStateOf(false) }

    // State to hold the description of the selected anxiety level
    var selectedDescription by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current // Focus manager for handling UI focus
    val snackbarHostState = remember { SnackbarHostState() } // State for showing snackbars
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for async actions
    val scrollState = rememberScrollState() // Scroll state for the vertical scroll view
    val systemUiController = rememberSystemUiController() // System UI controller for styling

    // Set navigation bar color
    systemUiController.setNavigationBarColor(color = Color.Black)

    // Colours for the intensity levels, corresponding to each button
    val intensityColors = listOf(
        Color(0xFFFFC1C1), // Light Pastel Red
        Color(0xFFFFD6A5), // Light Pastel Orange
        Color(0xFFFFE7A9), // Light Pastel Yellow
        Color(0xFFDAF7A6), // Soft Green
        Color(0xFFA4F1D1), // Mint Green
        Color(0xFFA9DEF9), // Sky Blue
        Color(0xFFCCAFFC), // Lavender
        Color(0xFFFFB5E8), // Light Pink
        Color(0xFFFFA6C9), // Pastel Coral
        Color(0xFFD5F4E6)  // Soft Teal
    )

    // Texts for the anxiety intensity levels
    val intensityTexts = listOf(
        "Panic Attack", "Anxiety Attack", "Constant Fidgeting",
        "Anxious but Survive", "Anxiety Triggered", "Mild Anxiety",
        "Anxiety Comes and Goes", "Bad", "Very Bad", "Not Anxious"
    )

    // Descriptions for the anxiety intensity levels
    val intensityDescriptions = listOf(
        "A state of extreme panic",
        "An intense anxiety episode",
        "Constant small movements or restlessness",
        "Feeling anxious but managing",
        "Triggered by an event or thought",
        "Mild symptoms of anxiety",
        "Anxiety symptoms that come and go",
        "Feeling bad overall",
        "Feeling very bad overall",
        "No symptoms of anxiety"
    )

    // Main container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Background color of the screen
            .padding(top = 35.dp) // Padding from the top
    ) {
        // Column to hold the content vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Enable vertical scrolling
                .padding(16.dp) // Padding for the entire column
        ) {
            // Title for the screen
            Text(
                text = "Anxiety Intensity Levels",
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp)) // Spacer for spacing

            // Buttons for each anxiety intensity level
            intensityTexts.forEachIndexed { index, text ->
                Button(
                    onClick = {
                        currentSelectedAnxietyLevel = text // Update selected level
                    },
                    modifier = Modifier
                        .fillMaxWidth() // Make button fill the width
                        .padding(vertical = 4.dp), // Add vertical padding
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentSelectedAnxietyLevel == text) Color.Gray else intensityColors[index] // Highlight selected button
                    )
                ) {
                    // Button text
                    Text(
                        text = text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (currentSelectedAnxietyLevel == text) Color.White else Color.Black // Highlight text for selected button
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // Spacer for spacing

            // Label for the text field
            Text(
                text = "Feeling Notes",
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp)) // Spacer for spacing

            // Text field for entering notes
            TextField(
                value = currentFeelingNotes, // Current value of the text field
                onValueChange = { currentFeelingNotes = it }, // Update state on text change
                modifier = Modifier
                    .fillMaxWidth() // Make text field fill the width
                    .padding(8.dp), // Add padding
                textStyle = TextStyle(color = Color.Black), // Text style for the input
                placeholder = { Text("Enter your notes here") } // Placeholder text
            )

            Spacer(modifier = Modifier.height(16.dp)) // Spacer for spacing

            // Submit button
            Button(
                onClick = {
                    // Ensure an anxiety level is selected
                    if (currentSelectedAnxietyLevel.isEmpty() || currentSelectedAnxietyLevel == "Not Selected") {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please select an anxiety level before continuing.")
                        }
                    } else {
                        // Save the entered data
                        saveAnxietyData(
                            level = currentSelectedAnxietyLevel,
                            notes = currentFeelingNotes, // Notes remain optional
                            context = navController.context
                        )
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Data submitted successfully!") // Show confirmation
                        }
                        focusManager.clearFocus() // Clear keyboard focus
                        navController.navigateUp() // Navigate back after submission
                    }
                },
                modifier = Modifier.fillMaxWidth() // Make button fill the width
            ) {
                Text("Submit") // Button label
            }


        }

        // Dialog for showing the selected level description
        if (showDescription) {
            AlertDialog(
                onDismissRequest = { showDescription = false }, // Close dialog on dismiss
                confirmButton = {
                    Button(onClick = { showDescription = false }) {
                        Text("OK") // Confirmation button
                    }
                },
                text = {
                    Text(selectedDescription) // Display the selected description
                }
            )
        }

        // Snackbar for showing messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align snackbar to bottom-center
                .padding(10.dp) // Add padding
        )
    }
}

/**
 * Saves anxiety data with user ID.
 */
fun saveAnxietyData(level: String, notes: String, context: Context) {
    val filename = "anxiety_data.txt" // File to save data
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE) // Shared preferences for user session
    val userId = sharedPreferences.getString("userId", null) // Retrieve user ID

    if (userId.isNullOrEmpty()) {
        Log.e("SaveDataError", "User ID is missing. Cannot save data.") // Log error if user ID is missing
        return
    }

    val currentTime = System.currentTimeMillis() // Get current timestamp
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Format for date and time
    val formattedDate = dateFormat.format(Date(currentTime)) // Format the current date

    // Content to save in the file
    val fileContent = """
        ID: $userId
        Date: $formattedDate
        Level: $level
        Notes: $notes
    """.trimIndent()

    try {
        context.openFileOutput(filename, Context.MODE_APPEND).use { output ->
            output.write("$fileContent\n".toByteArray()) // Write data to file
        }
        Log.d("FileSave", "Anxiety data saved successfully for user ID: $userId") // Log success message
    } catch (e: IOException) {
        Log.e("FileSaveError", "Failed to save anxiety data to file", e) // Log error if saving fails
    }
}
