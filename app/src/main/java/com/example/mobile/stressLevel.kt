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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.view.WindowCompat
import com.example.mobile.ui.theme.MobileTheme
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class stressLevel : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MobileTheme {
                StressLevelContent(
                    onSubmit = { level, notes ->
                        saveData(level, notes)
                    }
                )
            }
        }
    }

    private fun saveData(level: String, notes: String) {
        val filename = "stress_history.txt"
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(currentTime))
        val fileContent = """
            Date: $formattedDate
            Level: $level
            Notes: $notes

        """.trimIndent()

        try {
            openFileOutput(filename, Context.MODE_APPEND).use { output ->
                output.write(fileContent.toByteArray())
            }
            Log.d("FileSave", "Data saved to internal storage in $filename")
        } catch (e: IOException) {
            Log.e("FileSaveError", "Failed to save data to file", e)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StressLevelContent(
    onSubmit: (String, String) -> Unit
) {
    var currentFeelingNotes by remember { mutableStateOf("") }
    var currentSelectedStressLevel by remember { mutableStateOf("Not Stressed") }
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                    .padding(8.dp)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(color = Color.Black),
                placeholder = { Text("Enter your notes here") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Call the onSubmit function to save data
                    onSubmit(currentSelectedStressLevel, currentFeelingNotes)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Data submitted successfully!")
                    }
                    focusManager.clearFocus()
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

@Preview(showBackground = true)
@Composable
fun StressLevelPreview() {
    MobileTheme {
        StressLevelContent(
            onSubmit = { _, _ -> }
        )
    }
}
