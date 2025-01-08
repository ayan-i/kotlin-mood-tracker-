package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.Button
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextField
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
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
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

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
//function to save userID to SharedPreferences
fun saveUserIdToPreferences(context: Context, userId: String) {

    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("userId", userId).apply()
}
//function to handle user login
fun login(context: Context, inputUsername: String, inputPassword: String): String? {
    return try {
        //open login file
        val fileInputStream: FileInputStream = context.openFileInput("login.txt")
        val lines = fileInputStream.bufferedReader().readLines()
        //read all the lines and then go through each file
        for (line in lines) {
            //split into parts
            val parts = line.split(",")
            if (parts.size >= 4) {
                //only call the userID ,password and username to be checked
                val userId = parts[0].trim()
                val username = parts[2].trim()
                val password = parts[3].trim()

                // Check if credentials match
                if (username == inputUsername && password == inputPassword) {
                    UserSession.userId = userId // Save to UserSession
                    return userId // Return userId on success
                }
            }
        }
        null
    } catch (e: IOException) {
        Log.e("Login", "Error reading login.txt file", e)
        null
    }
}


@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current // Retrieve the current context
    var username by remember { mutableStateOf(value = "") }  // State for username
    var password by remember { mutableStateOf(value = "") }  // State for password
    var showPassword by remember { mutableStateOf(value = false) } //state for showing password
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
            onValueChange = { username = it },  // Update username state on input
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
        //Textfield for password input
        TextField(
            value = password,
            onValueChange = { newText -> password = newText },// Update password state on input
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            // Toggle password visibility
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
                    // Toggle password visibility with a clickable text
                    modifier = Modifier.clickable { showPassword = !showPassword }
                )
            }
        )




        Button(
            onClick = {
                val userId = login(context,username,password) // Attempt login
                if (userId != null) {
                    Toast.makeText(context,"Login successful!",Toast.LENGTH_SHORT).show()
                    saveUserIdToPreferences(context, userId) // Save userId in SharedPreferences
                    navController.navigate("overview_screen") //Navigate to overview screen
                } else {
                    //Error message
                    Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.darkpurple)
            ),
            modifier = Modifier
                .padding(top = 430.dp, start = 100.dp)
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

//hello
