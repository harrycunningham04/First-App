package com.example.basictest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the welcomeMsg TextView
        welcomeMsg = findViewById(R.id.welcomeMsg);

        // Set a welcome message
        welcomeMsg.setText("Welcome to the best barbers in brighton. This is our easy booking app where you only need to enter your name, number, what cut you would like and the time. Then we give you the best haircut possible.");
    }

    public void goToForm(View view) {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    public void goToReview(View view) {
        Intent intent = new Intent(this, LeaveReviewActivity.class);
        startActivity(intent);
    }

    public void goToAppointmentHistory(View view) {
        Intent intent = new Intent(this, appointmentHistory.class);
        startActivity(intent);
    }
}