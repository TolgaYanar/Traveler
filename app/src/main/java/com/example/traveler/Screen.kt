package com.example.traveler

sealed class Screen(val route: String) {
    object LoadingScreen: Screen("loading_screen")
    object RegisterScreen: Screen("register_screen")
    object LoginScreen: Screen("login_screen")
    object MainMenuScreen: Screen("main_menu_screen")
    object CityInformationScreen: Screen("city_information_screen")
    object UserProfileScreen: Screen("user_profile_screen")
    object EditProfileScreen: Screen("edit_profile_screen")
    object FavoritesScreen: Screen("favorites_screen")
    object NotificationsScreen: Screen("notifications_screen")
    object TripPlanJournalScreen: Screen("trip_plan_journal_screen")
    object TripPlanTodaysPlanScreen: Screen("trip_plan_todays_plan_screen")
    object AddTaskScreen: Screen("add_task_screen")
    object AddJournalScreen: Screen("add_journal_screen")
    object RecentTripScreen: Screen("recent_trip_screen")
    object AddNotesScreen: Screen("add_notes_screen")
    object MessageRoom: Screen("message_room")

}