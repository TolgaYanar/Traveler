package com.example.traveler

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.traveler.data.City
import com.example.traveler.data.Journal
import com.example.traveler.data.User

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    authenticationViewModel: AuthenticationViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
    countryViewModel: CountryViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    tourismViewModel: TourismViewModel = viewModel(),
    journalPropertiesViewModel: JournalPropertiesViewModel = viewModel(),
    context: Context
){
    NavHost(navController = navController, startDestination = "login_flow"){

        navigation(startDestination = Screen.LoadingScreen.route, route = "login_flow"){
            composable(Screen.LoadingScreen.route){
                LoadingScreen(navController)
            }
            composable(Screen.RegisterScreen.route){
                RegisterScreen(navController, authenticationViewModel, context = context,
                    profileViewModel = profileViewModel)
            }

            composable(Screen.LoginScreen.route){
                LoginScreen(navController, authenticationViewModel, profileViewModel)
            }
        }

        composable(Screen.MainMenuScreen.route){
            MainMenu(navController = navController, profileViewModel = profileViewModel,
                countryViewModel = countryViewModel)
        }

        composable(Screen.UserProfileScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
            if(result != null){
                UserProfileScreen(navController, user = result, authenticationViewModel = authenticationViewModel,
                    journalPropertiesViewModel = journalPropertiesViewModel,
                    profileViewModel = profileViewModel)
            }
            else{
                UserProfileScreen(navController, authenticationViewModel = authenticationViewModel,
                    journalPropertiesViewModel = journalPropertiesViewModel,
                    profileViewModel = profileViewModel)
            }
        }

        composable(Screen.RecentTripScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
            if (result != null && user != null) {
                RecentTripScreen(navController = navController, journal = result, user = user,
                    journalPropertiesViewModel)
            }
        }
        
        composable(Screen.EditProfileScreen.route){
            EditProfileScreen(navController = navController, authenticationViewModel = authenticationViewModel,
                profileViewModel = profileViewModel)
        }
        
        composable(Screen.FavoritesScreen.route){
            FavoritesScreen(navController, profileViewModel)
        }

        composable(Screen.NotificationsScreen.route){
            NotificationsScreen(navController = navController, journalPropertiesViewModel = journalPropertiesViewModel)
        }

        composable(Screen.TripPlanTodaysPlanScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            if(result != null)
                TripPlanTodaysPlanScreen(journal = result, navController = navController,
                    journalPropertiesViewModel = journalPropertiesViewModel)
        }

        composable(Screen.TripPlanJournalScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            if (result != null) {
                TripPlanJournalScreen(navController = navController, journal = result,
                    journalPropertiesViewModel = journalPropertiesViewModel,
                    profileViewModel = profileViewModel)
            }
        }

        composable(Screen.AddNotesScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            if (result != null) {
                AddNotesScreen(navController = navController, journal = result,
                    journalPropertiesViewModel = journalPropertiesViewModel)
            }
        }
        
        composable(Screen.AddTaskScreen.route){
            val thatDay = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("thatday")
            val journal = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            val dayNum = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("daynumber")

            if (journal != null && thatDay != null && dayNum != null) {
                AddTaskScreen(navController = navController, thatDay = thatDay, journal = journal,
                    dayNumber = dayNum, journalPropertiesViewModel = journalPropertiesViewModel)
            }
        }

        composable(Screen.AddJournalScreen.route){
            val location = navController.previousBackStackEntry?.savedStateHandle?.get<String>("location")
            if (location != null) {
                AddJournal(navController = navController, journalPropertiesViewModel = journalPropertiesViewModel,
                    profileViewModel = profileViewModel, loc = location)
            }
            else AddJournal(navController = navController, journalPropertiesViewModel = journalPropertiesViewModel,
                profileViewModel = profileViewModel)
        }

        composable(Screen.CityInformationScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<City>("city")

            if (result != null) {
                CityInformationScreen(city = result, navController = navController,
                    journalPropertiesViewModel = journalPropertiesViewModel,
                    tourismViewModel = tourismViewModel,
                    weatherViewModel = weatherViewModel)
            }
        }

        composable(Screen.MessageRoom.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<User>("friend")
            if(result != null){
                ChatScreen(friend = result, profileViewModel = profileViewModel)
            }
        }
    }
}