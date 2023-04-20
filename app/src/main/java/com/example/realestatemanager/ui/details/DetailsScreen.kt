package com.example.realestatemanager.ui.details

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.realestatemanager.models.HousingWithPhoto
import com.example.realestatemanager.services.Utils
import com.example.realestatemanager.ui.main.MainViewModel
import com.example.realestatemanager.ui.navigation.WindowInfo
import com.example.realestatemanager.ui.navigation.rememberWindowInfo
import com.example.realestatemanager.ui.theme.lexendFont
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    housingId: String
) {
    // DECLARATION
    val windowInfo = rememberWindowInfo()
    var housingWithPhoto: HousingWithPhoto?
    val context = LocalContext.current
    var typePrice by remember{ mutableStateOf(true) }

    //INITIALIZE DETAILS
    housingWithPhoto = if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded){
        mainViewModel.housingLiveData?.observeAsState()?.value
    }else{
        mainViewModel.getHousingWithPhoto()
    }

    // CHECK IF THERE'S ANY HOUSING IN THE DATABASE
    val housingListState = mainViewModel.housingListFlow.collectAsState(initial = listOf()).also {
        if (it.value.isNotEmpty()) {
            mainViewModel.setHousingIdDetail(housingId.toLong())
            if (housingWithPhoto == null) {
                housingWithPhoto = it.value[0]
            }
        }

    }

    val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMMM yyyy", Locale.US)


    if (housingListState.value.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceAround

        ) {
            // GET POSITION
            val position = Utils.getLocationFromAddress(housingWithPhoto!!.housing.address, context)

            // DISPLAY IMAGES
            if(housingWithPhoto!!.photosInHousing.isNotEmpty()){
                Box(modifier = Modifier.fillMaxWidth()){
                    ImageSelector(housingWithPhoto = housingWithPhoto!!,windowInfo=windowInfo)
                }
            }

            // DISPLAY ADDRESS AND AGENT NAME
            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colors.background)
                        .padding(6.dp)
                ){
                    Column(

                    ) {
                        Text(
                            text = housingWithPhoto!!.housing.address,
                            overflow = TextOverflow.Clip
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = housingWithPhoto!!.housing.agentName,
                            fontFamily = lexendFont,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }else{
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colors.secondary)
                        .padding(6.dp)
                ){
                    Column(
                        modifier = Modifier.weight(7f)
                    ) {
                        Text(
                            text = housingWithPhoto!!.housing.address,
                            overflow = TextOverflow.Clip
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = housingWithPhoto!!.housing.agentName,
                            fontFamily = lexendFont,
                            fontWeight = FontWeight.Light
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxSize()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ){
                        Button(onClick = {
                            navController.navigate("simulator_screen/"+housingWithPhoto!!.housing.price.toString())
                        }) {
                            Text(text = "Load\nSimulator")
                        }
                    }
                }
            }

            // DESCRIPTION
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            ){
                                append("Description\n")
                            }
                            if(housingWithPhoto!!.housing.description != "") {
                                append(housingWithPhoto!!.housing.description)
                            }else{
                                append("No description for the moment")
                            }
                        })
                }
                Spacer(modifier = Modifier.padding(16.dp))

                // TYPE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    Row(modifier = Modifier.weight(2f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ){
                                            append("Type \n")
                                        }
                                        if(housingWithPhoto!!.housing.type != "") {
                                            append(housingWithPhoto!!.housing.type)
                                        }else{
                                            append("Unknown")
                                        }
                                    })
                            }

                            // STATUS
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ){
                                            append("Status \n")
                                        }
                                        if(housingWithPhoto!!.housing.status != "") {
                                            append(housingWithPhoto!!.housing.status)
                                        }else{
                                            append("Unknown")
                                        }
                                    })
                            }

                            // DATES
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ){
                                            append("Date of entry \n")
                                        }
                                        if(housingWithPhoto!!.housing.dateOfEntry != null) {
                                            append(dateFormat.format(housingWithPhoto!!.housing.dateOfEntry).toString())
                                        }else{
                                            append("Unknown")
                                        }

                                    })
                            }
                            Row {
                                if(housingWithPhoto!!.housing.status == "Sold"){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ){
                                                append("Date of sell \n")
                                            }
                                            if(housingWithPhoto!!.housing.dateOfSell != null) {
                                                append(dateFormat.format(housingWithPhoto!!.housing.dateOfSell).toString())
                                            }

                                        })
                                }
                            }
                        }
                    }

                    // PRICE
                    Row(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(modifier = Modifier
                                .clickable {
                                    typePrice = !typePrice
                                }) {

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ){
                                            append("Price \n")
                                        }
                                        if (housingWithPhoto!!.housing.price != -1){
                                            if(typePrice){
                                                append(housingWithPhoto!!.housing.price.toString() + "$")
                                            }else{
                                                append(Utils.convertDollarToEuro(housingWithPhoto!!.housing.price).toString() + "€")
                                            }
                                        }else {
                                            append("Unknown")
                                        }

                                    })
                            }

                            // ROOM
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ){
                                            append("Room \n")
                                        }
                                        if (housingWithPhoto!!.housing.room != -1){
                                            append(housingWithPhoto!!.housing.room.toString())
                                        }else {
                                            append("Unknown")
                                        }

                                    })
                            }

                            // SURFACE
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ){
                                            append("Surface \n")
                                        }
                                        if (housingWithPhoto!!.housing.surface != -1){
                                            append(housingWithPhoto!!.housing.surface.toString() +" sq m")
                                        }else {
                                            append("Unknown")
                                        }

                                    })
                            }
                        }
                    }
                }
                // DISPLAY STATIC MAP
                if(position != null){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .padding(bottom = 62.dp)
                            .border(1.dp, Color.DarkGray)
                            .shadow(0.5.dp),
                        contentAlignment = Alignment.Center
                    ){
                        GlideImage(
                            model = "https://maps.googleapis.com/maps/api/staticmap?" +
                                    "center=${housingWithPhoto!!.housing.address}" +
                                    "&zoom=14&size=300x300" +
                                    "&maptype=roadmap\n" +
                                    "&markers=color:red%7C${position.latitude},${position.longitude}" +
                                    "&key=AIzaSyBlSeoT0erMP7v7RSLWyhcl2hodaKfG16s",
                            contentDescription = null,
                            contentScale = ContentScale.Crop)
                    }
                }
            }

        }
    }else{
        // NO HOUSING IN THE DATABASE
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
            ){
            Text(text = "No housing find")
        }
    }
    
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageSelector(
    housingWithPhoto : HousingWithPhoto,
    windowInfo: WindowInfo
) {

    LazyRow(modifier = Modifier.fillMaxWidth()){
        items(housingWithPhoto.photosInHousing.size){

            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded){
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                        .background(Color.LightGray),
                )
                {

                    GlideImage(
                        model = housingWithPhoto.photosInHousing[it].photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop)
                    Box(modifier = Modifier
                        .fillMaxSize()
                    )
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ){
                        Text(
                            text = housingWithPhoto.photosInHousing[it].caption,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }else{
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(250.dp)
                        .background(Color.LightGray),
                )
                {

                    GlideImage(
                        model = housingWithPhoto.photosInHousing[it].photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop)
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White
                                ),
                                startY = 750f
                            )
                        )

                    )
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ){
                        Text(
                            text = housingWithPhoto.photosInHousing[it].caption,
                            color = Color.Black
                        )
                    }

                }
            }
        }
    }
}