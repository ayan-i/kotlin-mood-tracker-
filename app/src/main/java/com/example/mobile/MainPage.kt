
package com.example.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.theme.MobileTheme
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainPage(navController: NavController) {
    //hello
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(R.drawable.images), contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mood Tracker",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp
        )

        Spacer(modifier = Modifier.height(130.dp))

        // Login button with custom size
        Button(
            onClick = {navController.navigate(route="login_screen")},
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.darkpurple),
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f) // Set width to 80% of the parent
                .height(60.dp) // Set custom height
        ) {
            Text("Login", fontSize = 20.sp) // Set custom text size
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider or spacing
        Text(
            text = "Don't have an account yet?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register button with custom size
        Button(
            onClick = {navController.navigate(route="signup_screen")},
            modifier = Modifier
                .fillMaxWidth(0.8f) // Set width to 80% of the parent
                .height(60.dp), // Set custom height
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.darkpurple),
            ),
        ) {
            Text("Register", fontSize = 20.sp) // Set custom text size
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainPagePreview() {
    MobileTheme {
        MainPage(navController = rememberNavController())
    }
}






//    Box(
//        modifier = Modifier
//            .fillMaxHeight()
//            .fillMaxWidth()
//            .background(color = Color.Black)
//    ) {
//        Text(
//            text = "MOODTRACKR!",
//            color = Color.White,
//            fontSize = 45.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(top = 170.dp, start = 40.dp)
//        )
//
//        Button(
//            onClick = { navController.navigate("login_screen")  },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = colorResource(R.color.darkpurple) // Use containerColor for Material 3
//            ),
//            modifier = Modifier
//                .padding(top = 430.dp, start = 65.dp)
//                .size(width = 300.dp, height = 45.dp),
//        ) {
//            Text(text = "Login", color = Color.White, fontSize = 22.sp)
//        }
//
//        Text(
//            text = "Don't have an account yet?",
//            color=Color.White,
//            fontSize = 18.sp,
//            modifier = Modifier.padding(top= 520.dp,start=90.dp)
//            )
//        Button(
//            onClick = {navController.navigate("signup_screen")},
//            colors = ButtonDefaults.buttonColors(
//                containerColor = colorResource(R.color.darkpurple)
//            ),
//            modifier = Modifier
//                .padding(top = 560.dp, start = 65.dp)
//                .size(width = 300.dp, height = 45.dp),
//        ) {
//            Text(text = "Join MoodTrackr", color = Color.White, fontSize = 18.sp)
//        }
//    }
//}
