package com.example.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.launch
import java.io.File

class MedPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditMedicationScreen()
        }
    }
}
private const val fileName = "medications15.txt"


@Composable
fun EditMedicationScreen(navController: NavHostController? = null, medicationId: Int? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    var medications by remember { mutableStateOf(loadMedications1(context, userId)) }

    if (medicationId == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Invalid medication ID", Toast.LENGTH_SHORT).show()
            navController?.navigateUp()
        }
        return
    }

    val currentMedication = medications.find { it.id == medicationId.toString() }
    if (currentMedication == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Medication not found", Toast.LENGTH_SHORT).show()
            navController?.navigateUp()
        }
        return
    }

    var name by remember { mutableStateOf(TextFieldValue(currentMedication.name)) }
    var dosage by remember { mutableStateOf(TextFieldValue(currentMedication.dosage)) }
    var frequency by remember { mutableStateOf(TextFieldValue(currentMedication.frequency)) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Edit Medication",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        if (showError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Medication Name",color = Color.White, fontSize = 15.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.Gray)

        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = dosage,
            onValueChange = { dosage = it },
            label = { Text("Add your Dosage",color = Color.White, fontSize = 15.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.Gray)

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            label = { Text("Frequency",color = Color.White, fontSize = 15.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.Gray)

        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (name.text.isBlank() || dosage.text.isBlank() || frequency.text.isBlank()) {
                showError = true
                errorMessage = "Please fill in all fields"
                return@Button
            }

            scope.launch(Dispatchers.IO) {
                try {
                    val updatedMedication = currentMedication.copy(
                        name = name.text,
                        dosage = dosage.text,
                        frequency = frequency.text
                    )
                    saveMedication(context, updatedMedication)
                    scope.launch(Dispatchers.Main) {
                        Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                        navController?.navigateUp()
                    }
                } catch (e: Exception) {
                    scope.launch(Dispatchers.Main) {
                        showError = true
                        errorMessage = "Failed to save changes: ${e.message}"
                    }
                }
            }
        }) {
            Text("Save Changes", color = Color.White)
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {  // Using IO dispatcher for file operations
                    try {
                        deleteMedication(context, currentMedication)

                        // Switch to Main dispatcher for UI updates
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(context, "Medication deleted successfully", Toast.LENGTH_SHORT).show()
                            navController?.navigateUp()
                        }
                    } catch (e: Exception) {
                        // Switch to Main dispatcher for UI updates
                        scope.launch(Dispatchers.Main) {
                            showError = true
                            errorMessage = "Failed to delete medication: ${e.message}"
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Delete", color = Color.White)
        }
    }
}

fun saveMedication(context: Context, medication: Medication) {
    try {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            throw IllegalStateException("Medication file not found")
        }

        val lines = file.readLines().toMutableList()
        val updatedLines = lines.map { line ->
            val parts = line.split(",")

            // Ensure the line has the expected number of parts and valid id
            if (parts.size < 7) {
                throw IllegalStateException("Invalid line format in medication file: $line")
            }

            if (parts[0]== medication.userId && parts[2] == medication.name) {
                "${medication.userId},${medication.id},${medication.name},${medication.dosage},${medication.frequency},${medication.duration},${medication.startDate},${medication.endDate}"
            } else line
        }
        file.writeText(updatedLines.joinToString("\n"))
    } catch (e: Exception) {
        throw Exception("Failed to save medication: ${e.message}")
    }
}


fun deleteMedication(context: Context, medication: Medication) {
    try {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            throw IllegalStateException("Medication file not found")
        }

        val lines = file.readLines().toMutableList()
        val updatedLines = lines.filterNot { line ->
            val parts = line.split(",")
            // Compare medication.id (String) with parts[0] (String)
            parts[0]== medication.userId && parts[1] == medication.id
        }
        file.writeText(updatedLines.joinToString("\n"))
    } catch (e: Exception) {
        throw Exception("Failed to delete medication: ${e.message}")
    }
}



@Preview(showBackground = true)
@Composable
fun EditMedicationScreenPreview() {
    EditMedicationScreen()
}
//hello
