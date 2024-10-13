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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import com.example.mobile.ui.theme.MobileTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.painterResource

@Composable
fun MoodPage(){
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color.Black)
    ) {
    
    }

    Text(
        text ="How are you feeling today?",
        color=Color.White,
        modifier= Modifier.padding(top=150.dp,start=21.dp),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
    )
    //add the emojis make it clickable this should be stored
   //add to reminder page
    // Image(
//        painter = painterResource(id = R.drawable.chill),
//        modifier=Modififer()
//
//    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MoodPagePreview() {
    MobileTheme {
        MoodPage()
    }
}
