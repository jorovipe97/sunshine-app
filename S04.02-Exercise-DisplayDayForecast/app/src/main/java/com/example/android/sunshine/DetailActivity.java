package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    TextView mWeatherTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_details);

        // TODO (2) Display the weather forecast that was passed from MainActivity
        Intent intentThatOpenedActivity = getIntent();
        if (intentThatOpenedActivity.hasExtra(MainActivity.EXTRA_WEATHER)) {
            String weatherData = intentThatOpenedActivity.getStringExtra(MainActivity.EXTRA_WEATHER);
            mWeatherTextView.setText(weatherData);
        }
    }
}