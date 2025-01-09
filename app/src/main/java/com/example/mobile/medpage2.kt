package com.example.mobile

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.MobileTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState


data class Medication(
    val userId: String = "",
    val id: String="",
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val duration: Int,
    val startDate: String = "",
    val endDate: String = ""
)

// Main Activity Class
class MedicationDetailActivity1 : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileTheme {
                val navController = rememberNavController()
                AddMedicationScreen(navController)
            }
        }
    }
}

private const val fileName = "medications15.txt"


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val id by remember { mutableIntStateOf(generateNextMedicationId(context, userId)) }
    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    val duration by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf("") }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Medication",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication Name", color = Color.White) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        OutlinedTextField(
            value = dosage,
            onValueChange = { dosage = it },
            label = { Text("Dosage", color = Color.White) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { showStartDatePicker = true }) {
                Text("Start Date: ${startDate.format(dateFormatter)}",color= Color.White)
            }
            TextButton(onClick = { showEndDatePicker = true }) {
                Text("End Date: ${endDate.format(dateFormatter)}",color= Color.White)
            }
        }

        if (showStartDatePicker) {
            val startDateState = rememberDatePickerState(
                initialSelectedDateMillis = startDate.toEpochDay() * 86400000
            )
            DatePickerDialog(
                onDismissRequest = {
                    showStartDatePicker = false
                    startDateError = ""
                },
                confirmButton = {
                    TextButton(onClick = {
                        val selectedDateMillis = startDateState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val selectedDate = LocalDate.ofEpochDay(selectedDateMillis / 86400000)
                            val today = LocalDate.now()

                            if (selectedDate.isBefore(today)) {
                                startDateError = "Cannot select a past date"
                            } else {
                                startDate = selectedDate
                                startDateError = ""
                                showStartDatePicker = false
                            }
                        } else {
                            startDateError = "Please select a date"
                        }
                    }) {
                        Text("OK", color = Color.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showStartDatePicker = false
                        startDateError = ""
                    }) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            ) {
                DatePicker(state = startDateState)
            }
        }


        if (showEndDatePicker) {
            val endDateState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDate = endDateState.selectedDateMillis?.let {
                            LocalDate.ofEpochDay(it / 86400000)
                        } ?: endDate
                        showEndDatePicker = false
                    }) {
                        Text("OK", color = Color.Black)
                    }
                }
            ) {
                DatePicker(state = endDateState)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val medication = Medication(
                    id = id.toString(),
                    name = medicationName,
                    dosage = dosage,
                    frequency = "daily",
                    duration = duration.toIntOrNull() ?: 0,
                )
                val file = File(context.filesDir, fileName)
                val writer = FileWriter(file, true)
                val line = "${userId ?: "0"},${medication.id},${medication.name},${medication.dosage},${medication.frequency},${medication.duration},${startDate.format(dateFormatter)},${endDate.format(dateFormatter)}"
                writer.write(line + "\n")
                writer.close()

                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Add Medication", fontSize = 18.sp)
        }
    }
}

// Function to Generate the Next Medication ID
private fun generateNextMedicationId(context: Context, userId: String?): Int {
    val medications = loadMedications1(context, userId)
    return (medications.maxOfOrNull { it.id.toIntOrNull() ?: 0 }?.plus(1) ?: 1)
}


// Preview
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddMedicationPreview() {
    val navController = rememberNavController()
    MobileTheme {
        AddMedicationScreen(navController)
    }

    //h
}
