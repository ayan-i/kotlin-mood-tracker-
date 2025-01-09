package com.example.mobile

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.IOException
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.abs

// activity for history page
class OptionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //sets the navigation
            val navController = rememberNavController()
            //allows navigation through Option Composable
            Option(navController)
        }
    }
}
// classes for mood ,stress and anxiety entries with the date associated with it
data class MoodEntry(val date: String, val mood: String)
data class StressEntry21(val date:String,val stressLevel:String,val Notes:String)
data class AnxietyEntry(val date:String,val anxietyLevel:String,val Notes:String)

//combined entry class from the variables from the other classes
data class CombinedEntry(
    val date: String,
    val mood: String,
    val stressLevel: String?, // Nullable variable to store the user's stress level as a String.
    val stressNotes: String?,// Nullable variable to store the user's stress notes as a String.
    val anxietyLevel: String?,// Nullable variable to store the user's anxiety level as a String.
    val anxietyNotes: String?//Nullable variable to store the user's anxiety notes  as a String.
)

//reading internal storage uwe week activity
//also geeks for geeks
fun readMoodHistory(context: Context, userId: String): List<MoodEntry> {
    return try {
        // Define input and output formats for parsing and formatting dates
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault())

        //open moodSELECT.txt files
        val inputStream = context.openFileInput("moodSELECT.txt")
        val lines = BufferedReader(InputStreamReader(inputStream))

        val moodEntries = mutableListOf<MoodEntry>()

        //go through each line in the file
        lines.forEachLine { line ->
            val parts = line.split(",")
            //split the line into userID,date,mood
            if (parts.size == 3 && parts[0] == userId) {
                val storedDate = parts[1].trim()
                val mood = parts[2].trim()
                val date = inputFormat.parse(storedDate)
                val formattedDate =
                    //format the date
                    if (date != null) {
                        outputFormat.format(date)
                    }
                    else {
                        storedDate
                    }
                // add the entry into the list
                moodEntries.add(MoodEntry(formattedDate, mood))
            }
        }

        //return it
        moodEntries
    } catch (e: Exception) {
        //log error message
        Log.e("MoodHistory", "Error reading or processing moodSELECT.txt", e)
        emptyList()
    }
}

fun readStressHistory(context: Context, userId: String): List<StressEntry21> {
    return try {
        // Define input and output formats for parsing and formatting dates
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Original format
        val outputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault()) // Desired format

        //open stress_history.txt file
        val inputStream = context.openFileInput("stress_history.txt")
        val lines = BufferedReader(InputStreamReader(inputStream)).readLines()
        // go through each line
        lines.chunked(4).mapNotNull { chunk ->
            if (chunk.size == 4) {
                // Ensure the chunk contains exactly 4 lines
                // Extract details from the chunk
                val id = chunk[0].substringAfter("ID:").trim()
                val stored_date = chunk[1].substringAfter("Date:").trim()
                val level = chunk[2].substringAfter("Level:").trim()
                val notes = chunk[3].substringAfter("Notes:").trim()

                // Check if the user ID matches the provided userId
                if (id == userId) {
                    // Parse and format the date
                    val date = inputFormat.parse(stored_date)
                    val formattedDate = if (date != null) outputFormat.format(date) else stored_date
                    // Create and return a StressEntry21 object
                    StressEntry21(formattedDate, level, notes)
                } else {
                    null
                }
            } else {
                null
            }
        }
    } catch (e: IOException) {
        Log.e("StressHistory", "Error reading stress_history.txt file", e)
        emptyList()
    }
}


fun readAnxietyHistory(context: Context, userId: String): List<AnxietyEntry> {
    return try {
        // Define input and output formats for parsing and formatting dates
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Original format
        val outputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault()) // Desired format

        // Open the anxiety data file for reading
        val inputStream = context.openFileInput("anxiety_data.txt")
        val lines = BufferedReader(InputStreamReader(inputStream)).readLines()

        // Process the file lines in chunks of 4 (each chunk represents one anxiety entry)
        lines.chunked(4).mapNotNull { chunk ->
            if (chunk.size == 4) {
                //ensures chunk has 4 lines
                val id = chunk[0].substringAfter("ID:").trim()
                val stored_date = chunk[1].substringAfter("Date:").trim()
                val level = chunk[2].substringAfter("Level:").trim()
                val notes = chunk[3].substringAfter("Notes:").trim()

                // Check if the user ID matches the provided userId
                if (id == userId) {
                    val date = inputFormat.parse(stored_date)
                    val formattedDate = if (date != null) outputFormat.format(date) else stored_date
                    // Create and return an AnxietyEntry object
                    AnxietyEntry(formattedDate, level, notes)
                } else {
                    null // Skip the entry if the ID doesn't match
                }
            } else {
                null // Skip incomplete chunks
            }
        }
    } catch (e: IOException) {
        Log.e("AnxietyHistory", "Error reading anxiety_data.txt file", e)
        emptyList()
    }
}


fun mergeEntries(
    moodEntries: List<MoodEntry>,
    stressEntries: List<StressEntry21>,
    anxietyEntries: List<AnxietyEntry>
): List<CombinedEntry> {
    //inputFormat has date and time
    //date only extracts only the date
    val inputFormat = SimpleDateFormat("EEEE MMM d - HH:mm", Locale.getDefault())
    val dateOnlyFormat = SimpleDateFormat("EEEE MMM d", Locale.getDefault())

    //group mood,anxiety and stress by the same  days
    val moodsByDay = moodEntries.groupBy { inputFormat.parse(it.date)?.let { dateOnlyFormat.format(it) } }
    val stressByDay = stressEntries.groupBy { inputFormat.parse(it.date)?.let { dateOnlyFormat.format(it) } }
    val anxietyByDay = anxietyEntries.groupBy { inputFormat.parse(it.date)?.let { dateOnlyFormat.format(it) } }

    //initialise the list to store merged entries
    val mergedList = mutableListOf<CombinedEntry>()

    //go through each day in mood entries
    moodsByDay.forEach { (day, moods) ->
        if (day != null) {
            //use of ?: to show if it is null from kotlin website as elvis operator
            // Get the corresponding stress and anxiety entries for the day.
            // If no entries exist for the day, use an empty list
            val dailyStress = stressByDay[day]?.toMutableList() ?: mutableListOf()
            val dailyAnxiety = anxietyByDay[day]?.toMutableList() ?: mutableListOf()

            // Iterate through the mood entries for the day.
            moods.forEach { mood ->
                // Find the closest stress entry to the current mood entry based on time difference from the function
                //same for anxiety
                val stress = dailyStress.minByOrNull { timeDiff(it.date, mood.date, inputFormat) }
                val anxiety = dailyAnxiety.minByOrNull { timeDiff(it.date, mood.date, inputFormat) }

                //remove selected stress & anxiety entries
                dailyStress.remove(stress)
                dailyAnxiety.remove(anxiety)

                //add a new combinedEntry to the mergedList
                mergedList.add(
                    CombinedEntry(
                        date = mood.date,//mood entry date
                        mood = mood.mood,//mood value from moodEntry
                        stressLevel = stress?.stressLevel,// Stress level from the closest stress entry
                        stressNotes = stress?.Notes,// Notes from the closest stress entry
                        anxietyLevel = anxiety?.anxietyLevel,// Anxiety level from the closest anxiety entry
                        anxietyNotes = anxiety?.Notes // Notes from the closest anxiety entry
                    )
                )
            }
        }
    }

    // Return the merged list of combined entries
    return mergedList
}

//used to calculate time difference
//use of ?: to show if it is null from kotlin website as elvis operator
//Long.MAX_VALUE means gets the highest value
//0l long value default to 0
private fun timeDiff(date1: String, date2: String, format: SimpleDateFormat): Long {
    // If parsing date1 fails, use Long.MAX_VALUE (maximum possible value) as the default time
    val time1 = format.parse(date1)?.time ?: Long.MAX_VALUE
    //    // If parsing date2 fails, use 0L (default value for the "long" type) as the default time
    val time2 = format.parse(date2)?.time ?: 0L
    // Return the absolute difference between the two times
    return abs(time1 - time2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Option(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color.Black)
    systemUiController.setNavigationBarColor(color = colorResource(R.color.lightpurple))

    // Drawer and coroutine scope setup for UI interaction
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Get the userID from sharedPreferences
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Retrieve userId from shared preferences
    val userId = sharedPreferences.getString("userId", null)

    // Declare mutable state to hold different types of historical data
    var moodHistory by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var stressHistory by remember { mutableStateOf<List<StressEntry21>>(emptyList()) }
    var anxietyHistory by remember { mutableStateOf<List<AnxietyEntry>>(emptyList()) }
    var combinedHistory by remember { mutableStateOf<List<CombinedEntry>>(emptyList()) }
    var filteredHistory by remember { mutableStateOf<List<CombinedEntry>>(emptyList()) }

    var selectedDate by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Load data in a non-blocking way
    LaunchedEffect(context) {
        if (!userId.isNullOrEmpty()) {
            Log.d("UserId", "Retrieved userId: $userId")
            moodHistory = readMoodHistory(context, userId)
            stressHistory = readStressHistory(context, userId)
            anxietyHistory = readAnxietyHistory(context, userId)
            combinedHistory = mergeEntries(moodHistory, stressHistory, anxietyHistory)
            filteredHistory = combinedHistory

            Log.d("MoodHistory", "Loaded entries: ${moodHistory.size}")
            Log.d("StressHistory", "Loaded entries: ${stressHistory.size}")
            Log.d("AnxietyHistory", "Loaded entries: ${anxietyHistory.size}")
            Log.d("CombinedHistory", "Loaded entries: ${combinedHistory.size}")
        } else {
            Log.e("MoodHistory", "Error: User ID is null or empty")
        }
    }

    // Function to filter based on search query
    fun filterHistory(query: String) {
        filteredHistory = combinedHistory.filter { entry ->
            entry.mood.contains(query, ignoreCase = true) ||
                    entry.stressNotes?.contains(query, ignoreCase = true) == true ||
                    entry.anxietyNotes?.contains(query, ignoreCase = true) == true ||
                    entry.stressLevel?.contains(query, ignoreCase = true) == true ||
                    entry.anxietyLevel?.contains(query, ignoreCase = true) == true
        }
    }
    //get the current date
    val currentDate = Calendar.getInstance()
    val currentYear = currentDate.get(Calendar.YEAR)
    val currentMonth = currentDate.get(Calendar.MONTH)
    val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

    // Date Picker setup
    val datePickerDialog = remember {
        // _ used for what the user puts in
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            // Format the selected date as a string
            val selectedDateStr = SimpleDateFormat("EEEE MMM d", Locale.getDefault()).format(
                Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
            )
            selectedDate = selectedDateStr

            // Filter the combined history based on the selected date
            filteredHistory = combinedHistory.filter { it.date.contains(selectedDateStr) }
        }, currentYear, currentMonth, currentDay)
        //current date in the filter comes up first
    }

    // Modal drawer setup for navigation menu-top bar
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent1(navController, context) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("History", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    backgroundColor = colorResource(R.color.lightpurple),
                    navigationIcon = {
                        //hamburger menu
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // This is where the search icon will be placed at the right side
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = colorResource(R.color.darkpurple)
                            )
                        }
                    },
                    modifier = Modifier.padding(top = 25.dp)
                )
            },
        content = { padding ->
                // Main content displaying history cards or message if empty
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)
                        .padding(padding)
                ) {
                    //Calender filter added in a seperate row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top=17.dp,start=100.dp)
                            //made it clickable so the calender can show up
                            .clickable { datePickerDialog.show() }
                    ) {
                        Icon(
                            //calender icon
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendar Icon",
                            tint = colorResource(R.color.darkpurple),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(29.dp)
                        )
                        Text(
                            text = "Select Date",
                            color = colorResource(R.color.lightpurple),
                            fontSize = 23.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    // Display selected date if selected from calender
                    selectedDate?.let {
                        Text(
                            text = "$it",
                            color = colorResource(R.color.lightpurple),
                            fontSize = 23.sp,
                            modifier = Modifier.padding(start=100.dp,top=25.dp)

                        )
                    }

                    // shows the textfield when the search icon is pressed
                    if (isSearchVisible) {
                        //styling of the textfield
                        OutlinedTextField(
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                cursorColor = Color.White,
                                unfocusedLabelColor = Color.Gray,
                                focusedLabelColor = Color.White,
                                textColor = Color.White,
                                unfocusedBorderColor = colorResource(R.color.lightpurple),
                                focusedBorderColor = colorResource(R.color.lightpurple)
                            ),
                            //call the state of the search query
                            value = searchQuery,
                            onValueChange = { query -> //checks the query taken into the state
                                searchQuery = query
                                filterHistory(query) //using onvaluechange call the filtered history
                            },
                            //placeholder text
                            label = { Text("Search through the entries") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                        )
                    }

                    // Display filtered history entries
                    if (filteredHistory.isEmpty()) {
                        Text(
                            text = "No entries found",
                            color = Color.White,
                            fontSize = 25.sp,
                            modifier = Modifier.padding(50.dp)
                        )
                    } else {
                        // Display filtered history entries
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                        ) {
                            items(filteredHistory) { entry ->
                                MoodHistoryCard(entry)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom navigation bar with different navigation items
                BottomNavigationBar1(navController = navController)
            }
        )
    }
}



@Composable
fun BottomNavigationBar1(navController: NavController) {
    BottomNavigation(
        backgroundColor = colorResource(R.color.lightpurple),
        contentColor = Color.Black,
        modifier = Modifier.padding(bottom = 22.dp)
    ) {
        BottomNavigationItem(
            // Overview item
            selected = false,
            onClick = { navController.navigate("overview_screen") },
            icon = {
                Icon(
                    Icons.Default.Dashboard,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 10.dp)
                )
            },
            label = { Text("Overview", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
        )
        BottomNavigationItem(
            // History item
            selected = false,
            onClick = { navController.navigate("history_screen") },
            icon = {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 10.dp)
                )
            },
            label = { Text("History", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
        )
        BottomNavigationItem(
            // Add mood item
            selected = false,
            onClick = { navController.navigate("mood_screen") },
            icon = {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(top = 10.dp)
                )
            }
        )
        BottomNavigationItem(
            //add stress item
            selected = false,
            onClick = { navController.navigate("stress_screen") },
            icon = {
                Icon(
                    Icons.Default.Medication,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 10.dp)
                )
            },
            label = { Text("Med", fontSize = 16.sp) }
        )
        BottomNavigationItem(
            // Advice item
            selected = false,
            onClick = { navController.navigate("advice_screen") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Help",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 10.dp)
                )
            },
            label = { Text("Advice", fontSize = 13.sp) }
        )
    }
}
// Function for displaying drawer content with navigation options
@Composable
fun DrawerContent1(navController: NavController, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.lightpurple))
            .padding(16.dp)
    ) {
        // Title for navigation menu
        Text(
            text = "Navigation Menu",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
        )
        Divider(color = Color.Black, thickness = 1.dp)

        // Drawer items for different screens
        DrawerItem1("Overview", Icons.Default.Dashboard) { navController.navigate("overview_screen") }
        DrawerItem1("Advice", Icons.Default.Phone) { navController.navigate("advice_screen") }
        DrawerItem1("Mood", Icons.Default.AddCircle) { navController.navigate("mood_screen") }
        DrawerItem1("Stress Level", Icons.Default.BatteryAlert) { navController.navigate("stress_screen") }
        DrawerItem1("Anxiety Level", Icons.Default.Warning) { navController.navigate("anxiety_screen") }
        DrawerItem1("Reminder Activity", Icons.AutoMirrored.Filled.EventNote) { navController.navigate("reminder_screen") }
        DrawerItem1("Logout", Icons.AutoMirrored.Filled.Logout) { logout(navController, context) }
    }
}

// A single item in the drawer with an icon and label
@Composable
fun DrawerItem1(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

// Displays a card with mood and additional information like stress and anxiety levels
@Composable
//uses the combinedEntry list
fun MoodHistoryCard(entry: CombinedEntry) {
    // Data class to for mood detailes
    data class Mood(val id: Int, val Moodname: String, val MoodEmoji: Int, val color: Color)

    //predefined moods with associated emoji icons and names and colour
    val moods = listOf(
        Mood(1, "Joyful", R.drawable.veryhappy, colorResource(R.color.lightblue)),
        Mood(
            2,
            "Happy",
            R.drawable.sentiment_satisfied_24dp_61c52f_fill0_wght400_grad0_opsz24,
            colorResource(R.color.green)
        ),
        Mood(3, "Meh", R.drawable.neutral, colorResource(R.color.yellow)),
        Mood(
            4,
            "Bad",
            R.drawable.sentiment_dissatisfied_24dp_dc602e_fill0_wght400_grad0_opsz24,
            colorResource(R.color.orange)
        ),
        Mood(
            5,
            "Down",
            R.drawable.sentiment_very_dissatisfied_24dp_e73e3e_fill0_wght400_grad0_opsz24,
            colorResource(R.color.red)
        )
    )
    //find the equivalent mood name with the entries mood name
    val moodEmoji = moods.find { it.Moodname == entry.mood }
    //used for expandable cards
    var expands by remember { mutableStateOf(false) }

    //card to display mood entry
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clickable { expands = !expands }//when clicked it expands
            .animateContentSize(),//makes the animation
        backgroundColor = colorResource(R.color.boxcolor),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        // Column to arrange content vertically
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row to arrange the mood icon and the date and mood text horizontally
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                //display mood emoji if found
                moodEmoji?.let {
                    Image(
                        painter = painterResource(id = it.MoodEmoji),
                        contentDescription = "Mood Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 16.dp)
                    )
                }
                // Column to display the date and mood text
                Column {
                    Text(
                        text = entry.date,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    // Display the mood text with dynamic color
                    Text(
                        text = entry.mood,
                        color = moodEmoji?.color ?: Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                }
            }

            //expands only when there is stress or anxiety info when state is true
            if (expands) {
                // Displaying stress information if recorded
                entry.stressLevel?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Stress Level: $it",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                entry.stressNotes?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Stress Notes: $it",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                // Displaying anxiety information if recorded
                entry.anxietyLevel?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Anxiety Level: $it",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                entry.anxietyNotes?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Anxiety Notes: $it",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }
    }
}



