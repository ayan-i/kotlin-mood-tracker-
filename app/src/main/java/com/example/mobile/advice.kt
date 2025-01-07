package com.example.mobile // Define the package for the application

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Main activity for the helpline screen
class HelpLineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            HelpLine(navController) // Launch the HelpLine composable
        }
    }
}

// Composable function for displaying the helpline UI
@Composable
fun HelpLine(navController: NavController) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color.White) // Set status bar color
    systemUiController.setNavigationBarColor(color = Color.Transparent) // Set navigation bar color
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState) // Enable vertical scrolling
    ) {
        // Back button to navigate to the previous screen
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .size(48.dp)
                .padding(top=20.dp,start=15.dp)
                .clickable {
                    navController.navigate(route = "overview_screen")
                },
            tint = Color.Black
        )

        // Display an image at the top
        Image(
            painter = painterResource(id = R.drawable.uwe),
            contentDescription = "UWE Image",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=25.dp)
                .height(130.dp)
        )

        // Content section with padding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // A reusable card component for different sections
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

            // Contact details section with phone and email
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

            // Section for the suicide helpline with a clickable call button
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

            // Section for Mind support helpline
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

            // Divider for visual separation
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Opening hours information
            SectionCard("Opening Hours") {
                Text(text = "Term Times", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Monday to Thursday: 8:30 AM - 5:00 PM", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Friday: 8:30 AM - 4:30 PM", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Out-of-Term Times", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Monday to Thursday: 8:30 AM - 4:30 PM", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Friday: 8:30 AM - 4:00 PM", fontSize = 14.sp, color = Color.Gray)
            }

            // Web resources with clickable links
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
            }
        }
    }
}

// Preview function for design testing
@Preview(showBackground = true)
@Composable
fun PreviewHelpLine() {
    HelpLine(navController=rememberNavController())
}
