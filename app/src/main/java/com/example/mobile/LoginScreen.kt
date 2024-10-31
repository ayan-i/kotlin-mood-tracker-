package com.example.mobile

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.Button
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextField
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import com.example.mobile.ui.theme.MobileTheme
import com.google.android.material.button.MaterialButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MainPage(navController)
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var showPassword by remember { mutableStateOf(value = false) }
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {





        Text(
            text = "WELCOME BACK!",
            color = colorResource(R.color.lightpurple),
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 140.dp, start = 50.dp)
        )

        TextField(
            value = username,
            maxLines = 1,
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = { username = it },
            placeholder = {
                Text(text = "Username", color = Color.Gray, fontSize = 16.sp)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White,
            ),
            modifier = Modifier
                .padding(top = 260.dp, start = 40.dp, end = 40.dp)
                .fillMaxWidth()

        )

        TextField(
            value = password,
            onValueChange = { newText -> password = newText },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .padding(top = 320.dp, start = 40.dp,end=40.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Password", color = Color.Gray, fontSize = 16.sp)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White,

                ),
            trailingIcon = {
                Text(
                    text = if (showPassword) "Hide" else "Show",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showPassword = !showPassword }
                )
            }
        )




        Button(
            //come back to this add xml layout folder and activity_layout file
            // this is what you should write
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.darkpurple)),

            modifier = Modifier.padding(top = 430.dp, start = 100.dp)
                .size(width = 200.dp, height = 45.dp),
        ) {
            Text(text = "Login", color = Color.White, fontSize = 22.sp)

        }
    }
}      //add checkbox right next to the terms & conditions
// color = colorResource(R.color.darkpurple),
//modifier=modifier.padding(top=460.dp,start=45.dp, end = 45.dp, bottom =10.dp)



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MobileTheme {
        LoginScreen(navController = rememberNavController())
    }
}
