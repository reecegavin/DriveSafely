/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 23/02/2021
 */

package com.example.drivesafely;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    // Create variables for the animation
    private Animation topAnim;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        //Animation
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        // The animationImage is found in the layout using findViewById.
        // This allows the value to be altered programmatically.*/
        image = findViewById(R.id.animationImage);
        image.setAnimation(topAnim);

        //Splash screen will be displayed for 3 seconds
        int SPLASH_SCREEN = 3000;

        //After the Splash Screen, the Dashboard will be displayed after 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, Dashboard.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}