package com.example.traveler

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NotificationsScreen(navController: NavController) {

    val bottomNavClass = BottomNavigationClass()
    bottomNavClass.selectedItemIndex = 2

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Notifications", color = Color.Black,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .heightIn(max = 24.dp)
                        .alpha(0.59f), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }, elevation = 3.dp,
                backgroundColor = colorResource(id = R.color.app_bar_color),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.UserProfileScreen.route) }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_person_outline_24), contentDescription = null,
                            modifier = Modifier.size(30.dp,30.dp))
                    }
                    IconButton(onClick = { navController.navigate(Screen.UserProfileScreen.route) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null,
                            modifier = Modifier.size(30.dp,30.dp))
                    }
                })
        },
        bottomBar = {
            NavigationBar {
                bottomNavClass.items.forEachIndexed { index, item ->
                    NavigationBarItem(selected = bottomNavClass.selectedItemIndex == index,
                        onClick = {
                            bottomNavClass.selectedItemIndex = index
                            if(item.title != "Notifications") navController.navigate(item.route)
                        },
                        label = {
                            Text(text = item.title)
                        }, alwaysShowLabel = false,
                        icon = {
                            BadgedBox(badge = {
                                if(item.badgeCount != null) {
                                    Badge {
                                        Text(text = item.badgeCount.toString())
                                    }
                                }
                            }) {
                                Icon(imageVector = if(index == bottomNavClass.selectedItemIndex) item.selectedIcon
                                else item.unselectedIcon, contentDescription = null)
                            }
                        })
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {


        }
    }
}