package com.example.mobile // Make sure to include your package name

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
    var checked by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf(value = "") }
    var username by remember { mutableStateOf(value = "") }
    var showPassword by remember { mutableStateOf(value="") }
    var email by remember { mutableStateOf(value = "") }
    var full_Name by remember { mutableStateOf(value = "") }
    var confirmPassword by remember { mutableStateOf("") }

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color.Black)
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            tint = Color.White,
            contentDescription = stringResource(id = R.string.arrow_back_content_desc),
            modifier=Modifier.padding(top=50.dp,start=20.dp)
                .clickable { navController.navigate("main_page") }
                .size(37.dp)

        )
        //hello

        Text(
            text = "GET STARTED",
            color = colorResource(R.color.lightpurple),
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 140.dp, start = 60.dp)
        )

        TextField(
            value = full_Name,
            maxLines = 1,
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = {full_Name = it },
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

        TextField(
            value = email,
            maxLines = 1,
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = {email= it },
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
                .padding(top = 410.dp, start = 40.dp, end = 40.dp) // Added end padding for consistency
                .fillMaxWidth() ,
        )


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
            modifier = Modifier.padding(top = 470.dp, start = 40.dp,end=40.dp)
                .fillMaxWidth() ,
        )

        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            Text(
                text="Password does not match",
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 550.dp,start=45.dp)
            )
        }


        Text(
            //make the terms and conditions bold
            //find out how to do that
            //add a link to terms and conditions
            text="I agree with the Terms & Conditions",
            color = colorResource(R.color.lightpurple),
            fontWeight = FontWeight.Bold,
            modifier=Modifier.padding(top=578.dp,start=80.dp, end = 45.dp, bottom =10.dp)

        )
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it },
                        colors = CheckboxDefaults.colors(
                        checkedColor = colorResource(R.color.darkpurple),
                        uncheckedColor = colorResource(R.color.darkpurple)
            ),
                                modifier = Modifier.padding(top=570.dp,start =40.dp)

            )
            //add checkbox next to terms and conditions
            Button(
                onClick = {},
                //should lead to reminder and goal page
                //navController.navigate("goal_page")
                //navController.navigate("
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.darkpurple)),

                modifier = Modifier
                    .padding(top = 620.dp, start = 100.dp)
                    .size(width = 200.dp, height = 45.dp),
            ) {
                    Text(text = "Sign up", color = Color.White, fontSize = 22.sp)

                }
        }
}      //add checkbox right next to the terms & conditions
// color = colorResource(R.color.darkpurple),



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MobileTheme {
        SignUp(navController=rememberNavController
                ())
    }
}
