package com.example.realestatemanager.ui.add

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.realestatemanager.R
import com.example.realestatemanager.models.*
import com.example.realestatemanager.services.Notification
import com.example.realestatemanager.services.Utils
import com.example.realestatemanager.ui.MainActivity
import com.example.realestatemanager.ui.navigation.WindowInfo
import com.example.realestatemanager.ui.navigation.rememberWindowInfo
import com.example.realestatemanager.ui.theme.RealEstateManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class EditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            RealEstateManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    outputDirectory = getOutputDirectory()
                    cameraExecutor = Executors.newSingleThreadExecutor()
                    val windowInfo = rememberWindowInfo()
                    if (shouldShowCamera.value) {
                        CameraView(
                            modifier = Modifier,
                            outputDirectory = outputDirectory,
                            executor = cameraExecutor,
                            onImageCaptured = ::handleImageCapture,
                            onError = { Log.e("Error", "View error:", it) }
                        )
                    }
                    else if (shouldShowPhoto.value){
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberImagePainter(photoUri),
                                contentDescription = null,
                                modifier = Modifier.wrapContentSize()
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Row{
                                Button(onClick = {
                                    shouldShowPhoto.value = false
                                    shouldShowCamera.value = true
                                }) {
                                    Text(text = "Retake")
                                }
                                Spacer(modifier = Modifier.padding(8.dp))
                                Button(onClick = {
                                    shouldShowPhoto.value = false
                                    imagesState.add(Utils.RotateBitmap(bitmap,90f))
                                    captionState.add("")
                                }) {
                                    Text(text = "Ok")
                                }
                            }

                        }
                    }
                    else {
                        if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded){
                            EditLargeScreen(this,windowInfo)
                        }else{
                            EditScreen(this,windowInfo)
                        }

                    }

                    // GET THE HOUSING ID IN THE EXTRA
                    val b = intent.extras
                    housingId = b?.getInt("housingId") ?: throw NullPointerException("Expression 'b' must not be null")

                    //Check if it's a update or a new housing
                    if(housingId != -999) {

                        // FILL ALL FIELDS WITH THE HOUSING'S INFORMATION
                        editViewModel.getHousing(housingId.toLong())
                        editViewModel.housingLiveData.observe(this ){housingWithPhoto ->
                            listPhotoRoom = housingWithPhoto.photosInHousing
                            if(housingWithPhoto != null){
                                descriptionState.value = housingWithPhoto.housing.description
                                typeState.value = housingWithPhoto.housing.type
                                addressState.value = housingWithPhoto.housing.address
                                surfaceState.value = housingWithPhoto.housing.surface.toString()
                                roomState.value = housingWithPhoto.housing.room.toString()
                                priceState.value = housingWithPhoto.housing.price.toString()
                                dateOfEntryState.value = housingWithPhoto.housing.dateOfEntry
                                if (housingWithPhoto.housing.dateOfEntry != null) {
                                    dateOfEntryStateString.value = Utils.getTodayDate(housingWithPhoto.housing.dateOfEntry)
                                }
                                dateOfSellState.value = housingWithPhoto.housing.dateOfSell
                                if (housingWithPhoto.housing.dateOfSell != null) {
                                    dateOfSellStateString.value = Utils.getTodayDate(housingWithPhoto.housing.dateOfSell)
                                }
                                statusState.value = housingWithPhoto.housing.status
                                agentNameState.value = housingWithPhoto.housing.agentName

                                Log.e("TAG", " Start - Image Edit: ${imagesState.size} / ROOM : ${housingWithPhoto.photosInHousing.size}" )
                                if(imagesState.isEmpty()){
                                    for (i in 0 until housingWithPhoto.photosInHousing.size){
                                        imagesState.add(housingWithPhoto.photosInHousing[i].photo)
                                        captionState.add(housingWithPhoto.photosInHousing[i].caption)
                                        Log.e("TAG", "Image Edit: ${imagesState.size}/ROOM : ${housingWithPhoto.photosInHousing.size}" )
                                    }
                                }

                            }
                        }
                        editViewModel.housingWithInterest.observe(this ) { housingWithInterest ->

                            for (i in 0 until housingWithInterest.interestsInHousing.size){
                                interestState.add(housingWithInterest.interestsInHousing[i].interestName)
                            }
                        }
                    }
                }
            }
        }
    }
    // DECLARATION
    private val editViewModel: EditViewModel by viewModels()
    private var listPhotoRoom : List<PhotoEntity>? = null
    private var housingId : Int = -999
    private var descriptionState =  mutableStateOf("")
    private var typeState =  mutableStateOf("")
    private var addressState = mutableStateOf("")
    private var surfaceState = mutableStateOf("")
    private var roomState = mutableStateOf("")
    private var priceState = mutableStateOf("")
    private var dateOfEntryState : MutableState<Date?> = mutableStateOf(Date())
    private var dateOfSellState : MutableState<Date?> = mutableStateOf(Date())
    private var dateOfEntryStateString = mutableStateOf("")
    private var dateOfSellStateString = mutableStateOf("")
    private var statusState = mutableStateOf("")
    private var agentNameState = mutableStateOf("")
    private var imagesState =  mutableStateListOf<Bitmap>()
    private var captionState =  mutableStateListOf<String>()
    private var interestState =  mutableStateListOf<String>()

    private val openDialog =  mutableStateOf(false)
    private val openInterest =  mutableStateOf(false)

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var bitmap: Bitmap
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        imagesState.add(bitmap)
        captionState.add("")
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
    @Composable
    fun EditLargeScreen(editActivity: EditActivity, windowInfo: WindowInfo){

        val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMMM yyyy", Locale.US)
        val scaffoldState = rememberScaffoldState()
        val context = LocalContext.current
        Notification.createNotificationChannel(context)
        val scope  = rememberCoroutineScope()

        Scaffold (
            modifier= Modifier.fillMaxSize(),
            scaffoldState = scaffoldState
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {

                //------------
                // DISPLAY IMAGES
                //-------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    if(imagesState.size > 0) {
                        ImageSelector(windowInfo = windowInfo)
                    }

                    if(openDialog.value) {
                        AlertToPickImages()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement= Arrangement.Center,

                ) {
                    //------------
                    // Description
                    //-------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement= Arrangement.Start,
                    )  {
                        OutlinedTextField(
                            value = descriptionState.value,
                            label = { Text("Description") },

                            onValueChange ={ descriptionState.value = it },
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Button(modifier = Modifier.fillMaxWidth(0.8f),
                            onClick = {  openDialog.value = true }) {
                            Text(text = "Get a picture")
                        }
                    }

                    //------------
                    // Address
                    //-------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement= Arrangement.Start,
                    )  {

                        OutlinedTextField(
                            value = addressState.value,
                            label = { Text("Address") },
                            onValueChange ={ addressState.value = it },
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Dropdown("Type", listOf("House", "Apartment","Manor"),typeState.value){
                            typeState.value = it
                        }
                    }
                    //------------
                    // Surface
                    //-------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement= Arrangement.Start,
                    )  {
                        OutlinedTextField(
                            value = surfaceState.value,
                            label = { Text("Surface") },
                            onValueChange ={ surfaceState.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxLines = 1,
                            )
                        if(openInterest.value) {
                            AlertToPickInterest()
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Button(modifier = Modifier.fillMaxWidth(0.8f),
                            onClick = {  openInterest.value = true }) {
                            Text(text = "Select the interest")
                        }
                    }
                    //------------
                    // Room
                    //-------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement= Arrangement.Start,
                    )  {
                        OutlinedTextField(
                            value = roomState.value,
                            label = { Text("Room") },
                            onValueChange ={ roomState.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Dropdown("Status", listOf("Available", "Suspended","Sold"),statusState.value){
                            statusState.value = it
                        }
                    }
                    //------------
                    // Price
                    //-------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement= Arrangement.Start,
                    )  {
                        OutlinedTextField(
                            value = priceState.value,
                            label = { Text("Price") },
                            onValueChange ={ priceState.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                        ) {
                            SelectDate("Date Of Entry",
                                mState = dateOfEntryStateString.value,
                                windowInfo = windowInfo
                            ){
                                dateOfEntryState.value = it
                                dateOfEntryStateString.value =  dateFormat.format(it)
                            }

                            if (statusState.value == "Sold"){
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                SelectDate("Date Of Sell",
                                    mState = dateOfSellStateString.value,
                                    windowInfo = windowInfo
                                ){
                                    dateOfSellState.value = it
                                    dateOfSellStateString.value =  dateFormat.format(it)
                                }
                            }
                        }
                    }
                    //------------
                    // Agent
                    //-------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement= Arrangement.Start,
                    )  {
                        OutlinedTextField(
                            value = agentNameState.value,
                            label = { Text("Agent") },
                            onValueChange ={ agentNameState.value = it },
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        //------------
                        // Add Button
                        //-------------
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(0.8f),
                            onClick = {

                            if(statusState.value == "Sold"
                                && dateOfSellStateString.value.isNotEmpty()
                                && addressState.value.isNotEmpty()
                                && surfaceState.value.isNotEmpty()
                                && roomState.value.isNotEmpty()
                                && priceState.value.isNotEmpty()
                                || statusState.value != "Sold"
                                && addressState.value.isNotEmpty()
                                && surfaceState.value.isNotEmpty()
                                && roomState.value.isNotEmpty()
                                && priceState.value.isNotEmpty()
                            ){
                                if(dateOfEntryStateString.value.isEmpty()){
                                    dateOfEntryState.value = null
                                }
                                if(dateOfSellStateString.value.isEmpty()){
                                    dateOfSellState.value = null
                                }

                                // SAVE HOUSING IN ROOM
                                if(housingId == -999){
                                    // INSERT HOUSING
                                    val housing = HousingEntity(
                                        description = descriptionState.value,
                                        type = typeState.value,
                                        address = addressState.value,
                                        surface = surfaceState.value.toInt(),
                                        room = roomState.value.toInt(),
                                        price = priceState.value.toInt(),
                                        status = statusState.value,
                                        dateOfEntry = dateOfEntryState.value,
                                        dateOfSell = dateOfSellState.value,
                                        agentName = agentNameState.value
                                    )
                                    editViewModel.addHousing(housing)

                                    editViewModel.housingId.observe(editActivity ){

                                        Notification.sendNotification(context)

                                        // SAVE INTERESTS IN ROOM
                                        for (i in 0 until interestState.size){
                                            val interest = InterestEntity(
                                                interestName = interestState[i]
                                            )
                                            editViewModel.addInterest(interest)

                                            val housingInterest = HousingInterestCrossRef(
                                                roomId = it,
                                                interestName = interestState[i]
                                            )
                                            editViewModel.addHousingInterestCrossRef(housingInterest)
                                        }

                                        // SAVE PHOTOS IN ROOM
                                        for (i in 0 until imagesState.size){

                                            val photo = PhotoEntity(
                                                caption = captionState[i],
                                                photo = imagesState[i],
                                                roomId = it
                                            )
                                            editViewModel.addPhoto(photo)
                                            editViewModel.photoId.observe(editActivity ) {
                                                if (i == imagesState.size-1){
                                                    val navigateToMain = Intent(context, MainActivity::class.java)
                                                    ContextCompat.startActivity(context, navigateToMain, null)
                                                }
                                            }
                                        }

                                        if (
                                            imagesState.isEmpty()
                                        ){
                                            val navigateToMain = Intent(context, MainActivity::class.java)
                                            ContextCompat.startActivity(context, navigateToMain, null)
                                        }
                                    }
                                }else{
                                    // UPDATE HOUSING
                                    val housing = HousingEntity(
                                        roomId = housingId.toLong(),
                                        description = descriptionState.value,
                                        type = typeState.value,
                                        address = addressState.value,
                                        surface = surfaceState.value.toInt(),
                                        room = roomState.value.toInt(),
                                        price = priceState.value.toInt(),
                                        status = statusState.value,
                                        dateOfEntry = dateOfEntryState.value,
                                        dateOfSell = dateOfSellState.value,
                                        agentName = agentNameState.value
                                    )
                                    editViewModel.updateHousing(housing)
                                    if(listPhotoRoom!=null){
                                        for(i in 0 until listPhotoRoom!!.size){
                                            editViewModel.deletePhoto(listPhotoRoom!![i])
                                        }
                                    }

                                    // SAVE INTERESTS IN ROOM
                                    for (i in 0 until interestState.size){
                                        val interest = InterestEntity(
                                            interestName = interestState[i]
                                        )
                                        editViewModel.addInterest(interest)

                                        val housingInterest = HousingInterestCrossRef(
                                            roomId = housingId.toLong(),
                                            interestName = interestState[i]
                                        )
                                        editViewModel.addHousingInterestCrossRef(housingInterest)
                                    }

                                    // SAVE PHOTOS IN ROOM
                                    for (i in 0 until imagesState.size){
                                        val photo = PhotoEntity(
                                            caption = captionState[i],
                                            photo = imagesState[i],
                                            roomId = housingId.toLong()

                                        )
                                        editViewModel.addPhoto(photo)
                                        editViewModel.photoId.observe(editActivity) {
                                            if (i==imagesState.size -1){
                                                val navigateToMain = Intent(context, MainActivity::class.java)
                                                ContextCompat.startActivity(context, navigateToMain, null)
                                            }
                                        }
                                    }
                                    if (imagesState.isEmpty()){
                                        val navigateToMain = Intent(context, MainActivity::class.java)
                                        ContextCompat.startActivity(context, navigateToMain, null)
                                    }
                                }

                            }else{
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Fill the mandatory fields")
                                }
                            }

                        }){
                            if(housingId == -999){
                                Text(text = "Add")
                            }else{
                                Text(text = "Update")
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
    @Composable
    fun EditScreen(editActivity: EditActivity, windowInfo: WindowInfo) {

        val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMMM yyyy", Locale.US)
        val scaffoldState = rememberScaffoldState()
        val context = LocalContext.current
        Notification.createNotificationChannel(context)
        val scope  = rememberCoroutineScope()

        Scaffold (
            modifier= Modifier.fillMaxSize(),
            scaffoldState = scaffoldState
        ){

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ){
                //------------
                // DISPLAY IMAGES
                //-------------
                if(imagesState.size > 0) {
                    ImageSelector(windowInfo = windowInfo)
                }

                if(openDialog.value) {
                    AlertToPickImages()
                }
                //------------
                // Description
                //-------------
                Row {

                    OutlinedTextField(
                        value = descriptionState.value,
                        label = { Text("Description") },

                        onValueChange ={ descriptionState.value = it },
                        maxLines = 5
                    )
                }

                //------------
                // Address
                //-------------
                Row {

                    OutlinedTextField(
                        value = addressState.value,
                        label = { Text("Address") },
                        onValueChange ={ addressState.value = it },
                        maxLines = 1
                    )
                }
                //------------
                // Surface
                //-------------
                Row {
                    OutlinedTextField(
                        value = surfaceState.value,
                        label = { Text("Surface") },
                        onValueChange ={ surfaceState.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        maxLines = 1,

                    )
                }
                //------------
                // Room
                //-------------
                Row {
                    OutlinedTextField(
                        value = roomState.value,
                        label = { Text("Room") },
                        onValueChange ={ roomState.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        maxLines = 1
                    )
                }
                //------------
                // Price
                //-------------
                Row {
                    OutlinedTextField(
                        value = priceState.value,
                        label = { Text("Price") },
                        onValueChange ={ priceState.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        maxLines = 1
                    )
                }
                //------------
                // Agent
                //-------------
                Row {
                    OutlinedTextField(
                        value = agentNameState.value,
                        label = { Text("Agent") },
                        onValueChange ={ agentNameState.value = it },
                        maxLines = 1
                    )
                }
                //------------
                // Photos
                //-------------


                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Button(modifier = Modifier.fillMaxWidth(0.6f),
                        onClick = {  openDialog.value = true }) {
                        Text(text = "Access Gallery or Camera")
                    }
                }
                //------------
                // Type
                //-------------
                Row {
                    Dropdown("Type", listOf("House", "Apartment","Manor"),typeState.value){
                        typeState.value = it
                    }
                }

                //------------
                // Status
                //-------------
                Row {
                    Dropdown("Status", listOf("Available", "Suspended","Sold"),statusState.value){
                        statusState.value = it
                    }
                }
                //------------
                // Interest
                //-------------
                if(openInterest.value) {
                    AlertToPickInterest()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Button(modifier = Modifier.fillMaxWidth(0.6f),
                        onClick = {  openInterest.value = true }) {
                        Text(text = "Select the interest")
                    }
                }
                //------------
                // Dates
                //-------------
                Row (
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    SelectDate("Date Of Entry",
                        mState = dateOfEntryStateString.value,
                        windowInfo = windowInfo
                    ){
                        dateOfEntryState.value = it
                        dateOfEntryStateString.value =  dateFormat.format(it)
                    }

                    if (statusState.value == "Sold"){
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        SelectDate("Date Of Sell",
                            mState = dateOfSellStateString.value,
                            windowInfo = windowInfo
                        ){
                            dateOfSellState.value = it
                            dateOfSellStateString.value =  dateFormat.format(it)
                        }
                    }
                }

                //------------
                // Add Button
                //-------------
                Button(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    onClick = {

                    if(statusState.value == "Sold"
                        && dateOfSellStateString.value.isNotEmpty()
                        && addressState.value.isNotEmpty()
                        && surfaceState.value.isNotEmpty()
                        && roomState.value.isNotEmpty()
                        && priceState.value.isNotEmpty()
                        || statusState.value != "Sold"
                        && addressState.value.isNotEmpty()
                        && surfaceState.value.isNotEmpty()
                        && roomState.value.isNotEmpty()
                        && priceState.value.isNotEmpty()
                    ){
                        if(dateOfEntryStateString.value.isEmpty()){
                            dateOfEntryState.value = null
                        }
                        if(dateOfSellStateString.value.isEmpty()){
                            dateOfSellState.value = null
                        }

                        // SAVE HOUSING IN ROOM
                        if(housingId == -999){
                            // INSERT HOUSING
                            val housing = HousingEntity(
                                description = descriptionState.value,
                                type = typeState.value,
                                address = addressState.value,
                                surface = surfaceState.value.toInt(),
                                room = roomState.value.toInt(),
                                price = priceState.value.toInt(),
                                status = statusState.value,
                                dateOfEntry = dateOfEntryState.value,
                                dateOfSell = dateOfSellState.value,
                                agentName = agentNameState.value
                            )
                            editViewModel.addHousing(housing)

                            editViewModel.housingId.observe(editActivity ){

                                Notification.sendNotification(context)

                                // SAVE INTERESTS IN ROOM
                                for (i in 0 until interestState.size){
                                    val interest = InterestEntity(
                                        interestName = interestState[i]
                                    )
                                    editViewModel.addInterest(interest)

                                    val housingInterest = HousingInterestCrossRef(
                                        roomId = it,
                                        interestName = interestState[i]
                                    )
                                    editViewModel.addHousingInterestCrossRef(housingInterest)
                                }

                                // SAVE PHOTOS IN ROOM
                                for (i in 0 until imagesState.size){

                                    val photo = PhotoEntity(
                                        caption = captionState[i],
                                        photo = imagesState[i],
                                        roomId = it
                                    )

                                    editViewModel.addPhoto(photo)
                                    editViewModel.photoId.observe(editActivity ) {

                                        if (i == imagesState.size-1){
                                            val navigateToMain = Intent(context, MainActivity::class.java)
                                            ContextCompat.startActivity(context, navigateToMain, null)
                                        }
                                    }
                                }


                                if (imagesState.isEmpty()){
                                    val navigateToMain = Intent(context, MainActivity::class.java)
                                    ContextCompat.startActivity(context, navigateToMain, null)
                                }

                            }
                        }else{
                            // UPDATE HOUSING
                            val housing = HousingEntity(
                                roomId = housingId.toLong(),
                                description = descriptionState.value,
                                type = typeState.value,
                                address = addressState.value,
                                surface = surfaceState.value.toInt(),
                                room = roomState.value.toInt(),
                                price = priceState.value.toInt(),
                                status = statusState.value,
                                dateOfEntry = dateOfEntryState.value,
                                dateOfSell = dateOfSellState.value,
                                agentName = agentNameState.value
                            )
                            if(listPhotoRoom!=null){
                                for(i in 0 until listPhotoRoom!!.size){
                                    editViewModel.deletePhoto(listPhotoRoom!![i])
                                }
                            }
                            editViewModel.updateHousing(housing)

                            // SAVE INTERESTS IN ROOM
                            for (i in 0 until interestState.size){
                                val interest = InterestEntity(
                                    interestName = interestState[i]
                                )
                                editViewModel.addInterest(interest)

                                val housingInterest = HousingInterestCrossRef(
                                    roomId = housingId.toLong(),
                                    interestName = interestState[i]
                                )
                                editViewModel.addHousingInterestCrossRef(housingInterest)
                            }

                            // SAVE PHOTOS IN ROOM
                            for (i in 0 until imagesState.size){
                                val photo = PhotoEntity(
                                    caption = captionState[i],
                                    photo = imagesState[i],
                                    roomId = housingId.toLong()
                                )
                                editViewModel.addPhoto(photo)
                                editViewModel.photoId.observe(editActivity) {
                                    if (i==imagesState.size -1){
                                        val navigateToMain = Intent(context, MainActivity::class.java)
                                        ContextCompat.startActivity(context, navigateToMain, null)
                                    }
                                }

                            }
                            if (imagesState.isEmpty()){
                                val navigateToMain = Intent(context, MainActivity::class.java)
                                ContextCompat.startActivity(context, navigateToMain, null)
                            }
                        }

                    }else{
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Fill in the fields")
                        }

                    }

                }){
                    if(housingId == -999){
                        Text(text = "Add")
                    }else{
                        Text(text = "Update")
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun ImageSelector(windowInfo: WindowInfo ) {

        LazyRow(modifier = Modifier.fillMaxWidth()){
            items(imagesState.size){
                if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded){
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp),
                    )
                    {

                        GlideImage(model = imagesState[it], contentDescription = null, contentScale = ContentScale.Crop)
                        Box(modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(4.dp),
                            contentAlignment = Alignment.TopEnd
                        ){
                            IconButton(onClick = {
                                imagesState.removeAt(it)
                                captionState.removeAt(it)
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Image")
                            }
                        }
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.BottomStart
                        ){
                            TextField(
                                value = captionState[it],
                                onValueChange = {value ->
                                    captionState[it] = value
                                },
                                textStyle = TextStyle(color = Color.Black),
                                singleLine = true,
                                label = { Text("Your caption") }
                            )
                        }
                    }
                }else{
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(250.dp)
                            .background(Color.LightGray),
                    )
                    {

                        GlideImage(model = imagesState[it], contentDescription = null, contentScale = ContentScale.Crop)
                        Box(modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(4.dp),
                            contentAlignment = Alignment.TopEnd
                        ){
                            IconButton(onClick = {
                                imagesState.removeAt(it)
                                captionState.removeAt(it)
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Image")
                            }
                        }
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
                            .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.BottomStart
                        ){
                            TextField(
                                value = captionState[it],
                                onValueChange = {value ->
                                    captionState[it] = value
                                },
                                textStyle = TextStyle(color = Color.Black),
                                singleLine = true,
                                label = { Text("Your caption") }
                            )
                        }
                    }
                }

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

        var expanded by remember{ mutableStateOf(false) }
        var textFiledSize by remember{ mutableStateOf(Size.Zero) }
        val windowInfo = rememberWindowInfo()

        val icon = if (expanded){
            Icons.Filled.KeyboardArrowUp
        }else{
            Icons.Filled.KeyboardArrowDown
        }

        Row {
            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded ){
                Button(
                    onClick = { expanded = !expanded },

                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .onGloballyPositioned { coordinates ->
                            textFiledSize = coordinates.size.toSize()
                        }
                ){
                    Text(statusState.ifEmpty {
                        label
                    })
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
            }else{
                Button(
                    onClick = { expanded = !expanded },

                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .onGloballyPositioned { coordinates ->
                            textFiledSize = coordinates.size.toSize()
                        }
                ){
                    Text(statusState.ifEmpty {
                        label
                    })
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
            }


            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current){textFiledSize.width.toDp()})
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

    @SuppressLint("SimpleDateFormat")
    @Composable
    fun SelectDate(
        label: String,
        mState: String,
        windowInfo: WindowInfo,
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

        if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded ){
            Column(modifier = Modifier.fillMaxSize(0.4f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                // Creating a button that on
                // click displays/shows the DatePickerDialog
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mDatePickerDialog.show()
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFA78467)) ) {
                    Text(
                        mState.ifEmpty { label } ,
                        fontSize = 11.sp
                    )
                }
            }
        }else{
            Column(modifier = Modifier.fillMaxSize(0.5f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                // Creating a button that on
                // click displays/shows the DatePickerDialog
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mDatePickerDialog.show()
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFA78467)) ) {
                    Text(
                        mState.ifEmpty { label } ,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    @Composable
    fun AlertToPickImages() {

        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Select :")
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        selectImageLauncher.launch("image/*")
                        openDialog.value = false
                    }) {
                        Text(text = "Gallery")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        shouldShowCamera.value = true
                        openDialog.value = false
                    }) {
                        Text(text = "Camera")
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
                        onClick = { openDialog.value = false }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        )
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

    private fun handleImageCapture(uri: Uri) {
        shouldShowCamera.value = false
        cameraExecutor.shutdown()
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}
