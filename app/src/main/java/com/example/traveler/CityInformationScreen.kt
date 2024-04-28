package com.example.traveler

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.traveler.data.City
import com.example.traveler.data.User
import java.util.Locale

@Composable
fun CityInformationScreen(city: City,
                          navController: NavController,
                          tourismViewModel: TourismViewModel,
                          weatherViewModel: WeatherViewModel,
                          journalPropertiesViewModel: JournalPropertiesViewModel
                          ){

    LaunchedEffect(key1 = true){
        tourismViewModel.fetchData(city)
        weatherViewModel.fetchData(city)
    }

    val tourism_data by tourismViewModel.tourismData.observeAsState(emptyList())

    val catering_data by tourismViewModel.restaurantData.observeAsState(emptyList())

    val otherUsers =  remember { mutableListOf<User>() }

    var fiveDayForecast by remember {
        mutableStateOf(mutableStateOf(false))
    }

    LaunchedEffect(key1 = true){
        journalPropertiesViewModel.getRandomUsersWithMatchingLocation(1,city.name,otherUsers)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        tourismViewModel.clearData()
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }, title = { Text(text = city.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)},
                backgroundColor = colorResource(id = R.color.app_bar_color)
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize())
        {
           if(!tourism_data.isNullOrEmpty() && !catering_data.isNullOrEmpty()){
               Card(modifier = Modifier
                   .padding(30.dp)
                   .height(140.dp)
                   .fillMaxWidth()
                   .clickable {
                       fiveDayForecast.value = true
                   },
                   border = BorderStroke(2.dp, Color.Black),
                   colors = CardDefaults.cardColors(
                       containerColor = Color.White
                   ),
                   elevation = CardDefaults.cardElevation(
                       defaultElevation = 6.dp
                   )
               ) {
                   Box(modifier = Modifier
                       .fillMaxSize(), contentAlignment = Alignment.CenterStart)
                   {
                       weatherViewModel.weatherData.value?.weather?.get(0)
                           ?.let {Image(painter = painterResource(id = getDrawableResourceId("icon${weatherViewModel.weatherData.value?.weather?.get(0)?.icon}")), contentDescription = null,
                               modifier = Modifier.size(90.dp))}
                       Text(text = weatherViewModel.weatherData.value?.main?.temp?.toInt().toString(), fontSize = 35.sp, modifier = Modifier
                           .padding(horizontal = 90.dp)
                           .padding(end = 70.dp))
                       Image(painter = painterResource(id = R.drawable.centigrade), contentDescription = null,
                           modifier = Modifier
                               .padding(start = 132.dp)
                               .size(30.dp))
                       Box(modifier = Modifier
                           .fillMaxSize(), contentAlignment = Alignment.TopEnd)
                       {
                           weatherViewModel.weatherData.value?.weather?.get(0)
                               ?.let { it1 -> Text(text = it1.description, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 15.dp, vertical = 50.dp)) }
                           Text(text = "RealFeel " + formattedDouble(doubleValue = weatherViewModel.weatherData.value?.main?.feels_like), fontSize = 14.sp, modifier = Modifier
                               .padding(horizontal = 15.dp)
                               .padding(top = 78.dp))
                       }
                   }
               }

               val allowedIndexes by remember {
                   mutableStateOf(mutableListOf(0,8,16,24,32))
               }

               if(fiveDayForecast.value){
                   weatherViewModel.FiveDayForecast(
                       onDismissRequest = {
                           fiveDayForecast.value = false
                       },
                       allowedIndexes = allowedIndexes,
                       fiveDayForecast
                   )
               }

               Card(modifier = Modifier
                   .padding(horizontal = 30.dp)
                   .height(325.dp)
                   .fillMaxWidth(),
                   colors = CardDefaults.cardColors(
                       //containerColor = Color.Transparent
                   )
               ) {
                   LazyColumn(modifier = Modifier.fillMaxSize()){
                       item {
                           Text(text = "Touristic Destinations", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                               modifier = Modifier.padding(horizontal = 16.dp))
                           for (destination in tourism_data!!){
                               Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top, modifier = Modifier
                                   .fillMaxWidth()
                                   .padding(vertical = 4.dp)) {
                                   Canvas(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp), onDraw = {
                                       drawCircle(color = Color.Black, radius = 10f)
                                   })
                                   Text(text = "${destination.properties.name} - ${destination.properties.city}",
                                       fontSize = 14.sp, modifier = Modifier)
                               }
                           }
                           Text(text = "Restaurants", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                               modifier = Modifier.padding(horizontal = 16.dp))
                           for (restaurant in catering_data!!){
                               Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top, modifier = Modifier
                                   .fillMaxWidth()
                                   .padding(vertical = 4.dp)) {
                                   Canvas(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp), onDraw = {
                                       drawCircle(color = Color.Black, radius = 10f)
                                   })
                                   Text(text = "${restaurant.properties.name} - ${restaurant.properties.district},${restaurant.properties.city}",
                                       fontSize = 14.sp, modifier = Modifier)
                               }
                           }
                       }
                   }
               }

               Text(text = "Explore Journals", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                   modifier = Modifier
                       .padding(horizontal = 30.dp)
                       .padding(top = 10.dp)
                       .alpha(0.6f))

               LazyColumn(modifier = Modifier
                   .fillMaxSize()
                   .padding(horizontal = 30.dp),
                   horizontalAlignment = Alignment.CenterHorizontally)
               {

                   items(otherUsers){
                       
                       Card(modifier = Modifier
                           .padding(vertical = 10.dp)
                           .height(90.dp)
                           .fillMaxWidth()
                           .clickable {
                               navController.currentBackStackEntry?.savedStateHandle?.set(
                                   "user",
                                   it
                               )
                               navController.navigate(Screen.UserProfileScreen.route)
                           }
                       ) {
                           Box(modifier = Modifier.fillMaxSize()){
                               AsyncImage(model = it.profile_image, contentDescription = null,
                                   contentScale = ContentScale.Crop)
                               Box(modifier = Modifier
                                   .fillMaxSize()
                                   .padding(8.dp)
                                   .background(Color.Transparent),
                                   contentAlignment = Alignment.BottomStart) {
                                   Text(text = it.fullName, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
                               }
                           }
                       }
                   }
               }
               
           }else{
               CircularProgressIndicator(modifier = Modifier
                   .fillMaxSize()
                   .wrapContentSize(Alignment.Center))
           }
        }
    }
}

private fun formattedDouble(doubleValue: Double?): String {
    return String.format(Locale.getDefault(), "%.1f", doubleValue)
}

// Function to get the resource ID of an image based on its name
fun getDrawableResourceId(imageName: String): Int {
    return try {
        val resId = R.drawable::class.java.getField(imageName).getInt(null)
        resId
    } catch (e: Exception) {
        e.printStackTrace()
        0 // Return 0 if resource not found
    }
}
