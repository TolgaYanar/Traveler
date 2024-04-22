package com.example.traveler


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.traveler.data.Injection

@Composable
fun FavoritesScreen(navController: NavController, profileViewModel: ProfileViewModel) {

    val bottomNavClass = BottomNavigationClass()
    bottomNavClass.selectedItemIndex = 1

    LaunchedEffect(key1 = profileViewModel){
        profileViewModel.loadFeaturesOfUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Favorites", color = Color.Black,
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
                })
        },
        bottomBar = {
            NavigationBar {
                bottomNavClass.items.forEachIndexed { index, item ->
                    NavigationBarItem(selected = bottomNavClass.selectedItemIndex == index,
                        onClick = {
                            bottomNavClass.selectedItemIndex = index
                            if(item.title != "Favorites") navController.navigate(item.route)
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
        LazyVerticalGrid(modifier = Modifier
            .padding(it)
            .fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.Top, columns = GridCells.Fixed(2))
        {
            profileViewModel.favoriteCountries.value.let {

                if(it != null){

                    items(it){city->

                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 20.dp,
                                focusedElevation = 30.dp
                            ),
                            modifier = Modifier
                                .background(Color.Transparent)
                                .width(160.dp)
                                .height(140.dp)
                                .padding(8.dp)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        key = "city",
                                        value = city
                                    )
                                    navController.navigate(Screen.CityInformationScreen.route)
                                }
                        ) {
                            Box(modifier = Modifier.background(Color.Transparent)){

                                AsyncImage(model = city.imageUrl, contentDescription = null,
                                    contentScale = ContentScale.Crop)
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .background(Color.Transparent),
                                    contentAlignment = Alignment.BottomStart){
                                    Text(text = city.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                                }
                                var favorite by remember {
                                    mutableStateOf(true)
                                }

                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                    contentAlignment = Alignment.TopEnd){
                                    if(favorite == false){
                                        Icon(painter = painterResource(id = R.drawable.baseline_bookmark_border_24), contentDescription = null)
                                    }else{
                                        Icon(painter = painterResource(id = R.drawable.baseline_bookmark_24), contentDescription = null,
                                            modifier = Modifier.clickable {
                                            profileViewModel.currentUser.value?.let { user ->
                                                Injection.instance().collection("users").document(
                                                    user.uid).collection("favorites").document(city.name).delete()
                                                    .addOnSuccessListener {
                                                        favorite = !favorite
                                                        println("Document deleted successfully")
                                                    }
                                                    .addOnFailureListener {
                                                        println("error occured while trying to delete favorite country")
                                                    }
                                                }
                                                navController.navigate(Screen.FavoritesScreen.route)
                                            }, tint = Color.Yellow
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}