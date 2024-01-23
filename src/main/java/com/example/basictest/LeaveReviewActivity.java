package com.example.basictest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import androidx.appcompat.app.AppCompatActivity;

public class LeaveReviewActivity extends AppCompatActivity {


    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button submitReviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        // Find views by their IDs
        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        // Disable the submit button by default
        submitReviewButton.setEnabled(false);

        // Set a click listener for the submit button
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected rating and entered review text
                float rating = ratingBar.getRating();
                String reviewText = reviewEditText.getText().toString();

                // Display a toast with the collected information
                String message = "Rating: " + rating + "\nReview: " + reviewText;
                Toast.makeText(LeaveReviewActivity.this, message, Toast.LENGTH_LONG).show();

                // Optionally, you can save the review data or perform other actions here
            }
        });

        // Add a TextWatcher to the reviewEditText to enable/disable the submit button based on text input
        reviewEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Enable the submit button if there is some text in the reviewEditText
                submitReviewButton.setEnabled(charSequence.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed in this case
            }
        });    }

    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}