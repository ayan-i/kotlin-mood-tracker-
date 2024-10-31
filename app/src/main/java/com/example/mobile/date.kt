@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mobile


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.IconButton
import androidx.compose.ui.window.Popup
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.theme.MobileTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.text.DateFormat
import java.util.Calendar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material3.DatePicker
import java.util.Locale

class dateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DatePickerInput()
        }
    }
}

@Composable
fun DatePickerInput() {
    var selectedDate by remember { mutableStateOf("Pick Date") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text("Pick a date") },
        placeholder = { Text("DD/MM/YYYY") },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        selectedDate = convertMillisToDate(selectedMillis)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
        }

        Text(
            text = "History",
            fontSize = 40.sp,
            color = Color.White,
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                .size(width = 300.dp, height = 115.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sentiment_very_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24),
                    contentDescription = "emotion",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 20.dp)
                )

                Column(
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = "Date 2024", // Date of the entry from the database
                        color = Color.Black,
                        fontSize = 17.sp,
                    )
                    Text(
                        text = "Angry", // Emotion from the database
                        color = Color.Black,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Anxiety", // Emotion from the database
                        color = Color.Black,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Stress", // Emotion from the database
                        color = Color.Black,
                        fontSize = 17.sp
                    )
                }
            }
        }
        BottomNavigation(
            backgroundColor = colorResource(R.color.lightpurple),
            contentColor = Color.Black,
            modifier = Modifier.padding(top = 810.dp)
                .fillMaxWidth()
                .height(80.dp)

        ) {
            BottomNavigationItem(
                selected = false,
                onClick = {  },
                icon = { Icon(Icons.Filled.ShowChart, contentDescription = null,
                    modifier = Modifier.size(45.dp).padding(top = 10.dp)) },
                label = { Text("Overview",fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp) }
            )
            BottomNavigationItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Filled.History, contentDescription = null,
                    modifier = Modifier.size(45.dp) .padding(top = 10.dp)) },
                label = { Text("History",
                    fontSize = 16.sp) }
            )
            BottomNavigationItem(
                selected = false,
                onClick = {  },
                icon = { Icon(Icons.Filled.AddCircleOutline, contentDescription = null,
                    modifier = Modifier.size(60.dp) .padding(top = 10.dp)) },
            )
            BottomNavigationItem(
                selected = false,
                onClick = {  },
                icon = { Icon(Icons.Filled.Medication, contentDescription = null,
                    modifier = Modifier.size(45.dp) .padding(top = 10.dp)) },
                label = { Text("Med",fontSize = 16.sp,
                ) }
            )
            BottomNavigationItem(
                selected = false,
                onClick = {  },
                icon = { Icon(Icons.Filled.Person, contentDescription = null,
                    modifier = Modifier.size(45.dp) .padding(top = 10.dp)) },
                label = { Text("Profile",
                    fontSize = 16.sp) }

            )
        }
    }

}





// Helper function to format the selected date
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun date2() {
    MobileTheme {
        DatePickerInput()
    }
}