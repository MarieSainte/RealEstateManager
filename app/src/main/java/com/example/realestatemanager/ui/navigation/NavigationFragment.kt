package com.example.realestatemanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.realestatemanager.ui.details.DetailsScreen
import com.example.realestatemanager.ui.main.ListScreen
import com.example.realestatemanager.ui.main.MainViewModel
import com.example.realestatemanager.ui.main.MapScreen
import com.example.realestatemanager.ui.simulator.SimulatorScreen

@Composable
fun NavigationFragment(
    mainViewModel: MainViewModel,
    navFragmentController: NavHostController,
    navController: NavHostController
) {

    NavHost(navController = navFragmentController, startDestination = NavigationItem.ListScreen.route) {

        composable(NavigationItem.ListScreen.route) {
            ListScreen(mainViewModel = mainViewModel, navController = navFragmentController)
        }

        composable(NavigationItem.MapScreen.route) {
            MapScreen(mainViewModel = mainViewModel,navController = navController)
        }

        composable(
            NavigationItem.DetailsScreen.route,
            arguments = listOf(navArgument("housingId") { type = NavType.StringType })) { backStackEntry ->

            DetailsScreen( navController = navFragmentController, mainViewModel = mainViewModel,
                backStackEntry.arguments?.getString("housingId")!!
            )
        }
        composable(
            NavigationItem.SimulatorScreen.route,
            arguments = listOf(navArgument("price") { type = NavType.StringType })) { backStackEntry ->

            SimulatorScreen( navController = navFragmentController, mainViewModel = mainViewModel,
                backStackEntry.arguments?.getString("price")!!
            )
        }
    }
}