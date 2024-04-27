/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 17/02/2021
 */

package com.example.drivesafely;

//Import necessary dependencies

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationRequest;

import java.text.DecimalFormat;

//SpeedometerFragment is used to calculate distance travelled and current speed
public class SpeedometerFragment extends Fragment implements LocationListener {

    //Declare private variables
    private static final String TAG = "";
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    //Declare public variables
    public static double distance = 0;
    public static LocationManager lm;
    private Location mCurrentLocation, lStart, lEnd;
    private LocationRequest mLocationRequest;
    private TextView speedText;
    private boolean pip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SetupLocation();

        //Inflate the fragment_speedometer layout
        View view = inflater.inflate(R.layout.fragment_speedometer, container, false);
        speedText = view.findViewById(R.id.speedTextview);

        return view;

    }

    //Method used to get the setup the location requests and initialise the location manager
    @SuppressLint("MissingPermission")
    private void SetupLocation() {

        //Location request parameters are set and high accuracy is requested
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Location updates requested
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }



    //Method used to carry out operations when a user's location is changed
    @Override
    public void onLocationChanged(Location location) {

        //Used to calculate distance
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
        }
        lEnd = mCurrentLocation;

        //Call method to update the live values of distance and speed
        updateUI();
    }

    //The live feed of Distance and Speed are being set in the method below .
    private void updateUI() {

        //Calculate the distance value
        distance = distance + (lStart.distanceTo(lEnd) / 1000.00);

        if (distance < 100) {
            //Display distance with 1 decimal place if less than 100km
            EyeTracking.distance.setText(new DecimalFormat("#.#").format(distance) + " km");
        } else {
            //Display distance with 0 decimal places if greater than 100km
            EyeTracking.distance.setText(new DecimalFormat("#").format(distance) + " km");
        }
        Log.i(TAG, "Distance  " + distance);

        /*Calculating the speed with getSpeed method returns speed in m/s so we are converting it
        to km/h*/
        float nCurrentSpeed = mCurrentLocation.getSpeed() * 3.6f;
        //Update the speed textview with 0 decimal place format
        speedText.setText(String.format("%.0f", nCurrentSpeed) + " km/h");
        Log.i(TAG, "Speed  " + nCurrentSpeed);

        lStart = lEnd;

    }

    //Method used to stop getting location updates when the app is closed
    @Override
    public void onStop() {
        super.onStop();
        lm.removeUpdates(this);
        Log.i(TAG, "Location stopped");
    }

    @Override
    public void onPause() {
        pip = EyeTracking.inPipFragment;
        Log.i(TAG, "PIP is" + pip);
        super.onPause();
        if (pip) {

        } else {

            lm.removeUpdates(this);
            Log.i(TAG, "Location paused");
        }

    }

}

