package com.example.hermes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hermes.R;


public class PostLoginActivity extends AppCompatActivity {


    private TextView intro;
    protected void updateText() {
        Intent extras = getIntent();

        String name = extras.getStringExtra("name");
        String role = extras.getStringExtra("role");
            System.out.println(name + role);
            intro.setText("Hello " + name + ", you have signed in as a " + role);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.postlogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        intro = findViewById(R.id.accountText);
        updateText();

    }

}