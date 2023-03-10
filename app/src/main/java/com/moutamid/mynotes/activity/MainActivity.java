package com.moutamid.mynotes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.moutamid.mynotes.Constants;
import com.moutamid.mynotes.R;
import com.moutamid.mynotes.databinding.ActivityMainBinding;
import com.moutamid.mynotes.models.WeatherDataModel;

import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
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
    ArrayList<String> tasksArrayList = Stash.getArrayList(Constants.NOTES_LIST, String.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Constants.checkApp(this);

        Sprite doubleBounce = new Wave();
        b.spinKit.setIndeterminateDrawable(doubleBounce);

        if (tasksArrayList.size() == 0) {
            tasksArrayList.add(getDateTime() + "\n" + "(test note) My saturday was going pretty well until i realized it was sunday :(");
        }

        b.notesBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int duration = 300;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v,
                                "scaleX", 0.8f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v,
                                "scaleY", 0.8f);
                        scaleDownX.setDuration(duration);
                        scaleDownY.setDuration(duration);

                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);

                        scaleDown.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(
                                v, "scaleX", 1f);
                        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(
                                v, "scaleY", 1f);
                        scaleDownX2.setDuration(duration);
                        scaleDownY2.setDuration(duration);

                        AnimatorSet scaleDown2 = new AnimatorSet();
                        scaleDown2.play(scaleDownX2).with(scaleDownY2);

                        scaleDown2.start();
                        startActivity(new Intent(MainActivity.this, NotesActivity.class));

                        break;
                }
                return true;
            }
        });

        b.checkWeatherBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int duration = 300;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v,
                                "scaleX", 0.8f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v,
                                "scaleY", 0.8f);
                        scaleDownX.setDuration(duration);
                        scaleDownY.setDuration(duration);

                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);

                        scaleDown.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(
                                v, "scaleX", 1f);
                        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(
                                v, "scaleY", 1f);
                        scaleDownX2.setDuration(duration);
                        scaleDownY2.setDuration(duration);

                        AnimatorSet scaleDown2 = new AnimatorSet();
                        scaleDown2.play(scaleDownX2).with(scaleDownY2);

                        scaleDown2.start();

                        startActivity(new Intent(MainActivity.this, CheckWeatherActivity.class));
                        break;
                }
                return true;
            }
        });

        b.weatherIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int duration = 300;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v,
                                "scaleX", 0.8f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v,
                                "scaleY", 0.8f);
                        scaleDownX.setDuration(duration);
                        scaleDownY.setDuration(duration);

                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);

                        scaleDown.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(
                                v, "scaleX", 1f);
                        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(
                                v, "scaleY", 1f);
                        scaleDownX2.setDuration(duration);
                        scaleDownY2.setDuration(duration);

                        AnimatorSet scaleDown2 = new AnimatorSet();
                        scaleDown2.play(scaleDownX2).with(scaleDownY2);

                        scaleDown2.start();

                        break;
                }
                return true;
            }
        });


        initRecyclerView(true);

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

                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject firstObject = weatherArray.getJSONObject(0);
                String iconName = firstObject.getString("icon");

//                Double maxTemp = mainObject.getDouble("temp_max");
                float currentTemp = (float) mainObject.getDouble("temp");

                runOnUiThread(() -> {
                    b.currentTempTxt.setText(currentTemp + "");

                    Glide.with(getApplicationContext())
                            .load("http://openweathermap.org/img/wn/" + iconName + "@2x.png")
                            .apply(new RequestOptions()
                                    .error(R.drawable.clearsky)
                            )
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(b.weatherIcon);
                });

                ValueLineSeries series = new ValueLineSeries();
                series.setColor(0xFFF6C15A);

                ArrayList<WeatherDataModel> list = Stash.getArrayList(Constants.WEATHER_LIST, WeatherDataModel.class);

                // IF SIZE 0 THEN ADD ITEMS BEFORE
                if (list.size() == 0) {
                    list.add(new WeatherDataModel("", 0));
                    list.add(new WeatherDataModel("", currentTemp / 2));
                    list.add(new WeatherDataModel("", 0));
                }

                int count = 0;
                for (WeatherDataModel weatherDataModel : list) {
                    if (weatherDataModel.day.equals(getCurrentDayName())) {
                        count++;
                    }
                }
                if (count == 0)
                    list.add(new WeatherDataModel(getCurrentDayName(), currentTemp));

                // IF SIZE 4 THEN ADD ITEMS AFTER
                if (list.size() == 4) {
                    list.add(new WeatherDataModel("", 0));
                    list.add(new WeatherDataModel("", currentTemp / 2));
                    list.add(new WeatherDataModel("", 0));
                }

                for (int i = 0; i < list.size(); i++) {
                    series.addPoint(new ValueLinePoint(list.get(i).day, list.get(i).degree));
                }

                Stash.put(Constants.WEATHER_LIST, list);

                runOnUiThread(() -> {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            b.spinKit.setVisibility(View.GONE);
                            b.frontLayout.setVisibility(View.VISIBLE);

                            b.cubiclinechart.addSeries(series);
                            b.cubiclinechart.startAnimation();
                        }
                    }, 3000);
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

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView(boolean isFirstTime) {

        conversationRecyclerView = findViewById(R.id.mainRv);
        //conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        if (isFirstTime)
            initChart();
    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public RecyclerViewAdapterMessages.ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_notes, parent, false);
            return new RecyclerViewAdapterMessages.ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapterMessages.ViewHolderRightMessage holder, int position1) {

            int position = holder.getAdapterPosition();

            holder.title.setText(tasksArrayList.get(position));

            holder.title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Are you sure?")
                            .setMessage("Do you really want to delete this note?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    tasksArrayList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Stash.put(Constants.NOTES_LIST, tasksArrayList);

                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                    return false;
                }
            });

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int duration = 300;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v,
                                    "scaleX", 0.8f);
                            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v,
                                    "scaleY", 0.8f);
                            scaleDownX.setDuration(duration);
                            scaleDownY.setDuration(duration);

                            AnimatorSet scaleDown = new AnimatorSet();
                            scaleDown.play(scaleDownX).with(scaleDownY);

                            scaleDown.start();
                            break;

                        case MotionEvent.ACTION_UP:
                            ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(
                                    v, "scaleX", 1f);
                            ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(
                                    v, "scaleY", 1f);
                            scaleDownX2.setDuration(duration);
                            scaleDownY2.setDuration(duration);

                            AnimatorSet scaleDown2 = new AnimatorSet();
                            scaleDown2.play(scaleDownX2).with(scaleDownY2);

                            scaleDown2.start();

                            break;
                    }
                    return true;
                }
            });


        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView title;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.titleTextview);

            }
        }

    }

    private String getDateTime() {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    protected void onResume() {
        super.onResume();
        tasksArrayList = Stash.getArrayList(Constants.NOTES_LIST, String.class);

        if (tasksArrayList.size() == 0) {
            tasksArrayList.add(getDateTime() + "\n" + "(test note) My saturday was going pretty well until i realized it was sunday :(");
        }
        initRecyclerView(false);
    }
}