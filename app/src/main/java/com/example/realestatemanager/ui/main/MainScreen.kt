package com.example.realestatemanager.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.realestatemanager.services.Utils
import com.example.realestatemanager.ui.add.EditActivity
import com.example.realestatemanager.ui.details.DetailsScreen
import com.example.realestatemanager.ui.navigation.NavigationFragment
import com.example.realestatemanager.ui.navigation.WindowInfo
import com.example.realestatemanager.ui.navigation.rememberWindowInfo
import com.example.realestatemanager.ui.simulator.SimulatorScreen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController
    ) {

    // DECLARATION
    val navFragmentController = rememberNavController()
    val windowInfo = rememberWindowInfo()
    val scaffoldState = rememberScaffoldState()
    val backStackEntryFragment = navFragmentController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val shouldShowMap = remember{ mutableStateOf(false) }
    val shouldShowSimulator = remember{ mutableStateOf(false) }
    val scope  = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier,
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                title ={
                    Text(text = "RealEstateManager")
                },
                actions = {

                    // BUTTON TO NAVIGATE TO LOAD SIMULATOR
                    if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded &&!shouldShowMap.value&&!shouldShowSimulator.value){
                        IconButton(onClick = {
                            shouldShowSimulator.value = true
                        }) {
                            Icon(Icons.Filled.Money, contentDescription = "Load Simulator")
                        }
                    }

                    // BUTTON TO NAVIGATE TO LIST SCREEN
                    if (backStackEntryFragment.value?.destination?.route == "map_screen" || shouldShowMap.value||shouldShowSimulator.value){
                        IconButton(onClick = {
                            if (shouldShowMap.value && backStackEntryFragment.value?.destination?.route ==null||shouldShowSimulator.value){
                                shouldShowMap.value = false
                                shouldShowSimulator.value = false
                            }else{
                                navFragmentController.navigate("list_screen")
                            }
                        }) {
                            Icon(Icons.Filled.List, contentDescription = "list")
                        }
                    }

                    // BUTTON TO NAVIGATE TO MAP IF THERE'S A CONNEXION
                    if (backStackEntryFragment.value?.destination?.route == "list_screen" ||windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded &&!shouldShowMap.value&&!shouldShowSimulator.value){
                        IconButton(onClick = {
                            if (backStackEntryFragment.value?.destination?.route ==null){
                                shouldShowMap.value = true
                            }else{
                                if(Utils.isInternetAvailable(context)){
                                    navFragmentController.navigate("map_screen")
                                }else{
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("No internet connexion available")
                                    }
                                }
                            }

                        }) {
                            Icon(Icons.Filled.Map, contentDescription = "map")
                        }

                        // BUTTON TO EXPAND THE SEARCH VIEW IN LIST SCREEN
                        IconButton(onClick = {
                            if(mainViewModel.getExpandedSearch()){
                                mainViewModel.setExpandedSearch(false)
                            }else{
                                mainViewModel.setExpandedSearch(true)
                            }
                        }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }

                    // BUTTON TO NAVIGATE TO EDIT SCREEN TO INSERT A NEW HOUSING
                    if (backStackEntryFragment.value?.destination?.route == "list_screen" || backStackEntryFragment.value?.destination?.route == "map_screen" || backStackEntryFragment.value?.destination?.route ==null&&!shouldShowSimulator.value){
                        IconButton(onClick = {
                            val navigateToEdit = Intent(context, EditActivity::class.java)
                            navigateToEdit.putExtra("housingId", -999)
                            ContextCompat.startActivity(context, navigateToEdit, null)
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add")
                        }
                    }

                    // BUTTON TO NAVIGATE TO EDIT SCREEN TO UPDATE A HOUSING
                    if (backStackEntryFragment.value?.destination?.route == "details_screen/{housingId}"
                        ||backStackEntryFragment.value?.destination?.route ==null &&!shouldShowSimulator.value&&!shouldShowMap.value){
                        IconButton(onClick = {
                            val housingID = mainViewModel.getHousingIdDetail()
                            val navigateToEdit = Intent(context, EditActivity::class.java)
                            navigateToEdit.putExtra("housingId", housingID.toInt())
                            ContextCompat.startActivity(context, navigateToEdit, null)
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }

    ){
        if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded ){

            // IF LARGE SCREEN DISPLAY LIST SCREEN AND DETAIL SCREEN
            Row(modifier = Modifier.fillMaxSize()) {
                if (!shouldShowMap.value&& shouldShowSimulator.value){
                    SimulatorScreen(navController = navFragmentController, mainViewModel = mainViewModel, price = "0")
                }else if(shouldShowMap.value){
                    MapScreen(navController = navFragmentController, mainViewModel = mainViewModel)
                }else{
                    Row(modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.33f)
                    ){
                        ListScreen(mainViewModel = mainViewModel, navController = navFragmentController)
                    }
                    Row(modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.66f)
                    ){
                        DetailsScreen(
                            navController = navFragmentController,
                            mainViewModel = mainViewModel,
                            housingId = "1"
                        )
                    }
                }
            }
        }else{
            NavigationFragment(mainViewModel = mainViewModel,navFragmentController=navFragmentController, navController=navController)
        }

    }
}


