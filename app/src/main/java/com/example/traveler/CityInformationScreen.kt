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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.traveler.data.Country
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale

@Composable
fun CityInformationScreen(country: Country,
                          navController: NavController,
                          tourismViewModel: TourismViewModel = viewModel(),
                          weatherViewModel: WeatherViewModel = viewModel()
                          ){

    LaunchedEffect(key1 = true){
        tourismViewModel.fetchData(country)
        weatherViewModel.fetchData(country)
    }

    val tourism_data by tourismViewModel.tourismData.observeAsState(emptyList())

    val catering_data by tourismViewModel.restaurantData.observeAsState(emptyList())

    val otherUsers =  remember { mutableListOf<User>() }

    LaunchedEffect(key1 = true){
        getRandomUsersWithMatchingLocation(1,country.capital,otherUsers)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }, title = { Text(text = country.capital, fontSize = 24.sp, fontWeight = FontWeight.Bold)},
                backgroundColor = colorResource(id = R.color.app_bar_color)
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize())
        {
           if(tourism_data.isNotEmpty() && catering_data.isNotEmpty()){
               Card(modifier = Modifier
                   .padding(30.dp)
                   .height(140.dp)
                   .fillMaxWidth(),
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
               Card(modifier = Modifier
                   .padding(horizontal = 30.dp)
                   .height(325.dp)
                   .fillMaxWidth(),
                   colors = CardDefaults.cardColors(
                       //containerColor = Color.Transparent
                   )
               ) {
                   Text(text = "Touristic Destinations", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                       modifier = Modifier.padding(horizontal = 16.dp))
                   for (destination in tourism_data){
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
                   for (restaurant in catering_data){
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

               Text(text = "Explore Journals", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                   modifier = Modifier.padding(horizontal = 30.dp).padding(top = 10.dp).alpha(0.6f))

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
                               navController.currentBackStackEntry?.savedStateHandle?.set("user", it)
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


fun formattedDouble(doubleValue: Double?): String {
    return String.format(Locale.getDefault(), "%.1f", doubleValue)
}

// Function to get the resource ID of an image based on its name
private fun getDrawableResourceId(imageName: String): Int {
    return try {
        val resId = R.drawable::class.java.getField(imageName).getInt(null)
        resId
    } catch (e: Exception) {
        e.printStackTrace()
        0 // Return 0 if resource not found
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun getRandomUsersWithMatchingLocation(number : Int, location : String, list : MutableList<User>){

    GlobalScope.launch {

        val usersID = mutableListOf<String>()
        val users = mutableListOf<String>()
        val firestore = Injection.instance()

        val usersCollection = firestore.collection("users")
        val usersCollectionSnapshot = usersCollection.get().await()

        usersCollectionSnapshot?.forEach { document->
            val userId = document.reference.id
            if (!usersID.contains(userId) && userId !=  FirebaseAuth.getInstance().uid) {
                usersID.add(userId)
                if(usersID.size == number){
                    return@forEach
                }
            }
        }

        for (id in usersID){
            val journalsShapshot = usersCollection.document(id).collection("journals").get().await()

            journalsShapshot.forEach { journal->
                val journalRef = journal.toObject(Journal::class.java)

                val currentDate = Calendar.getInstance().time.time
                val journalEndDate = journalRef.endDateInMillis
                if(journalRef.location.contains(location) && !journalRef.private && currentDate>journalEndDate){
                    if(!users.contains(id)){
                        users.add(id)
                    }
                }
            }
        }
        list.shuffle()
        users.forEach { userID->
            val user = usersCollection.document(userID).get().await().toObject(User::class.java)
            if (user != null) {
                list.add(user)
            }
        }
    }
}