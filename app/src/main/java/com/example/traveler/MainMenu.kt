package com.example.traveler


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.traveler.data.Country
import com.example.traveler.data.Injection
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await


@Composable
fun MainMenu(profileViewModel: ProfileViewModel = viewModel(), navController: NavController,
             countryViewModel: CountryViewModel = viewModel()){

    val bottomNavClass = BottomNavigationClass()

    val user by profileViewModel.currentUser.observeAsState()

    val firestore = Injection.instance()



    Scaffold(
        topBar = {
            TopAppBar(title = {


                Text(text = "Hello, ${user?.fullName}!", color = Color.Black,
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
                            if(item.title != "Home") navController.navigate(item.route)
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
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(it)
            .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top)
        {

            item {
                if(countryViewModel.cityList.isNotEmpty()){
                    Image(painter = painterResource(id = R.drawable.specialofferimage), contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .padding(horizontal = 40.dp)
                            .size(250.dp))

                    Row(modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Text(text = "Explore Cities", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(0.6f))
                    }


                    LazyRow {
                        items(countryViewModel.cityList){city->

                            Card(
                                backgroundColor = Color.Transparent,
                                modifier = Modifier
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
                                        mutableStateOf(false)
                                    }

                                    user?.let { it1 ->
                                        firestore.collection("users").document(it1.uid)
                                            .collection("favorites").document(city.name).get().addOnSuccessListener {
                                                if(it.exists()) favorite = true
                                            }
                                    }

                                    val hashMapOfCountry = hashMapOf<String, Any>(
                                        "name" to city.name,
                                        "country" to city.country,
                                        "latitude" to city.latitude,
                                        "longitude" to city.longitude,
                                        "imageUrl" to city.imageUrl
                                    )
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                        contentAlignment = Alignment.TopEnd){
                                        if(favorite == false){
                                            Icon(painter = painterResource(id = R.drawable.baseline_bookmark_border_24), contentDescription = null,
                                                modifier = Modifier.clickable {
                                                    user?.let { it1 ->
                                                        firestore.collection("users").document(
                                                            it1.uid).collection("favorites").document(city.name).set(hashMapOfCountry, SetOptions.merge())
                                                            .addOnSuccessListener {
                                                                favorite = !favorite
                                                                println("Document updated successfully")
                                                            }
                                                            .addOnFailureListener {
                                                                println("error occured while trying to add favorite country")
                                                            }
                                                    }
                                                })
                                        }else{
                                            Icon(painter = painterResource(id = R.drawable.baseline_bookmark_24), contentDescription = null,
                                                modifier = Modifier.clickable {
                                                    user?.let { it1 ->
                                                        firestore.collection("users").document(
                                                            it1.uid).collection("favorites").document(city.name).delete()
                                                            .addOnSuccessListener {
                                                                favorite = !favorite
                                                                println("Document deleted successfully")
                                                            }
                                                            .addOnFailureListener {
                                                                println("error occured while trying to delete favorite country")
                                                            }
                                                    }
                                                }, tint = Color.Yellow)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    CircularProgressIndicator()
                }
            }
        }
    }
    
}

