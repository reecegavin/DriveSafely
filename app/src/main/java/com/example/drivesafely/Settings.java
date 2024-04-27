/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 13/02/2021
 */

package com.example.drivesafely;

//Import necessary dependencies

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;

//Settings class enables the user to change application settings
public class Settings extends AppCompatActivity {

    //Declare public variables
    public static final String TONE_PREF =null ;
    public static final String TIME_PREF ="Hello" ;
    private static final String TAG = "EyeTracker";
    public static int time;
    public static int tone;

    //Declare private variables
    private static SeekBar sBar;
    private static TextView timeTextView;
    private static int progress;
    private static int position;
    private Spinner toneSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout to the settings XML file
        setContentView(R.layout.activity_settings);

       /* SeekBar, TextView and Spinner are found in the layout using findViewById.
        This allows the values of each to be changed programmatically.*/
        sBar = findViewById(R.id.seekBarSettings);
        timeTextView = findViewById(R.id.timeText);
        toneSpinner = findViewById(R.id.spinner);

        //Load previously saved time and tone data
        loadTimeData();
        loadToneData();
        Log.i(TAG, "After Loadata Time is  " + time);
        Log.i(TAG, "After Loadata progress is  " + progress);

        //Set the time TextView to display the correct amount of seconds
        timeTextView.setText(time + " Seconds");

        //Switch statement used to set the progress of the seekbar for a corresponding time
        switch (time) {
            //If time is equal to 2 seconds, set progress to 0
            case 2:
                progress = 0;
                break;
            //If time is equal to 3 seconds, set progress to 1
            case 3:
                progress = 1;
                break;
            //If time is equal to 4 seconds, set progress to 2
            case 4:
                progress = 2;
                break;
        }

        //Set the seekbar progress after the switch statement has executed
        sBar.setProgress(progress);
        Log.i(TAG, "After switch statement progress is  " + progress);


        //Insert the ChooseContact fragment into the settings page
        Fragment fragment;
        fragment = new ChooseContact();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.contactlist, fragment).commit();

        //Create an ArrayList and ArrayAdapter to allow selection of different tones
        String[] value = {"Missile Alert", "Railroad", "Police Siren"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(value));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.style_spinner, arrayList);
        toneSpinner.setAdapter(arrayAdapter);
        toneSpinner.setSelection(position);
        Log.i(TAG, "spinner  " + position);

        //listener used to set the alarm tone for a certain ArrayList position
        toneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    tone = 0;
                }
                if (position == 1) {
                    tone = 1;
                }
                if (position == 2) {
                    tone = 2;
                }
                Settings.position = position;
            }

            //Do nothing if nothing is selected
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //SeekBar listener used to change the value of time and Seekbar position
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //Set value of time and progress
                time = progress + 2;
                Settings.progress = progress;
                timeTextView.setText(time + " Seconds");
                Log.i(TAG, "Time is saved as " + time);
            }

            //Do nothing
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            //Do nothing
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }

    //When the back button is pressed, save the time and tone data to their shared preferences
    @Override
    public void onBackPressed() {
        saveTimeData();
        saveToneData();
        finish();
    }

    //Method used to save the value of time to its relative shared preference
    private void saveTimeData() {
        SharedPreferences sharedPreferences = getSharedPreferences(TIME_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TIME_PREF, time);
        editor.commit();
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "(SavedData) Time is " + time);
    }

    //Method used to load the value of time from its relative shared preference
    public void loadTimeData() {
        SharedPreferences sharedPreferences = getSharedPreferences(TIME_PREF, MODE_PRIVATE);
        time = sharedPreferences.getInt(TIME_PREF, 3);
        Log.i(TAG, "(LoadData) Time is " + time);

    }
    //Method used to save the tone value to its relative shared preference
    private void saveToneData() {
        SharedPreferences sharedPreferences3 = getSharedPreferences(TONE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences3.edit();
        editor.putInt(TONE_PREF, position);
        editor.apply();
        Log.i(TAG, "(SavedData) Position is " + position);
    }

    //Method used to load the tone value to its relative shared preference
    private void loadToneData() {
        SharedPreferences sharedPreferences = getSharedPreferences(TONE_PREF, MODE_PRIVATE);
        position = sharedPreferences.getInt(TONE_PREF, 0);
        Log.i(TAG, "(LoadData) Position " + position);
    }

}

