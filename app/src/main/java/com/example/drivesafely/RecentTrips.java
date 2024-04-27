/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 17/02/2021
 */

package com.example.drivesafely;

//Import necessary dependencies

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;


public class RecentTrips extends AppCompatActivity {

    //Declare private variables
    private static final String TAG = "";
    private TextView timeTextOne;
    private TextView timeTextTwo;
    private TextView timeTextThree;
    private TextView detectTextOne;
    private TextView detectTextTwo;
    private TextView detectTextThree;
    private TextView speedTextOne;
    private TextView speedTextTwo;
    private TextView speedTextThree;
    private TextView distanceTextOne;
    private TextView distanceTextTwo;
    private TextView distanceTextThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent__trips);

        /*CardViews and TextViews are found in the layout using findViewById.
        This allows the values of each to be changed programmatically.*/
        CardView tripOne = findViewById(R.id.tripOne);
        CardView tripTwo = findViewById(R.id.tripTwo);
        CardView tripThree = findViewById(R.id.tripThree);
        timeTextOne = findViewById(R.id.timeTextOne);
        timeTextTwo = findViewById(R.id.timeTextTwo);
        timeTextThree = findViewById(R.id.timeTextThree);
        detectTextOne = findViewById(R.id.detectTextOne);
        detectTextTwo = findViewById(R.id.detectTextTwo);
        detectTextThree = findViewById(R.id.detectTextThree);
        speedTextOne = findViewById(R.id.speedTextOne);
        speedTextTwo = findViewById(R.id.speedTextTwo);
        speedTextThree = findViewById(R.id.speedTextThree);
        distanceTextOne = findViewById(R.id.distanceTextOne);
        distanceTextTwo = findViewById(R.id.distanceTextTwo);
        distanceTextThree = findViewById(R.id.distanceTextThree);

        //The last three trips data are loaded when the activity is started
        loadTripData();
    }

    //Method used to load the data for each trip
    public void loadTripData() {

        //Declare variables
        int timeTrip;
        int detectTrip;
        float speedTrip;
        float distanceTrip;
        float distanceTrip2;
        float distanceTrip3;

        //SharedPreference to load value of time for trip one
        SharedPreferences spTimeT1 = getSharedPreferences(EyeTracking.TIME_TRIP_ONE_PREF, MODE_PRIVATE);
        timeTrip = spTimeT1.getInt((EyeTracking.TIME_TRIP_ONE_PREF), 0);
        String timeTripOne = stringFormatter(timeTrip);
        timeTextOne.setText(timeTripOne);

        //SharedPreference to load value of time for trip two
        SharedPreferences spTimeT2 = getSharedPreferences(EyeTracking.TIME_TRIP_TWO_PREF, MODE_PRIVATE);
        timeTrip = spTimeT2.getInt((EyeTracking.TIME_TRIP_TWO_PREF), 0);
        String timeTripTwo = stringFormatter(timeTrip);
        timeTextTwo.setText(timeTripTwo);

        //SharedPreference to load value of time for trip three
        SharedPreferences spTimeT3 = getSharedPreferences(EyeTracking.TIME_TRIP_THREE_PREF, MODE_PRIVATE);
        timeTrip = spTimeT3.getInt((EyeTracking.TIME_TRIP_THREE_PREF), 0);
        String timeTripThree = stringFormatter(timeTrip);
        timeTextThree.setText(timeTripThree);

        //SharedPreference to load value of detections for trip one
        SharedPreferences spDetect1 = getSharedPreferences(EyeTracking.DETECT_TRIP_ONE_PREF, MODE_PRIVATE);
        detectTrip = spDetect1.getInt((EyeTracking.DETECT_TRIP_ONE_PREF), 0);
        detectTextOne.setText(String.valueOf(detectTrip));

        //SharedPreference to load value of detections for trip two
        SharedPreferences spDetect2 = getSharedPreferences(EyeTracking.DETECT_TRIP_TWO_PREF, MODE_PRIVATE);
        detectTrip = spDetect2.getInt((EyeTracking.DETECT_TRIP_TWO_PREF), 0);
        detectTextTwo.setText(String.valueOf(detectTrip));

        //SharedPreference to load value of detections for trip three
        SharedPreferences spDetect3 = getSharedPreferences(EyeTracking.DETECT_TRIP_THREE_PREF, MODE_PRIVATE);
        detectTrip = spDetect3.getInt((EyeTracking.DETECT_TRIP_THREE_PREF), 0);
        detectTextThree.setText(String.valueOf(detectTrip));

        //SharedPreference to load value of speed for trip one
        SharedPreferences spSpeed1 = getSharedPreferences(EyeTracking.SPEED_TRIP_ONE_PREF, MODE_PRIVATE);
        speedTrip = spSpeed1.getFloat((EyeTracking.SPEED_TRIP_ONE_PREF), 0);
        speedTextOne.setText(String.format("%.0f", speedTrip) + " km/h");
        Log.i(TAG, "speed one is " + EyeTracking.SPEED_TRIP_ONE_PREF);

        //SharedPreference to load value of speed for trip two
        SharedPreferences spSpeed2 = getSharedPreferences(EyeTracking.SPEED_TRIP_TWO_PREF, MODE_PRIVATE);
        speedTrip = spSpeed2.getFloat((EyeTracking.SPEED_TRIP_TWO_PREF), 0);
        speedTextTwo.setText(String.format("%.0f", speedTrip) + " km/h");

        //SharedPreference to load value of speed for trip three
        SharedPreferences spSpeed3 = getSharedPreferences(EyeTracking.SPEED_TRIP_THREE_PREF, MODE_PRIVATE);
        speedTrip = spSpeed3.getFloat((EyeTracking.SPEED_TRIP_THREE_PREF), 0);
        speedTextThree.setText(String.format("%.0f", speedTrip) + " km/h");

        //SharedPreference to load value of distance for trip one
        SharedPreferences spDistance1 = getSharedPreferences(EyeTracking.DISTANCE_TRIP_ONE_PREF, MODE_PRIVATE);
        distanceTrip = spDistance1.getFloat((EyeTracking.DISTANCE_TRIP_ONE_PREF), 0);
        Log.i(TAG, "Dist1" + distanceTrip);
        distanceTextOne.setText(new DecimalFormat("#.#").format(distanceTrip) + " km");

        //SharedPreference to load value of distance for trip two
        SharedPreferences spDistance2 = getSharedPreferences(EyeTracking.DISTANCE_TRIP_TWO_PREF, MODE_PRIVATE);
        distanceTrip2 = spDistance2.getFloat((EyeTracking.DISTANCE_TRIP_TWO_PREF), 0);
        distanceTextTwo.setText(new DecimalFormat("#.#").format(distanceTrip2) + " km");

        //SharedPreference to load value of distance for trip three
        SharedPreferences spDistance3 = getSharedPreferences(EyeTracking.DISTANCE_TRIP_THREE_PREF, MODE_PRIVATE);
        distanceTrip3 = spDistance3.getFloat((EyeTracking.DISTANCE_TRIP_THREE_PREF), 0);
        distanceTextThree.setText(new DecimalFormat("#.#").format(distanceTrip3) + " km");

    }

    //Method used to properly format the time data
    private String stringFormatter(int trip) {
        @SuppressLint("DefaultLocale")
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(trip),
                TimeUnit.MILLISECONDS.toMinutes(trip) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(trip)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(trip) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trip)));
        return hms;
    }


}
