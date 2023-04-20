package com.example.realestatemanager.ui.main

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.realestatemanager.services.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    // GET CONTEXT
    val context = LocalContext.current
    // GET LOCATION
    var myPosition = LatLng(48.86, 2.38)
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
            if (location != null){
                //WE COMMENT THIS PART TO TEST "myPosition"" ON THE EMULATION
                //myPosition = LatLng(location.latitude, location.longitude)
            }
        }
    // GET HOUSING LIST FROM ROOM
    val housingListState = mainViewModel.housingListFlow.collectAsState(initial = listOf())
    // SET CAMERA POSITION ACCORDING TO THE LOCATION
    val cameraPositionState = rememberCameraPositionState {
        if (myPosition.latitude != 0.0){
            position = CameraPosition.fromLatLngZoom(myPosition, 14f)
        }else{
            Log.e(TAG, "Can't access your localisation" )
        }
    }

    // GOOGLE MAP
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 50.dp)
    ){
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            housingListState.value.forEach {
                val addressPosition : LatLng = Utils.getLocationFromAddress(it.housing.address, context)
                Marker(
                    state = MarkerState(position = addressPosition),
                    title = it.housing.type,
                    snippet = " ${it.housing.status} - ${it.housing.price}\$",
                    tag = it.housing.roomId,
                    onInfoWindowClick = { marker ->
                        navController.navigate("details_screen/$marker.tag")
                    }

                )
            }
            Marker(
                state = MarkerState(position = myPosition),
                title = "Your Position",
                icon = (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }
    }
}