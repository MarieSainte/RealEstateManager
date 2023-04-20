package com.example.realestatemanager.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.realestatemanager.ui.main.MainScreen
import com.example.realestatemanager.ui.main.MainViewModel
import com.example.realestatemanager.ui.theme.RealEstateManagerTheme
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks {


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel by viewModels()
            val navController = rememberNavController()
            val perms = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

            )
            getPermissions(perms)

            RealEstateManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //START WITH MAIN SCREEN
                    MainScreen(mainViewModel = mainViewModel, navController = navController)
                }
            }
        }
    }

    // PERMISSIONS
    private fun getPermissions(perms: Array<String>) {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE ,
                Manifest.permission.READ_EXTERNAL_STORAGE ,
                Manifest.permission.CAMERA ,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Already have permission, do the thing
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                host = this,
                rationale = "Permissions",
                requestCode = 123,
                perms = perms
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.e(TAG, "onPermissionsDenied------ "  )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.e(TAG, "onPermissionsGranted-------- "  )
    }
}

