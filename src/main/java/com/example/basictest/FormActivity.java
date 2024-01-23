package com.example.basictest;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FormActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private Spinner dropdownSpinner;
    private TimePicker timePicker;
    private Button pickdatebutton;
    private EditText displayDateEditText;

    private DatePickerDialog datePickerDialog;

    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        userNameEditText = findViewById(R.id.userNameEditText);
        displayDateEditText = findViewById(R.id.displayDateEditText);
        dropdownSpinner = findViewById(R.id.dropdownSpinner);
        timePicker = findViewById(R.id.timePicker);
        pickdatebutton = findViewById(R.id.pickdateBtn);
        dbHelper = new DatabaseHelper(this);

        // Populate the dropdown spinner with items
        populateDropdownSpinner();

        pickdatebutton.setOnClickListener(v -> {
            // Call a method to show the date picker
            showDatePicker();
        });

        Button submitButton = findViewById(R.id.submitBtn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click to submit the form
                submitForm();
            }
        });
    }

    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void populateDropdownSpinner() {
        // Replace this list with your actual dropdown items
        List<String> dropdownItems = new ArrayList<>();
        dropdownItems.add("Buzz cut");
        dropdownItems.add("Short back and sides");
        dropdownItems.add("Wash and cut");
        dropdownItems.add("Shave");
        dropdownItems.add("Hair colouring");
        dropdownItems.add("Perms");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropdownItems);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        dropdownSpinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        // Get the current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a date picker dialog
        datePickerDialog = new DatePickerDialog(
                FormActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Set the selected date to the displayDateEditText
                        displayDateEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                },
                year, month, day);

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private void submitForm() {
        // Get user inputs
        String userName = userNameEditText.getText().toString();
        String selectedDropdownItem = dropdownSpinner.getSelectedItem().toString();

        // Check if the name is empty
        if (userName.trim().isEmpty()) {
            // Display a message or toast indicating that the name is required
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return; // Stop the submission process
        }

        // Get selected date from the displayDateEditText
        String selectedDate = displayDateEditText.getText().toString();

        // Check if the date is empty
        if (selectedDate.trim().isEmpty()) {
            // Display a message or toast indicating that the date is required
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return; // Stop the submission process
        }

        // Get selected time from TimePicker
        int hour, minute;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        // Get user-entered phone number
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        String phoneNumber = phoneNumberEditText.getText().toString();

        // Check if the phone number is empty or invalid
        if (phoneNumber.trim().isEmpty() || !isValidPhoneNumber(phoneNumber)) {
            // Display a message or toast indicating that the phone number is required or invalid
            Toast.makeText(this, "Please enter a valid phone number starting with '07'", Toast.LENGTH_SHORT).show();
            return; // Stop the submission process
        }

        // Insert data into the database
        insertBooking(userName, phoneNumber, selectedDropdownItem, displayDateEditText.getText().toString(), hour + ":" + minute);


        // Display a toast with the collected information
        String message = "Name: " + userName +
                "\nSelected Item: " + selectedDropdownItem +
                "\nSelected Date: " + displayDateEditText.getText().toString() +
                "\nSelected Time: " + hour + ":" + minute +
                "\nPhone Number: " + phoneNumber;

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void insertBooking(String name, String phone, String cutType, String date, String time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_CUT_TYPE, cutType);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_TIME, time);

        db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);
        db.close();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is an 9-digit number after the '07'
        return phoneNumber.matches("^07\\d{9}$");
    }

}
