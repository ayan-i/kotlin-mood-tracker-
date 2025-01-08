package com.example.mobile // Make sure to include your package name

import android.content.Context
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import java.util.UUID
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.example.mobile.ui.theme.MobileTheme
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.FileOutputStream
import java.io.IOException

//class activity for signup
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SignUp(navController)
        }
    }
}
@Composable
fun SignUp(navController: NavController) {
    val context = LocalContext.current
    var checked by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf(value = "") } // Remember the checkbox state
    var username by remember { mutableStateOf(value = "") } // // Remember the username state
    var showPassword by remember { mutableStateOf(value = "") }
    var email by remember { mutableStateOf(value = "") } // Remember the email state
    var full_Name by remember { mutableStateOf(value = "") } // Remember the full name state
    var confirmPassword by remember { mutableStateOf("") } // Remember the confirm Password  state

    //set the system bar colour to transparent
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color.Black)
    ) {

        Text(
            text = "GET STARTED",
            color = colorResource(R.color.lightpurple),
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 140.dp, start = 60.dp)
        )

        // full name textfield
        TextField(
            value = full_Name,
            maxLines = 1,
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = { full_Name = it },//saved what is written
            placeholder = {
                Text(text = "Name", color = Color.Gray, fontSize = 16.sp)
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
        //email input field
        TextField(
            value = email,
            maxLines = 1,
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = { email = it },
            placeholder = {
                Text(text = "Email", color = Color.Gray, fontSize = 16.sp)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White,
            ),
            modifier = Modifier
                .padding(top = 310.dp, start = 40.dp, end = 40.dp)
                .fillMaxWidth()

        )
        // Username input field
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
                .padding(top = 360.dp, start = 40.dp, end = 40.dp)
                .fillMaxWidth()

        )

        // Password input field
        TextField(
            value = password,
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation(),
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = { newText -> password = newText },
            placeholder = {
                Text(text = "Password", color = Color.Gray, fontSize = 16.sp)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White,

                ),
            modifier = Modifier
                .padding(
                    top = 410.dp,
                    start = 40.dp,
                    end = 40.dp
                ) // Added end padding for consistency
                .fillMaxWidth(),
        )

        // Confirm password input field
        TextField(
            value = confirmPassword,
            textStyle = TextStyle(fontSize = 20.sp),
            maxLines = 1,
            onValueChange = { newText -> confirmPassword = newText },
            visualTransformation = PasswordVisualTransformation(),

            placeholder = {
                Text(
                    text = "Confirm Password",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier.padding(top = 470.dp, start = 40.dp, end = 40.dp)
                .fillMaxWidth(),
        )
        //Password mismatch message
        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            Text(
                text = "Password does not match",
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 550.dp, start = 45.dp)
            )
        }

        //I agree with the Terms & Conditions
        Text(
            text = "I agree with the Terms & Conditions",
            color = colorResource(R.color.lightpurple),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 578.dp, start = 80.dp, end = 45.dp, bottom = 10.dp)

        )
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = CheckboxDefaults.colors(
                checkedColor = colorResource(R.color.darkpurple),
                uncheckedColor = colorResource(R.color.darkpurple)
            ),
            modifier = Modifier.padding(top = 570.dp, start = 40.dp)

        )
        Button(
            onClick = {
                try {
                    // Save the new user data
                    val existingUsers = mutableListOf<String>()
                    val file = context.getFileStreamPath("login.txt")
                    // Read existing usernames if file exists
                    if (file.exists()) {
                        context.openFileInput("login.txt").use { fis ->
                            fis.bufferedReader().useLines { lines ->
                                //read lines of the txt file
                                lines.forEach { line ->
                                    val userDetails = line.split(",")
                                    if (userDetails.size > 2) {
                                        // Ensure the line has at least 3 parts
                                        existingUsers.add(userDetails[2])
                                        // Add the username (3rd element) to the list of existing users
                                    }
                                }
                            }
                        }
                    }
                    //validation for all Textfields
                    when {
                        full_Name.isBlank() -> {
                            Toast.makeText(context, "Name cannot be blank.", Toast.LENGTH_SHORT).show()
                        }
                        !email.contains("@") -> {
                            Toast.makeText(context, "Invalid email address.", Toast.LENGTH_SHORT).show()
                        }
                        username.isBlank() -> {
                            Toast.makeText(context, "Username cannot be blank.", Toast.LENGTH_SHORT).show()
                        }
                        password.isBlank() -> {
                            Toast.makeText(context, "Password cannot be blank.", Toast.LENGTH_SHORT).show()
                        }
                        confirmPassword.isBlank() -> {
                            Toast.makeText(context, "Confirm password cannot be blank.", Toast.LENGTH_SHORT).show()
                        }
                        username in existingUsers -> {
                        Toast.makeText(context, "Username already exists.", Toast.LENGTH_SHORT).show()
                        }

                        !checked -> {
                            Toast.makeText(
                                context,
                                "Please agree to the Terms & Conditions.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                        else -> {
                            // Save the new user data
                            val userId = UUID.randomUUID().toString()
                            val fos: FileOutputStream =
                                context.openFileOutput("login.txt", Context.MODE_APPEND)
                            // Save this parameters
                            val entry = "$userId,$email,$username,$password\n"
                            fos.write(entry.toByteArray())
                            fos.flush()
                            fos.close()
                            //success message and goes to login page
                            Toast.makeText(context, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate("login_screen")
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "An error occurred.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.darkpurple)
            ),
            modifier = Modifier
                .padding(top = 620.dp, start = 100.dp)
                .size(width = 200.dp, height = 45.dp),
        ) {
            Text(text = "Sign up", color = Color.White, fontSize = 22.sp)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MobileTheme {
        SignUp(navController=rememberNavController
                ())
    }
}
//hello1