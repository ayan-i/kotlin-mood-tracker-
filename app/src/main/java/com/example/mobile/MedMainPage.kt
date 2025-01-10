package com.example.mobile

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.MobileTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.colorResource
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.io.File
import java.io.FileReader
import java.util.Calendar

// MainActivity class for medication tracker
class MedTracker : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        setContent {
            MobileTheme {
                val navController = rememberNavController()
                MedicationMainPage(navController)
            }
        }
    }
}

//internal file for storing medications
private const val fileName = "medications15.txt"

//functions to load medications data from a file
fun loadMedications1(context: Context, userID: String?): List<Medication> {
    val file = File(context.filesDir, fileName)
    val medicationList = mutableListOf<Medication>()

    if (file.exists()) {
        val reader = FileReader(file)
        val lines = reader.readLines()
        reader.close()

        //processes each lone in the file and filter by userID
        lines.forEach { line ->
            val parts = line.split(",")
            if (parts.size == 8 && parts[0] == userID) {  // Filter by userID
                val medication = Medication(
                    userId= parts[0],
                    id = parts[1],
                    name = parts[2],
                    dosage = parts[3],
                    frequency = parts[4],
                    duration = parts[5].toInt(),
                    startDate = parts[6],
                    endDate = parts[7]
                )
                medicationList.add(medication)
            }
        }
    }
    return medicationList
}

//function to display the main medication page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationMainPage(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)

    val medications by remember {
        mutableStateOf(
            if (userId != null) {
                loadMedications1(context, userId) // Loads medications id userID exists
            } else {
                emptyList() //shows an empty list if no User ID
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medication",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 35.dp, start =5.dp),
                        color = Color.White,
                        fontSize = 30.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, top = 5.dp)
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(35.dp)
                                .align(Alignment.CenterStart) // Align to the left side
                                .clickable {
                                    navController.navigate(route = "overview_screen")
                                },
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        //floating action button to navigate to the add medications page
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addMedicationPage")
                },
                containerColor = colorResource(R.color.darkpurple)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Medication Entry")
            }
        },
        content = { paddingValues ->
            //main content showing medications or a message if none exist
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
            ) {


                if (medications.isNotEmpty()) {
                    medications.forEach { medication ->
                        MedicationCard(
                            medicationId = medication.id,
                            name = medication.name,
                            frequency = "Dosage: ${medication.dosage} \n${medication.frequency}",
                            navController = navController,
                            startDate = medication.startDate,
                            endDate = medication.endDate
                        )
                    }
                } else {
                    Text(
                        text = "No medications found.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    )
}

//Function to create a card for each medications
@Composable
fun MedicationCard(
    medicationId: String,
    name: String,
    frequency: String,
    navController: NavHostController,
    startDate: String,
    endDate: String
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current

    //Calculate the duration of the medication in days
    val duration = try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        if (end.isBefore(start)) {
            0
        } else {
            ChronoUnit.DAYS.between(start, end).toInt() + 1
        }
    } catch (e: Exception) {
        0
    }

    if (showTimePicker) {
        //shows time picker dialog
        ShowTimePickerDialog(
            onTimeSelected = { time, isDaily ->
                selectedTime = time
                scheduleNotification(context, time, name, isDaily) //schedules notification
                showTimePicker = false
            },
            onDismiss = {
                showTimePicker = false
            }
        )
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        //Display medication details
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = name, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                Text(text = frequency, fontSize = 16.sp)
                Text(
                    text = if (duration > 0) "Duration: $duration days" else "Invalid Date Range",
                    fontSize = 16.sp,
                    color = Color.Gray

                )

            }
            Row {
                //buttons for setting reminders or editing medication
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Set Reminder",
                        tint = colorResource(R.color.darkpurple)
                    )
                }
                IconButton(onClick = { navController.navigate("medDetailPage/${medicationId}") }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Medication Entry",
                        tint = colorResource(R.color.darkpurple)
                    )
                }
            }
        }
    }
}

//Dialog to pick reminder time
@Composable
fun ShowTimePickerDialog(onTimeSelected: (String, Boolean) -> Unit, onDismiss: () -> Unit) {
    var isDaily by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text("Set Reminder Time")
        },
        text = {
            //Dialog content with time picker
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Repeat Daily")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDaily,
                        onCheckedChange = { isDaily = it }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->
                            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                            onTimeSelected(formattedTime, isDaily)
                        },
                        hour,
                        minute,
                        true
                    ).show()
                }) {
                    Text("Pick Time")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}

//Creates notification channel
private fun createNotificationChannel(context: Context) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val name = "Medication Reminder"
        val descriptionText = "Channel for medication reminders"
        val importance = android.app.NotificationManager.IMPORTANCE_HIGH
        val channel = android.app.NotificationChannel("medication_reminder1", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: android.app.NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

//function to schedule notifications for medications reminder
@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, selectedTime: String, medicationName: String, isDaily: Boolean) {
    val calendar = Calendar.getInstance().apply {
        val timeParts = selectedTime.split(":")
        set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        set(Calendar.MINUTE, timeParts[1].toInt())
        set(Calendar.SECOND, 0)

        //if the time is in the past, schedule for the next day
        if (timeInMillis < System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("reminder_message", "Time to take your medication!")
        putExtra("medication_name1", medicationName)
    }

    val requestCode = selectedTime.hashCode()
    val pendingIntent = android.app.PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

    //Schedule the notification as repeating or one-time
    if (isDaily) {
        alarmManager.setRepeating(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            android.app.AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    } else {
        alarmManager.setExactAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
//hello1

