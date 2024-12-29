

package com.example.mobile

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnxietyScreen(navController: NavController) {
    var currentFeelingNotes by remember { mutableStateOf("") }
    var currentSelectedAnxietyLevel by remember { mutableStateOf("Anxiety Triggered") }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(color = Color.Black)


    // Colours for the intensity levels (from old code)
    val intensityColors = listOf(
        Color.Red, Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFFFC107),
        Color(0xFFFFEB3B), Color(0xFFCDDC39), Color(0xFF8BC34A), Color(0xFF4CAF50),
        Color(0xFF009688), Color(0xFF03A9F4)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top=35.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Anxiety Intensity Levels",
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val intensityTexts = listOf(
                "Panic Attack", "Anxiety Attack", "Constant Fidgeting",
                "Anxious but Survive", "Anxiety Triggered", "Mild Anxiety",
                "Anxiety Comes and Goes", "Bad", "Very Bad", "Not Anxious"
            )

            intensityTexts.forEachIndexed { index, text ->
                Button(
                    onClick = { currentSelectedAnxietyLevel = text },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = intensityColors[index] // Apply corresponding colour
                    )
                ) {
                    Text(
                        text = text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
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
                placeholder = { Text("Enter your notes here") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    saveAnxietyData(
                        level = currentSelectedAnxietyLevel,
                        notes = currentFeelingNotes,
                        context = navController.context
                    )
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Data submitted successfully!")
                    }
                    focusManager.clearFocus()
                    navController.navigateUp() // Navigate back after submission
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

/**
 * Saves anxiety data with user ID.
 */
fun saveAnxietyData(level: String, notes: String, context: Context) {
    val filename = "anxiety_data.txt"
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)

    if (userId.isNullOrEmpty()) {
        Log.e("SaveDataError", "User ID is missing. Cannot save data.")
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
        Log.d("FileSave", "Anxiety data saved successfully for user ID: $userId")
    } catch (e: IOException) {
        Log.e("FileSaveError", "Failed to save anxiety data to file", e)
    }
}