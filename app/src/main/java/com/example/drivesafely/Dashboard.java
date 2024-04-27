/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 17/02/2021
 */

package com.example.drivesafely;

//Import all necessary imports
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

//Class used to create the main dashboard of the application
public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout to activity_dashboard.XML
        setContentView(R.layout.activity_dashboard);
        TextView txt = (TextView) findViewById(R.id.custom_fonts);
        txt.setTextSize(24);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/slim_joe.otf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/big_john.otf");
        SpannableStringBuilder SS = new SpannableStringBuilder("DriveSafely");
        SS.setSpan (new com.example.drivesafely.CustomTypefaceSpan("", font), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan (new com.example.drivesafely.CustomTypefaceSpan("", font2), 5, 11,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        txt.setText(SS);


        //Create 4 CardView Variables to be used to display the different cards
        CardView startCard = findViewById(R.id.start_tracking);
        CardView recentCard = findViewById(R.id.recent_trips);
        CardView settingsCard = findViewById(R.id.settings);
        CardView helpCard = findViewById(R.id.help);

        //Add Click listener to the cards
        startCard.setOnClickListener(this);
        recentCard.setOnClickListener(this);
        settingsCard.setOnClickListener(this);
        helpCard.setOnClickListener(this);

    }

    /*Create a method for when a card is clicked a new intent is created and the relevant activity
    is started*/
    @Override
    public void onClick(View view) {

        //Create new intent i
        Intent i;

        //Switch statement used to start the selected cards activity
        switch (view.getId()) {
            case R.id.start_tracking:
                i = new Intent(this, EyeTracking.class);
                startActivity(i);
                break;
            case R.id.recent_trips:
                i = new Intent(this, RecentTrips.class);
                startActivity(i);
                break;
            case R.id.settings:
                i = new Intent(this, Settings.class);
                startActivity(i);
                break;
            case R.id.help:
                i = new Intent(this, Help.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}