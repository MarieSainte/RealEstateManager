package com.example.realestatemanager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import com.example.realestatemanager.services.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;

@RunWith(JUnit4.class)
public class UtilsTest {

    @Test
    public void convertDollarToEuroTest() {
        assertEquals(8120, Utils.convertDollarToEuro(10000));
    }

    @Test
    public void getTodayDateTest() {

        LocalDate localDate = LocalDate.of(2000, 5, 10);
        Date date = Utils.convertToDateViaSqlDate(localDate);
        String dateString = date.toString();

        assertEquals('2',dateString.charAt(0) );
        assertEquals('1',Utils.getTodayDate(date).charAt(0));
    }

    @Test
    public void convertToDateViaSqlDateTest() {
        LocalDate dateToConvert = LocalDate.now();
        assertEquals(Date.class, Utils.convertToDateViaSqlDate(dateToConvert).getClass());
    }
}
