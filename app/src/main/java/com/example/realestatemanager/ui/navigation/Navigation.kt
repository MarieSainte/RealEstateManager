package com.example.realestatemanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.realestatemanager.ui.MainActivity
import com.example.realestatemanager.ui.main.MainScreen
import com.example.realestatemanager.ui.main.MainViewModel

@Composable
fun Navigation(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    mainActivity: MainActivity
) {

    NavHost(navController = navController, startDestination = NavigationItem.MainScreen.route) {

        composable(NavigationItem.MainScreen.route) {
            MainScreen(mainViewModel = mainViewModel, navController = navController)
        }

    }
}