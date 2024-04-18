package com.example.traveler

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.traveler.data.Country
import com.example.traveler.data.Journal
import com.example.traveler.data.User

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    authenticationViewModel: AuthenticationViewModel = viewModel(),
    context: Context
){
    NavHost(navController = navController, startDestination = "login_flow"){

        navigation(startDestination = Screen.LoadingScreen.route, route = "login_flow"){
            composable(Screen.LoadingScreen.route){
                LoadingScreen(navController)
            }
            composable(Screen.RegisterScreen.route){
                RegisterScreen(navController, authenticationViewModel, context)
            }

            composable(Screen.LoginScreen.route){
                LoginScreen(navController, authenticationViewModel)
            }
        }

        composable(Screen.MainMenuScreen.route){
            MainMenu(navController = navController)
        }

        composable(Screen.UserProfileScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
            if(result != null){
                UserProfileScreen(navController, user = result)
            }
            else{
                UserProfileScreen(navController)
            }
        }

        composable(Screen.RecentTripScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
            if (result != null && user != null) {
                RecentTripScreen(navController = navController, journal = result, user = user)
            }
        }
        
        composable(Screen.EditProfileScreen.route){
            EditProfileScreen(navController = navController)
        }
        
        composable(Screen.FavoritesScreen.route){
            FavoritesScreen(navController)
        }

        composable(Screen.NotificationsScreen.route){
            NotificationsScreen(navController = navController)
        }

        composable(Screen.TripPlanTodaysPlanScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            if(result != null)
                TripPlanTodaysPlanScreen(journal = result, navController = navController)
        }

        composable(Screen.TripPlanJournalScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            if (result != null) {
                TripPlanJournalScreen(navController = navController, journal = result)
            }
        }

        composable(Screen.AddNotesScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            if (result != null) {
                AddNotesScreen(navController = navController, journal = result)
            }
        }
        
        composable(Screen.AddTaskScreen.route){
            val thatDay = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("thatday")
            val journal = navController.previousBackStackEntry?.savedStateHandle?.get<Journal>("journal")
            val dayNum = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("daynumber")

            if (journal != null && thatDay != null && dayNum != null) {
                AddTaskScreen(navController = navController, thatDay = thatDay, journal = journal,
                    dayNumber = dayNum)
            }
        }

        composable(Screen.AddJournalScreen.route){
            AddJournal(navController = navController)
        }

        composable(Screen.CityInformationScreen.route){
            val result = navController.previousBackStackEntry?.savedStateHandle?.get<Country>("country")

            if (result != null) {
                CityInformationScreen(country = result, navController = navController)
            }
        }
    }
}