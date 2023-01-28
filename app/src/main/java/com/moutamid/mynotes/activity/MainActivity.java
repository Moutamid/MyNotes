package com.moutamid.mynotes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Build;
import android.os.Bundle;

import com.fxn.stash.Stash;
import com.moutamid.mynotes.Constants;
import com.moutamid.mynotes.databinding.ActivityMainBinding;
import com.moutamid.mynotes.models.WeatherDataModel;

import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        initChart();

    }

    private void initChart() {
        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://api.openweathermap.org/data/2.5/weather?q=lahore&units=metric&appid=6cab45541ef9f30d265545eb9ca0b563");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if ((input = in != null ? in.readLine() : null) == null) break;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject jsonObject = new JSONObject(htmlData);

                JSONObject mainObject = jsonObject.getJSONObject("main");

//                Double maxTemp = mainObject.getDouble("temp_max");
                float currentTemp = (float) mainObject.getDouble("temp");

                ValueLineSeries series = new ValueLineSeries();
                series.setColor(0xFF56B7F1);

                ArrayList<WeatherDataModel> list = Stash.getArrayList(Constants.WEATHER_LIST, WeatherDataModel.class);

                list.add(new WeatherDataModel(getCurrentDayName(), currentTemp));

                series.addPoint(new ValueLinePoint("", 0));

                for (int i = 0; i < list.size(); i++) {
                    series.addPoint(new ValueLinePoint(list.get(i).day, list.get(i).degree));
                }

                series.addPoint(new ValueLinePoint("", 0));

                runOnUiThread(() -> {
                    b.cubiclinechart.addSeries(series);
                    b.cubiclinechart.startAnimation();
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private String getCurrentDayName() {
        Format f = new SimpleDateFormat("EEEE");
        return f.format(new Date());
    }

}