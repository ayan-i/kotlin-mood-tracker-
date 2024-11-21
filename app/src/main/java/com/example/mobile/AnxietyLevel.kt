package com.example.mobile


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.ui.theme.MobileTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AnxietyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileTheme{
                AnxietyIntensityScreen(
                    initialLevel = "Anxiety Triggered",
                    initialNotes = "",
                    onSubmit = { level, notes ->
                        saveDataToFile(level, notes)  // Save data to file on submit
                        Log.d("SubmitAction", "Level: $level, Notes: $notes")
                    }
                )
            }
        }
    }

    private fun saveDataToFile(level: String, notes: String) {
        val filename = "anxiety_data.txt"
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
            Log.d("FileSave", "Data saved to internal storage")
        } catch (e: IOException) {
            Log.e("FileSaveError", "Failed to save data to file", e)
        }
    }

    @Composable
    fun AnxietyIntensityScreen(
        initialLevel: String,
        initialNotes: String,
        onSubmit: (String, String) -> Unit
    ) {
        var currentFeelingNotes by remember { mutableStateOf(initialNotes) }
        var currentSelectedAnxietyLevel by remember { mutableStateOf(initialLevel) }
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()

        LaunchedEffect(isPressed.value) {
            while (isPressed.value) {
                scrollState.scrollBy(10f)
                delay(16L)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
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
                    "Panic Attack",
                    "Anxiety Attack",
                    "Constant Fidgeting",
                    "Anxious but Survive",
                    "Anxiety Triggered",
                    "Mild Anxiety",
                    "Anxiety Comes and Goes",
                    "Bad",
                    "Very Bad",
                    "Not Anxious"
                )

                val intensityColors = listOf(
                    Color.Red, Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFFFC107),
                    Color(0xFFFFEB3B), Color(0xFFCDDC39), Color(0xFF8BC34A), Color(0xFF4CAF50),
                    Color(0xFF009688), Color(0xFF03A9F4)
                )

                intensityTexts.forEachIndexed { index, text ->
                    var isClicked by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            currentSelectedAnxietyLevel = text
                            isClicked = !isClicked
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isClicked) intensityColors[index].copy(alpha = 0.7f) else intensityColors[index]
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (isClicked) 8.dp else 4.dp
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
                        .padding(8.dp)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(color = Color.Black),
                    placeholder = { Text("Enter your notes here") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmit(currentSelectedAnxietyLevel, currentFeelingNotes)
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
    fun AnxietyIntensityScreenPreview() {
        MobileTheme {
            AnxietyIntensityScreen(
                initialLevel = "Anxiety Triggered",
                initialNotes = "",
                onSubmit = { _, _ -> /* Handle submit action in preview */ }
            )
        }
    }
}
