package com.example.realestatemanager.ui.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import androidx.sqlite.db.SimpleSQLiteQuery
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.realestatemanager.models.HousingWithPhoto
import com.example.realestatemanager.models.InterestItem
import com.example.realestatemanager.services.Utils
import com.example.realestatemanager.ui.navigation.WindowInfo
import com.example.realestatemanager.ui.navigation.rememberWindowInfo
import java.time.LocalDate
import java.util.*


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController,
) {
    // DECLARATION
    val housingListState : State<List<HousingWithPhoto>>
    val query = SimpleSQLiteQuery(settingsString.toString())
    val windowInfo = rememberWindowInfo()
    val showTheSearch = mainViewModel.expandedSearch.observeAsState(false)

    // INIT HOUSINGS LIST
    housingListState = if (filterState.value){
        mainViewModel.housingFilterFlow(query).collectAsState(initial = listOf())
    }else{
        mainViewModel.housingListFlow.collectAsState(initial = listOf())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (showTheSearch.value){
                ExpandableCard(mainViewModel=mainViewModel)
            }
        }
        Box(modifier = Modifier.fillMaxSize()
        ){
            LazyColumn{
                items(housingListState.value.size
                ){index ->
                    val housingWithPhoto = housingListState.value[index]
                    mainViewModel.setHousingLiveData(housingListState.value[0])
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded ){
                                mainViewModel.setHousingWithPhoto(housingListState.value[index])
                                mainViewModel.setHousingLiveData(housingListState.value[index])
                            }else{
                                mainViewModel.setHousingWithPhoto(housingListState.value[index])
                                navController.navigate("details_screen/" + housingWithPhoto.housing.roomId.toString())
                            }
                        }
                        .height(84.dp)

                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()

                            ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.3f)
                            ) {

                                if (housingWithPhoto.photosInHousing.isNotEmpty()) {
                                    GlideImage(model = housingWithPhoto.photosInHousing[0].photo, contentDescription = null, contentScale = ContentScale.Crop)
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalArrangement = Arrangement.SpaceBetween,

                                ) {
                                if(housingWithPhoto.housing.address.isNotEmpty()){
                                    Text(
                                        text = housingWithPhoto.housing.address,
                                        overflow = TextOverflow.Clip,
                                        maxLines = 1
                                    )
                                }
                                Row {
                                    if(housingWithPhoto.housing.price > 0){
                                        Text(text = "${housingWithPhoto.housing.price} $")
                                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                    }
                                    if(housingWithPhoto.housing.room > 0){
                                        Text(text = "${housingWithPhoto.housing.room} room")
                                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                    }
                                    if(housingWithPhoto.housing.surface > 0){
                                        Text(text = "${housingWithPhoto.housing.surface} sq m")
                                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                    }
                                }

                            }
                        }
                        Spacer(modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(1.dp)
                            .background(color = Color.LightGray)
                            .align(Alignment.BottomCenter))
                    }
                }
            }

        }
    }
}
// DECLARATION
private var interestState =   mutableStateListOf<String>()
private val openInterest =  mutableStateOf(false)
private var filterState =  mutableStateOf(false)
private val settingsString = StringBuilder()


@Composable
fun ExpandableCard(
    mainViewModel :MainViewModel
) {
    // DECLARATION

    var typeState by remember { mutableStateOf("") }
    var surfaceStateMin by remember { mutableStateOf("") }
    var surfaceStateMax by remember { mutableStateOf("") }
    var roomStateMin by remember { mutableStateOf("") }
    var roomStateMax by remember { mutableStateOf("") }
    var priceStateMin by remember { mutableStateOf("") }
    var priceStateMax by remember { mutableStateOf("") }
    var statusState by remember { mutableStateOf("") }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    delayMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(2.dp),

    ) {

            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filter",
                        fontWeight = FontWeight.Bold)
                }
            Column(
                modifier = Modifier.padding(top = 16.dp ,bottom = 52.dp , start = 16.dp , end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SURFACE
                Row {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Surface",
                            color = Color.LightGray
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(1.dp)
                                .background(color = Color.LightGray)
                        )
                        Row {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = surfaceStateMin,
                                    label = { Text("Min") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = { surfaceStateMin = it },
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = surfaceStateMax,
                                    label = { Text("Max") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = { surfaceStateMax = it },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                // ROOM
                Row {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Room",
                            color = Color.LightGray
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(1.dp)
                                .background(color = Color.LightGray)
                        )
                        Row {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = roomStateMin,
                                    label = { Text("Min") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = { roomStateMin = it },
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = roomStateMax,
                                    label = { Text("Max") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = { roomStateMax = it },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                // Price
                Row {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Price",
                            color = Color.LightGray
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(1.dp)
                                .background(color = Color.LightGray)
                        )
                        Row {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = priceStateMin,
                                    label = { Text("Min") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = { priceStateMin = it },
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = priceStateMax,
                                    label = { Text("Max") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = { priceStateMax = it },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                // Type
                Row {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Type",
                            color = Color.LightGray
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(1.dp)
                                .background(color = Color.LightGray)
                        )
                        Dropdown(
                            label = "Type",
                            list = listOf("House", "Apartment", "Manor"),
                            typeState
                        ) {
                            typeState = it
                        }
                    }
                }
                // Status
                Row {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Status",
                            color = Color.LightGray
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(1.dp)
                                .background(color = Color.LightGray)
                        )
                        Dropdown(
                            label = "Status",
                            list = listOf("Available", "Suspended", "Sold"),
                            statusState
                        ) {
                            statusState = it
                        }
                    }
                }

                if (openInterest.value) {
                    AlertToPickInterest()
                }

                Spacer(modifier = Modifier.padding(16.dp))

                // FILTER BUTTON
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        filterState.value = false
                        mainViewModel.setExpandedSearch(false)
                    }) {
                        Text(text = "Reset")
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                    Button(onClick = {
                        settingsString.clear()
                        settingsString.append("select * from housing Where ")

                        if(surfaceStateMin.isNotEmpty()){
                            settingsString.append("surface >= '$surfaceStateMin' and ")
                        }
                        if(surfaceStateMax.isNotEmpty()){
                            settingsString.append("surface <= '$surfaceStateMax' and ")
                        }
                        if(roomStateMin.isNotEmpty()){
                            settingsString.append("room >= '$roomStateMin' and ")
                        }
                        if(roomStateMax.isNotEmpty()){
                            settingsString.append("room <= '$roomStateMax' and ")
                        }
                        if(priceStateMin.isNotEmpty()){
                            settingsString.append("price >= '$priceStateMin' and ")
                        }
                        if(priceStateMax.isNotEmpty()){
                            settingsString.append("price <= '$priceStateMax' and ")
                        }
                        if(typeState.isNotEmpty()){
                            settingsString.append("type = '$typeState' and ")
                        }
                        if(statusState.isNotEmpty()){
                            settingsString.append("status = '$statusState' and ")
                        }

                        for(i in 0 until 4){
                            settingsString.deleteCharAt(settingsString.lastIndex)
                        }
                        mainViewModel.setExpandedSearch(false)
                        filterState.value = false
                        filterState.value = true
                    }) {
                        Text(text = "Filter")
                    }
                }
            }
        }
    }
}

@Composable
fun AlertToPickInterest() {

    var school by remember { mutableStateOf(false) }
    var park by remember { mutableStateOf(false) }
    var downtown by remember { mutableStateOf(false) }
    var nature by remember { mutableStateOf(false) }
    val interestList = remember { mutableStateListOf<InterestItem>() }
    interestList.add(InterestItem("School",school))
    interestList.add(InterestItem("Park",park))
    interestList.add(InterestItem("Downtown",downtown))
    interestList.add(InterestItem("Nature",nature))

    AlertDialog(
        onDismissRequest = {
            openInterest.value = false
        },
        title = {
            Text(
                text = "Interest",
                fontSize = 24.sp
            )
        },
        text = {
            Column (
                modifier = Modifier.padding(16.dp)
            ){
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    items(interestList.size) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = interestList[it].title)
                            Checkbox(
                                checked = interestList[it].isSelected,
                                onCheckedChange = { _ ->

                                    when (interestList[it].title){
                                        interestList[0].title -> school = !school
                                        interestList[1].title -> park = !park
                                        interestList[2].title -> downtown = !downtown
                                        interestList[3].title -> nature = !nature
                                    }
                                    interestList[it].isSelected = !interestList[it].isSelected
                                },
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    modifier = Modifier.wrapContentWidth(),
                    onClick = {
                        openInterest.value = false
                    }
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    modifier = Modifier.wrapContentWidth(),
                    onClick = {
                        interestState = mutableStateListOf()

                        for(interest in interestList) {

                            if (interest.isSelected && !interestState.contains(interest.title)) {
                                interestState.add(interest.title)
                            }
                        }
                        openInterest.value = false
                    }
                ) {
                    Text("Ok")
                }
            }
        }
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
fun SelectDate(
    label: String,
    mState: String,
    updateState: (Date) -> Unit
){

    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, Year: Int, Month: Int, DayOfMonth: Int ->

            val date = LocalDate.of(Year, Month+1, DayOfMonth)
            updateState(Utils.convertToDateViaSqlDate(date))
        }, mYear, mMonth, mDay
    )

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

        // Creating a button that on
        // click displays/shows the DatePickerDialog
        Button(onClick = {
            mDatePickerDialog.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFA78467)) ) {
            Text(mState.ifEmpty { label } )
        }
    }
}

@Composable
fun Dropdown(
    label: String,
    list : List<String>,
    statusState: String,
    updateState: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    var textFiledSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Column {

        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .onGloballyPositioned { coordinates ->
                    textFiledSize = coordinates.size.toSize()
                }
        ) {
            Text(statusState.ifEmpty {
                label
            })
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })
        )
        {
            list.forEach { label ->
                DropdownMenuItem(onClick = {
                    updateState(label)
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}