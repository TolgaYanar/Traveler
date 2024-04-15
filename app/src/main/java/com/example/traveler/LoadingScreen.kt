package com.example.traveler

import android.widget.Space
import android.widget.TextView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun LoadingScreen(navController: NavController){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF018786)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        item {
            Image(painter = painterResource(id = R.drawable.traveler), contentDescription = null,
                modifier = Modifier
                    .height(250.dp)
                    .width(250.dp), alignment = Alignment.TopStart)
            Spacer(modifier = Modifier.height(100.dp))
            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "For everyone who wants to travel in new, responsible ways",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White,
                        textAlign = TextAlign.Center)
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = { navController.navigate(Screen.RegisterScreen.route) },
                modifier = Modifier.padding(16.dp).size(300.dp,75.dp), shape = CircleShape) {
                Text(text = "Get Started", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold,
                    color = Color.White, fontSize = 23.sp, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
