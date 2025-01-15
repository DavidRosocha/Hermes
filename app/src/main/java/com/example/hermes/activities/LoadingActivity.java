package com.example.hermes;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hermes.activities.SignUpActivity;

/**
 * loadingactivity class manages the loading screen animation and transitions to the signup activity.
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     * called when the activity is first created. initializes the ui components and starts the animation.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        EdgeToEdge.enable(this);

        // Get the ImageView that will display the animation
        ImageView impactAnimation = findViewById(R.id.impactAnimation);

        // Set the animation background resource
        impactAnimation.setBackgroundResource(R.drawable.impact_animation);

        // Get the AnimationDrawable object
        AnimationDrawable animationDrawable = (AnimationDrawable) impactAnimation.getBackground();

        // Start the animation
        impactAnimation.post(() -> animationDrawable.start());

        // Apply system bars insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay for 2 seconds, then transition to the SignUpActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoadingActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish(); // Close the LoadingActivity
        }, 2000); // Adjusted delay based on animation length
    }
}