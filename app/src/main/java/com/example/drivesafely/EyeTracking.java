/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 16/03/2021
 */

package com.example.drivesafely;

//Import necessary dependencies

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//Class used to implement the eye/face tracking and other associated methods.
public final class EyeTracking extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //Declare public variables
    public static final String TIME_TRIP_ONE_PREF = "tripOne";
    public static final String TIME_TRIP_TWO_PREF = "tripTWo";
    public static final String TIME_TRIP_THREE_PREF = "tripThree";
    public static final String DETECT_TRIP_ONE_PREF = "detectTripOne";
    public static final String DETECT_TRIP_TWO_PREF = "detectTripTWo";
    public static final String DETECT_TRIP_THREE_PREF = "detectTripThree";
    public static final String SPEED_TRIP_ONE_PREF = "speedTripOne";
    public static final String SPEED_TRIP_TWO_PREF = "speedTripTWo";
    public static final String SPEED_TRIP_THREE_PREF = "speedTripThree";
    public static final String DISTANCE_TRIP_ONE_PREF = "distanceTripOne";
    public static final String DISTANCE_TRIP_TWO_PREF = "distanceTripTWo";
    public static final String DISTANCE_TRIP_THREE_PREF = "distanceTripThree";
    public static final String TRIP_VALUE_PREF = "trip3";
    //Declare private variables
    private static final String TAG = "Log";
    public static TextView distance;
    public static int tripValue;
    public static boolean inPipFragment;
    public int detections = 0;
    public Chronometer journeyTimer;
    public int numbOfBlinks;
    public int flag = 0;
    public boolean yellowAlert = true;
    public boolean facePresent = false;
    public FragmentManager fm;
    public FragmentTransaction ft;
    public Fragment fragment;
    public AlertDialog greenAlertDialog;
    public boolean isOnPause = false;
    private CameraSource mCameraSource = null;
    private MediaPlayer mp;
    private Vibrator vibrator;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private FrameLayout frameLayout;
    private int alarmTime;
    private int alarmTone;
    private String emergencyContact;
    private ConstraintLayout.LayoutParams params;
    private CardView previewCard, speedCard, journeyCard, distanceCard, detectionCard, logoCard;
    private TextView detectionsTextview;
    private Timer yellowAlertTimer;
    private TimerTask yellowAlertTimeTask;
    private boolean greenFirstTime = true;
    private int headAngle = 0;
    private float head_angle;
    private float updateHeadAngle;

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Layout is set to activity_eye_tracking
        setContentView(R.layout.activity_eye_tracking);

       /* Buttons, TextViews and CardViews are found in the layout using findViewById.
        This allows the values of each to be changed programmatically.*/
        Button openMapsBtn = findViewById(R.id.mapButton);
        ToggleButton previewBtn = findViewById(R.id.previewButton);
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        frameLayout = findViewById(R.id.previewFrame);
        speedCard = findViewById(R.id.speedCard);
        journeyCard = findViewById(R.id.journeyCard);
        distanceCard = findViewById(R.id.DistanceCard);
        detectionCard = findViewById(R.id.DetectionsCard);
        params = (ConstraintLayout.LayoutParams) frameLayout.getLayoutParams();
        previewCard = findViewById(R.id.previewCard);
        distance = findViewById(R.id.distancemain);
        detectionsTextview = findViewById(R.id.detections_text);
        logoCard = findViewById(R.id.logoCard);
        previewBtn.setTextOff("Turn Preview Off");
        previewBtn.setTextOn("Turn Preview On");
        journeyTimer = findViewById(R.id.journeyTimer);

        //Run methods shown
        showPositionDialog();
        loadTripValue();

        //Start the speedometerFragment and add its view to fragment_speedo in activity_eye_tracking
        fragment = new SpeedometerFragment();
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.fragmentSpeedo, fragment).commit();

        //Initialise chronometer and start
        journeyTimer.setBase(SystemClock.elapsedRealtime());
        journeyTimer.start();

        // For logging and testing purposes
        Log.i(TAG, "Before calling shared preferences " + alarmTime);

        // Get the value stored in Settings.TIME_PREF . If no value found, default is 3 seconds
        SharedPreferences sharedPreferencesTime = getSharedPreferences(Settings.TIME_PREF, MODE_PRIVATE);
        alarmTime = sharedPreferencesTime.getInt(Settings.TIME_PREF, 3) * 1000;
        Log.i(TAG, "Alarm time is: " + alarmTime);

        // Get the value stored in Settings.TONE_PREF . If no value found, default is tone 0 - Missile Alert
        SharedPreferences sharedPreferencesTone = getSharedPreferences(Settings.TONE_PREF, MODE_PRIVATE);
        alarmTone = sharedPreferencesTone.getInt(Settings.TONE_PREF, 0);

        //Attempt to get the chosen emergency contact. If no contact chosen. exception is thrown
        try {
            // For logging and testing purposes
            Log.i(TAG, "Before calling shared number " + emergencyContact);

            SharedPreferences sharedPreferencesContact = getSharedPreferences(ChooseContact.CONTACT_PREF, MODE_PRIVATE);
            emergencyContact = sharedPreferencesContact.getString(ChooseContact.CONTACT_PREF, "");

            // For logging and testing purposes
            Log.i(TAG, "Number is  " + emergencyContact);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Start Google Maps in picture-in-picture mode when openMapsBtn is clicked
        openMapsBtn.setOnClickListener(v -> {
            startPIP();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?"));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });


        previewBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            /*If previewBtn is checked, hide the preview and its associated card view
             and display toast message. Show the drivesafely logo*/
            if (isChecked) {
                mPreview.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Increase Brightness to maximum for higher accuracy", Toast.LENGTH_SHORT).show();
                previewCard.setVisibility(View.INVISIBLE);
                logoCard.setVisibility(View.VISIBLE);

                /*If previewBtn is not checked, hide the DriveSafely logo. The preview and its
                 * associated card view are visible.*/
            } else {
                mPreview.setVisibility(View.VISIBLE);
                previewCard.setVisibility(View.VISIBLE);
                logoCard.setVisibility(View.INVISIBLE);
            }
        });

        //Declare an audio manager and set the volume to half the max volume
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        // If volume is set to 0 by user, display Toast message
        if (volume == 0) {
            Toast.makeText(getApplicationContext(), "Volume is MUTE", Toast.LENGTH_LONG).show();
        }

        //Call method requestPermissions
        requestPermissions();

    }

    // Method to start checking if a user is blinking enough
    private void checkForBlinks() {
        yellowAlertTimer = new Timer();

        /*This method is used to trigger a yellow alert if the number of blinks
        in a minute is less than 4*/
        yellowAlertTimeTask = new TimerTask() {
            @Override
            public void run() {
                if (facePresent) {
                    yellowAlertTimer = new Timer();
                    if (numbOfBlinks < 5 && yellowAlert && !isOnPause) {
                        yellowAlert = false;
                        yellowAlertBox();
                    }
                }
                numbOfBlinks = 0;
            }

        };
        //The timer is scheduled to run every 60 seconds with an initial delay of 60 seconds
        yellowAlertTimer.schedule(yellowAlertTimeTask, 60 * 1000, 60 * 1000);
    }

    // Method to stop checking if a user is blinking enough
    private void stopCheckForBlinks() {

        yellowAlertTimeTask.cancel();
        yellowAlertTimer.cancel();
        yellowAlertTimer.purge();
    }

    // Method to assign a new value to updateHeadAngle every 3 seconds
    private void calcHeadAngle() {
        Timer headAngleTimer = new Timer();

        TimerTask angleTimerTask = new TimerTask() {
            @Override
            public void run() {
                updateHeadAngle = head_angle;
            }
        };
        //The timer is scheduled to run every 3 seconds with an initial delay of 3 seconds
        headAngleTimer.schedule(angleTimerTask, 3 * 1000, 3 * 1000);
    }


    /* Method used to call the dialog box which shows users how they should position their phone
     for optimal eye and face tracking*/
    private void showPositionDialog() {
        //Create new dialog and initialise views
        final Dialog dialog = new Dialog(EyeTracking.this);
        dialog.setCanceledOnTouchOutside(false);
        ConstraintLayout xp = findViewById(R.id.backgroundDialog);
        dialog.setContentView(R.layout.placement_dialog);
        final Button button = dialog.findViewById(R.id.okBtn);

        //Make the dialog box visible
        dialog.show();


        //Dismiss dialog when button is clicked
        button.setOnClickListener((v) -> {
            dialog.dismiss();
        });

    }

    //Method used to change alter the layout when picture-in-picture (PIP) mode is activated
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {

        super.onPictureInPictureModeChanged(isInPictureInPictureMode);

        /*If the application is changed to PIP mode, different buttons and cards are set to invisible
        The camera preview is set to occupy the full width and height of the PIP window*/
        if (isInPictureInPictureMode) {
            mPreview.setVisibility(View.VISIBLE);
            previewCard.setVisibility(View.VISIBLE);
            logoCard.setVisibility(View.INVISIBLE);
            previewCard.setRadius(0);
            ConstraintLayout.LayoutParams layPra = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            frameLayout.setLayoutParams(layPra);
            distanceCard.setVisibility(View.INVISIBLE);
            detectionCard.setVisibility(View.INVISIBLE);
            journeyCard.setVisibility(View.INVISIBLE);
            speedCard.setVisibility(View.INVISIBLE);

        }
        //If PIP mode is exited, the application is reset to it's normal view
        else {
            distanceCard.setVisibility(View.VISIBLE);
            detectionCard.setVisibility(View.VISIBLE);
            journeyCard.setVisibility(View.VISIBLE);
            speedCard.setVisibility(View.VISIBLE);
            previewCard.setRadius(15);
            frameLayout.setLayoutParams(params);
        }
    }

    // Method used to start PIP mode
    private void startPIP() {
        // Set inPipFragment to true for use in speedometerFragment.java
        inPipFragment = true;

        // Set the parameter for the PIP mode. A view with a 2:1 aspect ratio is used
        PictureInPictureParams pip = new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(1, 2))
                .build();

        // Picture in picture mode is started with the configured parameters
        enterPictureInPictureMode(pip);


    }

    // Method used to request permissions
    @AfterPermissionGranted(123)
    private void requestPermissions() {
        //Array of Strings holds a list of the necessary permissions
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE, Manifest.permission.READ_CONTACTS};

        //If permissions are granted, createCameraSource() is called
        if (EasyPermissions.hasPermissions(this, perms)) {
            createCameraSource();
        }
        //If permissions are not granted the user is displayed a message
        else {
            EasyPermissions.requestPermissions(this, "Permissions need to be accepted in order to use the application", 123, perms);
        }
    }

    // Method used to determine what is done if a permission is denied
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

        // If some permission is denied, a dialog box is shown with the below info
        if (EasyPermissions.somePermissionDenied(this, permissions)) {
            new AlertDialog.Builder(EyeTracking.this)
                    .setTitle("Permissions Needed")
                    .setMessage("Permissions need to be accepted in order to use the application")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                        //The user is returned to the dashboard screen on clicking OK
                        finish();
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    // Unused required method
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    // Method used if some permission is permanently denied
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        /* If some permission is permanently denied,
           the user is brought to their phone settings, where they can manually
           accept the apps permissions*/
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            finish();
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    // Method used to initialise the camera source and Face Detector
    private void createCameraSource() {

        Context context = getApplicationContext();
        /*Create new FaceDetector called detector with parameters chosen
        Refer to https://developers.google.com/android/reference/com/google/android/gms/vision/face/FaceDetector.Builder*/
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setProminentFaceOnly(true)
                .build();


        //The detectors processor is set to GraphicFaceTrackerFactory
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        mCameraSource = new CameraSource.Builder(context, detector)

                .setRequestedPreviewSize(1600, 1200)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();

    }

    // Called when the activity starts interacting with user or after resuming from onPause
    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
        checkForBlinks();
        calcHeadAngle();
        //createCameraSource();

        startCameraSource();
    }

    // Method called when the application enters into a paused state
    @Override
    protected void onPause() {
        super.onPause();

        /*onPause() is called when PIP mode is activated. The app functions should not be altered
        when PIP is active*/
        if (isInPictureInPictureMode()) {

        }
        /*If the app is not in PIP mode, the application shuts down the camera source and stops
         * checking for user blinks. */
        else {
            stopCheckForBlinks();
            stopPlaying();
            isOnPause = true;
            mPreview.stop();
            inPipFragment = false;

        }
    }

    // Method called just before the activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        stopPlaying();
        saveTrip();
        saveTripValue();
        SpeedometerFragment.distance = 0;
    }

    // Method used to store the value of the next trip.
    private void saveTripValue() {
        if (tripValue >= 3) {
            tripValue = 1;
        } else {
            tripValue++;
        }

        // Store the tripValue in TRIP_VALUE_PREF
        SharedPreferences sharedPreferencesSaveTripValue = getSharedPreferences(TRIP_VALUE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesSaveTripValue.edit();
        editor.putInt(TRIP_VALUE_PREF, tripValue);
        editor.commit();
        Log.i(TAG, "(SavedData) TripValue is " + tripValue);

    }

    // Method used to load the previously saved value of tripValue.
    public void loadTripValue() {
        SharedPreferences sharedPreferencesLoadTripValue = getSharedPreferences(TRIP_VALUE_PREF, MODE_PRIVATE);
        tripValue = sharedPreferencesLoadTripValue.getInt(TRIP_VALUE_PREF, 1);
        Log.i(TAG, "(LoadData) TripValue is  " + tripValue);
    }

    /*Method used to save the current trips information such as time, average speed,
    distance travelled and number of detections*/
    private void saveTrip() {

        // Initialise Strings for each of the 4 pieces of data
        String timePref = "";
        String detectPref = "";
        String speedPref = "";
        String distancePref = "";

        /* For a certain tripValue, the 4 data points are stored in the
        respective Shared preferences*/
        if (tripValue == 1 || tripValue == 2 || tripValue == 3) {
            if (tripValue == 1) {
                timePref = TIME_TRIP_ONE_PREF;
                detectPref = DETECT_TRIP_ONE_PREF;
                speedPref = SPEED_TRIP_ONE_PREF;
                distancePref = DISTANCE_TRIP_ONE_PREF;
            }

            if (tripValue == 2) {
                timePref = TIME_TRIP_TWO_PREF;
                detectPref = DETECT_TRIP_TWO_PREF;
                speedPref = SPEED_TRIP_TWO_PREF;
                distancePref = DISTANCE_TRIP_TWO_PREF;
            }


            if (tripValue == 3) {
                timePref = TIME_TRIP_THREE_PREF;
                detectPref = DETECT_TRIP_THREE_PREF;
                speedPref = SPEED_TRIP_THREE_PREF;
                distancePref = DISTANCE_TRIP_THREE_PREF;
            }

        }

        // The overall journey time, elapsedTime is saved in timePref
        SharedPreferences sharedPreferencesTime = getSharedPreferences(timePref, MODE_PRIVATE);
        SharedPreferences.Editor editorTime = sharedPreferencesTime.edit();
        int elapsedTime = (int) (SystemClock.elapsedRealtime() - journeyTimer.getBase());
        editorTime.putInt(timePref, elapsedTime);
        editorTime.apply();

        // The number of detections are stored in detectPref
        SharedPreferences sharedPreferencesDetect = getSharedPreferences(detectPref, MODE_PRIVATE);
        SharedPreferences.Editor editorDetect = sharedPreferencesDetect.edit();
        editorDetect.putInt(detectPref, detections);
        editorDetect.apply();

        // The average journey speed is calculated
        float avgSpeed = (float) (SpeedometerFragment.distance / elapsedTime * 3600 * 1000);
        Log.i(TAG, "avgspeed" + avgSpeed);

        // The average journey speed is saved in speedPref
        SharedPreferences sharedPreferencesSpeed = getSharedPreferences(speedPref, MODE_PRIVATE);
        SharedPreferences.Editor editorSpeed = sharedPreferencesSpeed.edit();
        editorSpeed.putFloat(speedPref, avgSpeed);
        editorSpeed.apply();

        // The journey distance is saved in distancePref
        SharedPreferences sharedPreferencesDistance = getSharedPreferences(distancePref, MODE_PRIVATE);
        SharedPreferences.Editor editorDistance = sharedPreferencesDistance.edit();
        editorDistance.putFloat(distancePref, (float) SpeedometerFragment.distance);
        editorDistance.apply();

    }


    /* Method used to start showing the camera preview.
    Throws exception if unable to start the camera.
    */
    private void startCameraSource() {
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);

            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    // Method used to play the user selected audio, when an alert is triggered.
    public void playMedia() {
        stopPlaying();

        if (alarmTone == 0) {
            mp = MediaPlayer.create(this, R.raw.missle_alert);
        }

        if (alarmTone == 1) {
            mp = MediaPlayer.create(this, R.raw.railroad);
        }
        if (alarmTone == 2) {
            mp = MediaPlayer.create(this, R.raw.police);
        }
        mp.setLooping(true);
        mp.start();
    }

    // Method used to stop playing the user selected audio, normally when the alert is dismissed
    public void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    // Method used to vibrate the phone when an alert is triggered.
    public void vibrate() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {800, 800, 800, 800};
        VibrationEffect vibe = VibrationEffect.createWaveform(pattern, 0);
        vibrator.vibrate(vibe);
    }

    // Method used to stop the phone vibrating
    public void stopVibrating() {
        vibrator.cancel();
    }

    // Method to create alert dialog box for when the user's face is missing.
    public void greenAlertBox() {
        // New dialog box created with the below parameters
        greenAlertDialog = new AlertDialog.Builder(EyeTracking.this)
                .setTitle("Face Missing")
                .setMessage("DriveSafely has detected that your face is missing. " +
                        "Try increasing screen brightness if it is dark.")
                .setIcon(R.drawable.ic_baseline_lens__green_24)
                .show();
        TextView textView = greenAlertDialog.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.xml");
        textView.setTypeface(face);
    }

    /*Method used to create alert dialog box for when the user does not blink
    enough times in a given minute or if their head is beginning to nod. This method will also
    open Google Maps and display a list of nearby petrol stations to the
    user when they dismiss the dialog box
    */
    public void yellowAlertBox() {

        runOnUiThread(() -> {
            // The media player plays a police siren and the phone vibrates
            mp = MediaPlayer.create(this, R.raw.police_siren_2);
            mp.setLooping(true);
            mp.start();
            vibrate();

            // The value of detections is increased and updated
            detections++;
            detectionsTextview.setText(String.valueOf(detections));

            // New alert dialog created with the below parameters
            AlertDialog yellowAlertDialog;
            yellowAlertDialog = new AlertDialog.Builder(EyeTracking.this)
                    .setTitle("Tiredness Detected")
                    .setMessage("DriveSafely has detected that you may be very tired." +
                            " A list of petrol stations will now be displayed. " +
                            "Consider pulling over and drinking a coffee.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            stopPlaying();
                            flag = 0;
                        }
                    }).setIcon(R.drawable.ic_baseline_lens__yellow_24)
                    .show();
            TextView textView = yellowAlertDialog.findViewById(android.R.id.message);
            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.xml");
            textView.setTypeface(face);

            /* On dismissing the dialog, the media player stops,
               the phone stops vibrating and numbOfBlinks is reset to 0*/
            yellowAlertDialog.setOnDismissListener(dialog -> {
                stopPlaying();
                stopVibrating();
                flag = 0;
                yellowAlert = true;
                // numbOfBlinks = 0;

                /*PIP mode is started and the user is presented
                with a list/map of nearby petrol stations
                */
                startPIP();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=Petrol");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            });
        });
    }

    /*Method used to create alert dialog box for when the user's eyes are closed for the user
    selected amount of time. A SMS is sent to the user's emergency contact if they are deemed to be
    asleep
     */
    public void redAlertBox() {
        runOnUiThread(() -> {
            // The selected tone is played and the phone vibrates
            playMedia();
            vibrate();

            // The value of detections is increased and updated
            detections++;
            detectionsTextview.setText(String.valueOf(detections));

            // New alert dialog created with the below parameters
            AlertDialog redAlertDialog;
            redAlertDialog = new AlertDialog.Builder(EyeTracking.this)
                    .setTitle("Eyes Closed Detected")
                    .setMessage("DriveSafely suspects that you have fallen asleep." +
                            " An SMS message has been sent to your emergency contact. " +
                            "It is advisable to pull over and stop.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            stopPlaying();
                            flag = 0;
                        }
                    }).setIcon(R.drawable.ic_baseline_lens_24)
                    .show();
            TextView textView = redAlertDialog.findViewById(android.R.id.message);
            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.xml");
            textView.setTypeface(face);

            /* On dismissing the dialog, the media player stops
               and the phone stops vibrating */
            redAlertDialog.setOnDismissListener(dialog -> {
                stopPlaying();
                stopVibrating();
                flag = 0;
            });
        });
    }


    // Graphic Face Tracker created
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {

        // Variables initialised
        private final GraphicOverlay mOverlay;
        private final FaceGraphic mFaceGraphic;
        private int state_i = 0;
        private long lastLowToHighState = 0;
        private boolean firstTime = true;
        private boolean eyesClosed;
        private int dips;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /* Method used to add the bounding box and calls the eyeTracking method.
           Called when the face position is updated .
        */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);

            if (flag == 0) {
                eyeTracking(face);
            }

        }

        // Called when the face is detected as being missing
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {

            facePresent = false;

            // Bounding box removed from camera preview and numbOfBlinks reset to 0
            mOverlay.remove(mFaceGraphic);
            numbOfBlinks = 0;
            Log.i(TAG, "face_present is" + facePresent);

            // Method faceMissing is called
            faceMissing();
        }

        // Method used to show a dialog box if a user's face has not been detected for 2 seconds
        public void faceMissing() {
            new Handler(Looper.getMainLooper()).post(()
                    -> new CountDownTimer(1000, 1000) {

                // Unused necessary method
                @Override
                public void onTick(long millisUntilFinished) {
                }

                // Called when the CountDownTimer finished.
                @Override
                public void onFinish() {
                    if (!facePresent && greenFirstTime) {
                        greenFirstTime = false;
                        greenAlertBox();
                    } else {
                        // Do nothing
                    }
                }
            }.start());
        }

        // Method used to remove the bounding box from the screen
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }

        // Method used to set variable eyesClose to true
        private void onEyesClosed() {
            eyesClosed = true;
        }

        // Method used to count the number of blinks
        private void onEyesOpened() {

            if (eyesClosed) {
                numbOfBlinks++;

                // Log.i(TAG, "numb of blinks" + numbOfBlinks);
            }
            eyesClosed = false;
        }

        // Method used to send a message to the user's chosen emergency contact
        private void sendSMS() {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emergencyContact, null, getString(R.string.Emergency_message), null, null);
        }

        // Method used to carry out operations based on the user's eyes
        private void eyeTracking(Face face) {

            /*
            Gets the probability of each eye being open.
            Returns a value between 0.0 and 1.0 giving a probability that the face's left eye is open.
             */
            float leftEye = face.getIsLeftEyeOpenProbability();
            float rightEye = face.getIsRightEyeOpenProbability();

            // Removes the greenAlertDialog is condition is false
            if (!greenFirstTime) {
                greenAlertDialog.dismiss();
                greenFirstTime = true;
            }

            /*
            Returns the rotation of the face about the horizontal axis of the image.
            Positive euler x is when the face turns toward the
            top of the of the image that is being processed.
            */
            head_angle = face.getEulerX();
            // Log.i(TAG,"EulerX "+head_angle);
            // Log.i(TAG,"X is  "+x);

            if (head_angle < (updateHeadAngle - 15) && SystemClock.elapsedRealtime() - lastLowToHighState > 500) {
                dips++;
                // Used to prevent the function from continuously firing
                lastLowToHighState = SystemClock.elapsedRealtime();
                Log.i(TAG, "p is  " + dips);
                if (dips == 5) {
                    yellowAlertBox();
                    dips = 0;
                }
            }

            // Sets the threshold value for when the eyes are deemed to be closed
            float threshold = 0.2f;

            //Log.i(TAG,"Left eye"+leftEye);

            // Checks if left or right eyes is considered open
            if (leftEye > threshold && rightEye > threshold) {
                state_i = 0;
                firstTime = true;
                facePresent = true;

                onEyesOpened();

            }

            // If both eyes are deemed closed a number of operations are carried out
            else {

                //onEyesClosed method is called
                onEyesClosed();

                // If state_i is = 0 the elapsedRealTime value is stored in lasLowToHighState
                if (state_i == 0) {
                    lastLowToHighState = SystemClock.elapsedRealtime();

                }

                 /*
                 If firstTime is True and the current elapsedRealTime - lastLowToHighState is
                 greater than the user selected alarm time, the redAlertBox method is called and
                 the application attempts to send a SMS to the chosen emergency contact, if one is
                 selected
                 */
                if (firstTime && SystemClock.elapsedRealtime() - lastLowToHighState > alarmTime) {

                    // Used to prevent the function from continuously firing
                    lastLowToHighState = SystemClock.elapsedRealtime();
                    firstTime = false;
                    redAlertBox();
                    try {
                        sendSMS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flag = 1;
                }
                state_i = 1;

            }


        }

    }

}

