package com.example.mobile // Make sure to include your package name

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

class HelpLineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            HelpLine(navController) // Display the HelpLine Composable
        }
    }
}

@Composable
fun HelpLine(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var feedbackText by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState) // Make content scrollable
    ) {
        // Full-width Image Section
        Image(
            painter = painterResource(id = R.drawable.uwe),
            contentDescription = "UWE Image",
            modifier = Modifier
                .fillMaxWidth() // Ensures the image spans the entire width
                .height(130.dp)
        )

        // Apply padding to the rest of the content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Padding applied to all other content except the image
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Section Card Composable
            @Composable
            fun SectionCard(title: String, content: @Composable () -> Unit) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = title, fontSize = 18.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        content()
                    }
                }
            }

            // Contact Details Section
            SectionCard("Contact Details") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector =Icons.Filled.Phone,
                        contentDescription = "Phone Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Phone: +44 (0)117 32 86268.", fontSize = 14.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector =Icons.Filled.Email,
                        contentDescription = "Email Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Email: wellbeing@uwe.ac.uk", fontSize = 14.sp, color = Color.Gray)
                }
            }

            // 24/7 Suicide Helpline Card
            SectionCard("24/7 Suicide Helpline") {
                Text(text = "The Samaritans", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "Call: 116123",
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:08001234567"))
                        context.startActivity(intent)
                    }
                )
            }

            // 24/7 Mental Health Helpline Card
            SectionCard("Mind Support Line") {
                Text(
                    text = "Call: 0300 102 1234",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:08007654321"))
                        context.startActivity(intent)
                    }
                )
            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            SectionCard("Opening Hours") {
                Text(text = "Term Times", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Monday to Thursday: 8:30 AM - 5:00 PM", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Friday: 8:30 AM - 4:30 PM", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Out-of-Term Times", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Monday to Thursday: 8:30 AM - 4:30 PM", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Friday: 8:30 AM - 4:00 PM", fontSize = 14.sp, color = Color.Gray)
            }

            SectionCard("Web Resources") {
                Text(
                    text = "Visit our website",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uwe.ac.uk/life/health-and-wellbeing/get-wellbeing-support"))
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Student Support Services",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uwe.ac.uk/study/study-support/student-support-advisers"))
                        context.startActivity(intent)
                    }
                )
            }

            SectionCard("Popular Topics") {
                Text(
                    text = "Managing Stress",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uwe.ac.uk/stress-management"))
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Exam Preparation Tips",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uwe.ac.uk/study/study-support/study-skills/prepare-for-assessments#:~:text=Completing%20exams%20at%20home&text=Here%20are%20some%20tips%20to,friends%20and%20support%20each%20other"))
                        context.startActivity(intent)
                    }
                )
            }

            SectionCard("Emergency Resources") {
                Text(
                    text = "Emergency Hotline: 999",
                    fontSize = 14.sp,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nearest Hospital",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=nearest+hospital"))
                        context.startActivity(intent)
                    }
                )
            }

            // Feedback Section with TextField and Submit Button
            SectionCard("Feedback") {
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Enter your feedback") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        saveFeedback(context, feedbackText.text)
                        feedbackText = TextFieldValue("") // Clear the text after saving
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE)) // Purple button
                ) {
                    Text("Submit", color = Color.White)
                }
            }
        }
    }
}

// Function to save feedback with date and time in internal storage
fun saveFeedback(context: Context, feedback: String) {
    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(currentTime))

    val fileContent = """
        Date: $formattedDate
        Feedback: $feedback

    """.trimIndent()

    try {
        context.openFileOutput("feedback.txt", Context.MODE_APPEND).use { output ->
            output.write(fileContent.toByteArray())
        }
        Toast.makeText(context, "Feedback submitted!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
    }
}

// Function to read feedback from internal storage
fun readFeedback(context: Context): String {
    return try {
        context.openFileInput("feedback.txt").bufferedReader().useLines { lines ->
            lines.joinToString("\n")
        }
    } catch (e: Exception) {
        "No feedback available."
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHelpLine() {
    HelpLine(navController=rememberNavController
        ())
}
//hello

