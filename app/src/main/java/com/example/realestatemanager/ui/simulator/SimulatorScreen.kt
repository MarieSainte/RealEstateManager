package com.example.realestatemanager.ui.simulator



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.realestatemanager.services.Utils
import com.example.realestatemanager.ui.main.MainViewModel
import com.example.realestatemanager.ui.navigation.WindowInfo
import com.example.realestatemanager.ui.navigation.rememberWindowInfo

@Composable
fun SimulatorScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    price: String
) {
    // DECLARATION
    var typeState by remember { mutableStateOf(true) }
    var depositeState by remember { mutableStateOf("") }
    var rateState by remember { mutableStateOf("") }
    var durationState by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val windowInfo = rememberWindowInfo()


    if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded){
        // DECLARATION
        var priceLive by remember { mutableStateOf("") }

        // INITIALIZE THE PRICE
        mainViewModel.housingLiveData?.observeAsState()?.value.also {
            priceLive = it?.housing?.price.toString()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.clickable { typeState = !typeState }
            ) {
                if (typeState){
                    Text(
                        text = "$priceLive $",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }else{
                    Text(
                        text = Utils.convertDollarToEuro(priceLive.toInt()).toString() +"€",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
            // DEPOSITE
            Row() {
                OutlinedTextField(
                    value = depositeState,
                    label = { Text("Deposite") },
                    onValueChange ={ depositeState = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }
            // RATE
            Row() {
                OutlinedTextField(
                    value = rateState,
                    label = { Text(text = "Rate") },
                    onValueChange ={ rateState = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }
            // LOAD DURATION
            Row() {
                OutlinedTextField(
                    value = durationState,
                    label = { Text("Load Duration") },
                    onValueChange ={ durationState = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }

            // RESULT
            if(result.isNotEmpty()) {
                Column{
                    if (typeState){
                        Text(
                            text = "Monthly payment : $result $",
                            color = Color.Red,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Total : " + (result.toInt() * durationState.toInt()).toString(),
                            color = Color.Red,
                            fontSize = 24.sp
                        )
                    }else{
                        val euroPriceMonthly = Utils.convertDollarToEuro(result.toInt()).toString()
                        val euroPriceTotal = Utils.convertDollarToEuro(result.toInt() * durationState.toInt()).toString()
                        Text(
                            text = "Monthly payment : $euroPriceMonthly €" ,
                            color = Color.Red,
                            fontSize = 24.sp
                        )

                        Text(
                            text = "Total : $euroPriceTotal",
                            color = Color.Red,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))
                    Button(onClick = {
                        depositeState =""
                        rateState = ""
                        durationState = ""
                        result = ""
                    }) {
                        Text(text = "Reset")
                    }
                }
            }else{
                Row() {
                    Button(onClick = {
                        if(depositeState.isNotEmpty() && rateState.isNotEmpty() && durationState.isNotEmpty()){
                            result =
                                mainViewModel.loadSimulator(price.toDouble(), depositeState.toInt(), rateState.toInt(), durationState.toInt()).toString()
                        }

                    }) {
                        Text(text = "Simulate")
                    }
                }
            }

        }
    }
    // IF NOT LARGE SCREEN
    else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.clickable { typeState = !typeState }
            ) {
                if (typeState){
                    Text(
                        text = "$price$",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }else{
                    Text(
                        text = Utils.convertDollarToEuro(price.toInt()).toString() +"€",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
            Row() {
                OutlinedTextField(
                    value = depositeState,
                    label = { Text("Deposite") },
                    onValueChange ={ depositeState = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }
            Row() {
                OutlinedTextField(
                    value = rateState,
                    label = { Text(text = "Rate") },
                    onValueChange ={ rateState = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }
            Row() {
                OutlinedTextField(
                    value = durationState,
                    label = { Text("Load Duration") },
                    onValueChange ={ durationState = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }
            // IF RESULT IS NOT EMPTY - DISPLAY THE RESULT
            if(result.isNotEmpty()) {
                Column (

                ) {
                    if (typeState){
                        Text(
                            text = "Monthly payment : $result $",
                            color = Color.Red,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Total : " + (result.toInt() * durationState.toInt()).toString(),
                            color = Color.Red,
                            fontSize = 24.sp
                        )
                    }else{
                        val euroPriceMonthly = Utils.convertDollarToEuro(result.toInt()).toString()
                        val euroPriceTotal = Utils.convertDollarToEuro(result.toInt() * durationState.toInt()).toString()
                        Text(
                            text = "Monthly payment : $euroPriceMonthly €" ,
                            color = Color.Red,
                            fontSize = 24.sp
                        )

                        Text(
                            text = "Total : $euroPriceTotal",
                            color = Color.Red,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))
                    Button(onClick = {
                        depositeState =""
                        rateState = ""
                        durationState = ""
                        result = ""
                    }) {
                        Text(text = "Reset")
                    }
                }
            }else{
                Row() {
                    Button(onClick = {
                        if(depositeState.isNotEmpty() && rateState.isNotEmpty() && durationState.isNotEmpty()){
                            result =
                                mainViewModel.loadSimulator(price.toDouble(), depositeState.toInt(), rateState.toInt(), durationState.toInt()).toString()
                        }

                    }) {
                        Text(text = "Simulate")
                    }
                }
            }

        }
    }

}