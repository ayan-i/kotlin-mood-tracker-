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

//class representing medication entry
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

// Main Activity for adding medication details
class MedicationDetailActivity1 : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileTheme {
                val navController = rememberNavController()
                AddMedicationScreen(navController)//displays add medication screen
            }
        }
    }
}

//file name for storing medication data
private const val fileName = "medications15.txt"

//fucntion for add medications screen
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)//Retrieve userID from share preferences
    val id by remember { mutableIntStateOf(generateNextMedicationId(context, userId)) } //Generates new medications ID
    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    val duration by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf("") }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    //UI layout for add medication screen
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

        //buttons for selecting start and end dates
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

        //date picker dialog gor start date
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
                                startDateError = "Cannot select a past date" //Validation for past date
                            } else {
                                startDate = selectedDate
                                startDateError = ""
                                showStartDatePicker = false
                            }
                        } else {
                            startDateError = "Please select a date" //error for missing date
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


        //date picker dialog for end date
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

        //button to save medication entry
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
    val medications = loadMedications1(context, userId)//load existing medications
    return (medications.maxOfOrNull { it.id.toIntOrNull() ?: 0 }?.plus(1) ?: 1) //determines the next ID
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

}
//hello1

