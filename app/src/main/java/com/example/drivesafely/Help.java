/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 17/02/2021
 */

package com.example.drivesafely;

//Import necessary dependencies
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//Class used to create the help activity
public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the view to the activity_help XML files
        setContentView(R.layout.activity_help);
    }
}