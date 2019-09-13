package com.example.weatherapp;

import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Forecast {
    private String[] data;
    static HashMap<String, Integer> icons = new HashMap<String, Integer>();
    String startTime, endTime, weatherDesc, iconSuffix, precipValue, precipType, windInfo, temp, pressure, humidity, cloudCover;

    public Forecast(String[] data) {
        this.data = data;
        parseInfo();
    }

    private void parseInfo() {
        String pattern = "###.#";
        DecimalFormat df = new DecimalFormat(pattern);
        startTime = ">>>>>>> " + data[0].substring(5,10) + " " + data[0].substring(11);
        endTime = data[1].substring(5,10) + " " + data[1].substring(11) + " <<<<<<< ";
        weatherDesc = data[2];
        iconSuffix = data[3];
        if (data[4] != null) {
            precipValue = data[4];
        } else {
            precipValue = "0";
        }
        if (data[5] != null) {
            precipType = data[5];
        } else {
            precipType = "precip.";
        }
        windInfo = "Wind: " + (df.format(Double.valueOf(data[6]) * 3.6)).toString() + "km/h";
        temp = (df.format(Double.valueOf(data[7]) - 273.15)).toString() + "°C";
        pressure = "Pressure: " + (df.format(Double.valueOf(data[8]) / 10)).toString() + "kPa";
        humidity = "Humidity: " + data[9] + "%";
        cloudCover = "Cloud Cover: " + data[10] + "%";
    }

    public static void addIcons() {
        icons.put("01d", R.drawable.sunny_d);
        icons.put("01n", R.drawable.sunny_n);
        icons.put("02d", R.drawable.few_clouds_d);
        icons.put("02n", R.drawable.few_clouds_n);
        icons.put("03d", R.drawable.scattered_clouds);
        icons.put("03n", R.drawable.scattered_clouds);
        icons.put("04d", R.drawable.broken_clouds);
        icons.put("04n", R.drawable.broken_clouds);
        icons.put("09d", R.drawable.shower_rain);
        icons.put("09n", R.drawable.shower_rain);
        icons.put("10d", R.drawable.rain_d);
        icons.put("10n", R.drawable.rain_n);
        icons.put("11d", R.drawable.thunderstorm);
        icons.put("11n", R.drawable.thunderstorm);
        icons.put("13d", R.drawable.snow);
        icons.put("13n", R.drawable.snow);
        icons.put("50d", R.drawable.mist);
        icons.put("50n", R.drawable.mist);
    }
}