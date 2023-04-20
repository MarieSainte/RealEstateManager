package com.example.realestatemanager.ui.navigation

sealed class NavigationItem(val route: String){

    object MainScreen: NavigationItem("main_screen")
    object ListScreen: NavigationItem("list_screen")
    object MapScreen: NavigationItem("map_screen")
    object SimulatorScreen: NavigationItem("simulator_screen/{price}")
    object DetailsScreen: NavigationItem("details_screen/{housingId}")
}
