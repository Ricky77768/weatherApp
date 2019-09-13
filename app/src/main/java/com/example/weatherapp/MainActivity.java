package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Executes when starting/restarting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_ui);

        // Defines data URLs
        String dataURL = "http://api.openweathermap.org/data/2.5/weather?q=markham&APPID=1d5f8fb5ea2908f45395ceb41dcd1ab9";
        String forecastURL = "http://api.openweathermap.org/data/2.5/forecast?q=markham&mode=xml&appid=1d5f8fb5ea2908f45395ceb41dcd1ab9";

        // Starts Downloads and Adding weather Icons
        DownloadFileData downloadFile = new DownloadFileData();
        DownloadFileForecast downloadFileForecast = new DownloadFileForecast();
        downloadFile.execute(dataURL);
        downloadFileForecast.execute(forecastURL);
        Forecast.addIcons();
    }

    // -----------------CLASSES------------------ //

    // Adapter Class for RecycleView
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private Forecast[] fc;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView f_time;
            public TextView f_temp;
            public TextView f_weatherDesc;
            public TextView f_otherInfo;
            public ImageView f_img;

            public MyViewHolder(View v) {
                super(v);
                f_time = v.findViewById(R.id.f_time);
                f_temp = v.findViewById(R.id.f_temp);
                f_weatherDesc = v.findViewById(R.id.f_weatherDesc);
                f_otherInfo = v.findViewById(R.id.f_otherInfo);
                f_img = v.findViewById(R.id.f_img);
            }
        }

        // Provide a dataset
        public MyAdapter(Forecast[] myDataset) {
            fc = myDataset;
        }

        // Create new View (by Layout Manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_layout, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace a View (by Layout Manager) (position is the current index of dataset based on which View)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.f_time.setText(fc[position].startTime + " ~ " + fc[position].endTime);
            holder.f_temp.setText(fc[position].temp);
            holder.f_weatherDesc.setText(fc[position].weatherDesc);
            holder.f_otherInfo.setText(fc[position].precipValue + "mm of " + fc[position].precipType + "\n" + fc[position].windInfo + "\n" + fc[position].pressure + "\n" + fc[position].humidity + "\n" + fc[position].cloudCover);
            holder.f_img.setImageResource(Forecast.icons.get(fc[position].iconSuffix));
        }

        @Override
        public int getItemCount() {
            return fc.length;
        }
    }

    // Class for downloading current weather file
    private class DownloadFileData extends AsyncTask<String, Integer, String> {
        protected String doInBackground (String... urls) {
            int count = 0;
            try {
                URL url = new URL(urls[0].toString());
                URLConnection connection = url.openConnection();
                connection.connect();

                // Useful for progress bar
                int lengthOfFile = connection.getContentLength();

                // Download file from URL(s)
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/Current Weather.json");

                byte[] data = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {

                    // Publishing the progress. After this onProgressUpdate will be called
                    publishProgress(0);

                    // Write data to the file
                    output.write(data, 0, count);
                }

                // Flushing output and Closing streams
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Ricky", e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPreExecute() { }

        @Override
        protected void onProgressUpdate(Integer... values) { }

        @Override
        protected void onPostExecute(String s) {
            try {
                parseInfo();
                TextView test = findViewById(R.id.temp);
            } catch (Exception e) {
                Log.e("Ricky", e.getMessage());
            }
        }
    }

    // Class for downloading file
    private class DownloadFileForecast extends AsyncTask<String, Integer, String> {
        protected String doInBackground (String... urls) {
            int count = 0;
            try {
                URL url = new URL(urls[0].toString());
                URLConnection connection = url.openConnection();
                connection.connect();

                // Useful for progress bar
                int lengthOfFile = connection.getContentLength();

                // Download file from URL(s)
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/Forecast.xml");

                byte[] data = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {

                    // Publishing the progress. After this onProgressUpdate will be called
                    publishProgress(0);

                    // Write data to the file
                    output.write(data, 0, count);
                }

                // Flushing output and Closing streams
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Ricky", e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPreExecute() { }

        @Override
        protected void onProgressUpdate(Integer... values) { }

        @Override
        protected void onPostExecute(String s) {
            try {
                parseForecast();
            } catch (Exception e) {
                Log.e("Ricky", e.getMessage());
            }
        }
    }

    // -----------------FUNCTIONS------------------ //

    // Function to read current weather data
    private void parseInfo() throws Exception {
        String pattern = "###.#";
        DecimalFormat df = new DecimalFormat(pattern);
        String temp, minTemp, maxTemp, tempRanges, humidity, pressure, visibility, windInfo, cloudCover, iconSuffix, weatherDescription, place;

        // Convert file to String
        File dir = Environment.getExternalStorageDirectory();
        String path = dir.getAbsolutePath();
        File data = new File(path + "/Current Weather.json");
        FileInputStream iStream = new FileInputStream(data);
        String info = convertStreamToString(iStream);
        iStream.close();

        // Extract Data
        JSONObject reader = new JSONObject(info);
        visibility = "Visibility: " + String.valueOf(df.format(reader.getDouble("visibility") / 1000)) + "km";

        JSONObject main  = reader.getJSONObject("main");
        temp = String.valueOf(df.format(main.getDouble("temp") - 273.15) + "°C");
        minTemp = String.valueOf(df.format(main.getDouble("temp_min") - 273.15)) + "°C";
        maxTemp = String.valueOf(df.format(main.getDouble("temp_max") - 273.15)) + "°C";
        tempRanges = minTemp + " ~ " + maxTemp;
        humidity = "Humidity: " + main.getString("humidity") + "%";
        pressure = "Pressure: " + String.valueOf(df.format(main.getDouble("pressure") / 10)) + "kPa";

        JSONObject wind = reader.getJSONObject("wind");
        windInfo = "Wind: " + String.valueOf(df.format(wind.getDouble("speed") * 3.6)) + "km/h";

        JSONObject clouds = reader.getJSONObject("clouds");
        cloudCover = "Cloud Coverage: "+ clouds.getString("all") + "%";

        JSONArray weatherArr = reader.getJSONArray("weather");
        JSONObject weather = weatherArr.getJSONObject(0);
        iconSuffix = weather.getString("icon");
        weatherDescription = weather.getString("description");

        JSONObject sys = reader.getJSONObject("sys");
        place = reader.getString("name") + ", " +  sys.getString("country");

        // Display weather icon
        ImageView img = findViewById(R.id.img);
        img.setImageResource(Forecast.icons.get(iconSuffix));

        // Display Data
        TextView temperature = findViewById(R.id.temp);
        TextView tempRange = findViewById(R.id.tempRange);
        TextView weatherDesc = findViewById(R.id.weatherDesc);
        TextView otherInfo = findViewById(R.id.otherInfo);
        TextView location = findViewById(R.id.location);
        temperature.setText(temp);
        tempRange.setText(tempRanges);
        weatherDesc.setText(weatherDescription);
        location.setText(place);
        otherInfo.setText(humidity + "\n" + pressure + "\n" + visibility + "\n" + windInfo + "\n" + cloudCover);

        // Delete file on system
        data.delete();
    }

    // Function to read forecast data
    private void parseForecast() throws Exception {
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser xmlParser = xmlFactoryObject.newPullParser();

        File dir = Environment.getExternalStorageDirectory();
        String path = dir.getAbsolutePath();
        File data = new File(path + "/Forecast.xml");
        InputStream stream = new FileInputStream(data);
        xmlParser.setInput(stream, null);

        Forecast[] forecasts = new Forecast[40];
        String[] dataSet = new String[11];
        int index = 0;

        int event = xmlParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            String name = xmlParser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:
                    // Make it into case and switch
                    switch (name) {
                        case "time": {
                            dataSet[0] = xmlParser.getAttributeValue(null, "from");
                            dataSet[1] = xmlParser.getAttributeValue(null, "to");
                            break;
                        }
                        case "symbol": {
                            dataSet[2] = xmlParser.getAttributeValue(null, "name");
                            dataSet[3] = xmlParser.getAttributeValue(null, "var");
                            break;
                        }
                        case "precipitation": {
                            dataSet[4] = xmlParser.getAttributeValue(null, "value");
                            dataSet[5] = xmlParser.getAttributeValue(null, "type");
                            break;
                        }
                        case "windSpeed": {
                            dataSet[6] = xmlParser.getAttributeValue(null, "mps");
                            break;
                        }
                        case "temperature": {
                            dataSet[7] = xmlParser.getAttributeValue(null, "value");
                            break;
                        }
                        case "pressure": {
                            dataSet[8] = xmlParser.getAttributeValue(null, "value");
                            break;
                        }
                        case "humidity": {
                            dataSet[9] = xmlParser.getAttributeValue(null, "value");
                            break;
                        }
                        case "clouds": {
                            dataSet[10] = xmlParser.getAttributeValue(null, "all");
                            break;
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (name.equals("time")) {
                        forecasts[index] = new Forecast(dataSet);
                        index++;
                    }
                    break;
            }
            event = xmlParser.next();
        }

        RecyclerView forecast = findViewById(R.id.f_time);
        // Improves performance
        forecast.setHasFixedSize(true);

        // Create LayoutManager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        forecast.setLayoutManager(mLayoutManager);

        // Create/Specify Adapters
        RecyclerView.Adapter mAdapter = new MyAdapter(forecasts);
        forecast.setAdapter(mAdapter);

        // Delete File
        data.delete();
    }

    // Function to convert file to a String
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
