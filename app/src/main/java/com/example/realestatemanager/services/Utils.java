package com.example.realestatemanager.services;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.util.Log;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Philippe on 21/02/2018.
 */
@ProvidedTypeConverter
public class Utils {


    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }

    public static String getTodayDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static Boolean isInternetAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null){
            return cm.getActiveNetwork() != null;
        }
        return false;
    }

    public static Date convertToDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(String.valueOf(dateToConvert));
    }

    public static LatLng getLocationFromAddress(String strAddress, Context context) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng  loc;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            loc = new LatLng (location.getLatitude(),   location.getLongitude());
            return loc;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TypeConverter
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @TypeConverter
    public static Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static String fromArrayListBitmap(ArrayList<Bitmap> list) {
        ArrayList<String> stringList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(BitMapToString(list.get(i))) ;
        }

        Gson gson = new Gson();
        return gson.toJson(stringList);
    }

    @TypeConverter
    public static ArrayList<Bitmap> fromStringToBitmapList(String value) {
        ArrayList<String> stringList;
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        stringList = fromString(value);
        for (int i = 0; i < stringList.size(); i++) {
            bitmapList.add(StringToBitMap(stringList.get(i))); ;
        }
        return bitmapList;
    }

}

