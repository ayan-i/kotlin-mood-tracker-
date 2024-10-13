package com.example.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu


@Composable
fun MainPage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color.Black)
    ) {
        Text(
            text = "MOODTRACKR!",
            color = Color.White,
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 170.dp, start = 40.dp)
        )

        Button(
            onClick = { navController.navigate("login_screen")  },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.darkpurple) // Use containerColor for Material 3
            ),
            modifier = Modifier
                .padding(top = 430.dp, start = 65.dp)
                .size(width = 300.dp, height = 45.dp),
        ) {
            Text(text = "Login", color = Color.White, fontSize = 22.sp)
        }

        Text(
            text = "Don't have an account yet?",
            color=Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(top= 520.dp,start=90.dp)
            )
        Button(
            onClick = {navController.navigate("signup_screen")},
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.darkpurple)
            ),
            modifier = Modifier
                .padding(top = 560.dp, start = 65.dp)
                .size(width = 300.dp, height = 45.dp),
        ) {
            Text(text = "Join MoodTrackr", color = Color.White, fontSize = 18.sp)
        }
    }
}
