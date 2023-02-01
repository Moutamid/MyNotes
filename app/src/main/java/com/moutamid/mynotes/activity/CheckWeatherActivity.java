package com.moutamid.mynotes.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.animation.SpriteAnimatorBuilder;
import com.github.ybq.android.spinkit.sprite.CircleSprite;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.sprite.SpriteContainer;
import com.moutamid.mynotes.R;
import com.moutamid.mynotes.databinding.ActivityCheckWeatherBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CheckWeatherActivity extends AppCompatActivity {
    private static final String TAG = "CheckWeatherActivity";

    private ActivityCheckWeatherBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityCheckWeatherBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Sprite doubleBounce = new DoubleBouncee();

        b.spinKit.setIndeterminateDrawable(doubleBounce);

         /*YoYo.with(Techniques.Tada)
            .duration(7000)
            .repeat(Integer.MAX_VALUE)
            .playOn(b.micButton);*/

        b.micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float reducedvalue = (float) 0.9;
                        v.setScaleX(reducedvalue);
                        v.setScaleY(reducedvalue);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setScaleX(1);
                        v.setScaleY(1);
                        triggerVoiceApi();
                        break;
                }
                return true;
            }
        });


    }

    private void triggerVoiceApi() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to type :)");

        try {
            startActivityForResult(intent, 9999);
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(CheckWeatherActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9999) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                b.micLayout.setVisibility(View.GONE);
                b.loadingLayout.setVisibility(View.VISIBLE);
                b.micButton.setEnabled(false);
                fetchWeather(Objects.requireNonNull(result).get(0));
            }
        }
    }

    private void fetchWeather(String name) {
        Log.d(TAG, "fetchWeather: ");
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "fetchWeather: ERROR: "+e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CheckWeatherActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
        String finalName = name;
        Log.d(TAG, "fetchWeather: finalName: "+finalName);
        new Thread(() -> {
            Log.d(TAG, "fetchWeather: threadStarted");
            URL google = null;
            try {
                google = new URL("https://api.openweathermap.org/data/2.5/weather?q=" +
                        finalName +
                        "&units=metric&appid=6cab45541ef9f30d265545eb9ca0b563");
            } catch (final MalformedURLException e) {
                Log.d(TAG, "fetchWeather: ERROR: "+e.getMessage());

                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                Log.d(TAG, "fetchWeather: ERROR: "+e.getMessage());
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
                    Log.d(TAG, "fetchWeather: ERROR: "+e.getMessage());
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                Log.d(TAG, "fetchWeather: ERROR: "+e.getMessage());
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            Log.d(TAG, "fetchWeather: JsonObject fetched: "+htmlData);

            try {
                JSONObject jsonObject = new JSONObject(htmlData);

                double currentTemp =  jsonObject.getJSONObject("main").getDouble("temp");
                double maxTemp =  jsonObject.getJSONObject("main").getDouble("temp_max");
                int humidity = jsonObject.getJSONObject("main").getInt("humidity");

                String countryName = jsonObject.getJSONObject("sys").getString("country");
                double speed = jsonObject.getJSONObject("wind").getDouble("speed");

                String iconName = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String cityName = jsonObject.getString("name");
//
                runOnUiThread(() -> {
                    Glide.with(getApplicationContext())
                            .load("http://openweathermap.org/img/wn/" + iconName + "@2x.png")
                            .apply(new RequestOptions()
                                    .error(R.drawable.clearsky)
                            )
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(b.weatherImg);

                    b.loadingLayout.setVisibility(View.GONE);
                    b.finalLayout.setVisibility(View.VISIBLE);

                    b.currentTempTxt.setText(round(currentTemp) + "°");
                    b.countryNameTxt.setText(countryName + "");

                    b.humidityTxt.setText(humidity + "%");

                    b.windTxt.setText(round(speed) + "KM/H");

                    b.highTempTxt.setText(round(maxTemp) + "°");

                    b.cityNameTxt.setText(cityName + "");

                });


            } catch (JSONException e) {
                Log.d(TAG, "fetchWeather: ERROR: "+e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CheckWeatherActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

        }).start();
    }

    private static double round (double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }


    private String getCurrentDayName() {
        Format f = new SimpleDateFormat("EEEE");
        return f.format(new Date());
    }


    public class DoubleBouncee extends SpriteContainer {

        @Override
        public Sprite[] onCreateChild() {
            return new Sprite[]{
                    new Bounce(), new Bounce()
            };
        }

        @Override
        public void onChildCreated(Sprite... sprites) {
            super.onChildCreated(sprites);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sprites[1].setAnimationDelay(1000);
            } else {
                sprites[1].setAnimationDelay(-1000);
            }
        }

        private class Bounce extends CircleSprite {

            Bounce() {
                setAlpha(153);
                setScale(0f);
            }

            @Override
            public ValueAnimator onCreateAnimation() {
                float fractions[] = new float[]{0f, 0.5f, 1f};
                return new SpriteAnimatorBuilder(this).scale(fractions, 0f, 1f, 0f).
                        duration(7000).
                        easeInOut(fractions)
                        .build();
            }
        }
    }

}