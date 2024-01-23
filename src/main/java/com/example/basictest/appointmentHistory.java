package com.example.basictest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class appointmentHistory extends AppCompatActivity {


    private ListView historyListView;
    private DatabaseHelper dbHelper;
    private Button removeButton;
    private Button removeByDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        dbHelper = new DatabaseHelper(this);

        historyListView = findViewById(R.id.historyListView);
        removeButton = findViewById(R.id.removeButton);
        removeByDateButton = findViewById(R.id.removeByDateButton);

        displayBookingHistory();

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptAndRemovePerson();
            }
        });

        removeByDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptAndRemoveAppointmentsByDate();
            }
        });
    }

    private void promptAndRemovePerson() {
        // Implement a dialog or any other UI element to get user input for the name
        // For simplicity, I'm using a basic AlertDialog for user input

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Name");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nameToRemove = input.getText().toString().trim();
                removePersonFromHistory(nameToRemove);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void promptAndRemoveAppointmentsByDate() {
        // Implement a dialog or any other UI element to get user input for the date
        // For simplicity, I'm using a basic DatePickerDialog for user input

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Remove appointments on the specified date
                removeAppointmentsByDate(dayOfMonth, monthOfYear + 1, year);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void removeAppointmentsByDate(int day, int month, int year) {
        // Remove appointments from the ListView
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) historyListView.getAdapter();
        if (adapter != null) {
            ArrayList<String> itemsToRemove = new ArrayList<>();
            for (int i = 0; i < adapter.getCount(); i++) {
                String item = adapter.getItem(i);
                if (item != null && item.contains("Date: " + day + "-" + month + "-" + year)) {
                    itemsToRemove.add(item);
                }
            }
            for (String itemToRemove : itemsToRemove) {
                adapter.remove(itemToRemove);
            }
            adapter.notifyDataSetChanged();
        }

        // Remove appointments from the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_BOOKINGS,
                DatabaseHelper.COLUMN_DATE + "=?",
                new String[]{String.format(Locale.US, "%02d-%02d-%04d", day, month, year)});
        db.close();

        // Display a Toast message based on the deletion result
        if (rowsDeleted > 0) {
            // Appointments removed successfully
            Toast.makeText(this, "Appointments on " + day + "-" + month + "-" + year + " removed", Toast.LENGTH_SHORT).show();
        } else {
            // No appointments found for the specified date
            Toast.makeText(this, "No appointments found on " + day + "-" + month + "-" + year, Toast.LENGTH_SHORT).show();
        }
    }

    private void removePersonFromHistory(String nameToRemove) {
        // Remove the person from the ListView
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) historyListView.getAdapter();
        if (adapter != null) {
            adapter.remove(nameToRemove);
            adapter.notifyDataSetChanged();
        }

        // Remove the person from the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_BOOKINGS, DatabaseHelper.COLUMN_NAME + "=?", new String[]{nameToRemove});
        db.close();

        // Display a Toast message based on the deletion result
        if (rowsDeleted > 0) {
            // Appointment removed successfully
            Toast.makeText(this, "Appointment for " + nameToRemove + " removed", Toast.LENGTH_SHORT).show();

            // Restart the activity to refresh the whole page
            Intent intent = getIntent();
            finish(); // Finish the current activity
            startActivity(intent); // Start a new instance of the activity
        } else {
            // Person with the specified name not found
            Toast.makeText(this, "No appointment found for " + nameToRemove, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayBookingHistory() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_PHONE,
                DatabaseHelper.COLUMN_CUT_TYPE,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_TIME
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_BOOKINGS,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String> historyList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
            String cutType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUT_TYPE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME));

            historyList.add("Name: " + name + ", Phone: " + phone +
                    ", Cut Type: " + cutType + ", Date: " + date +
                    ", Time: " + time);
        }

        cursor.close();
        db.close();

        // Create an ArrayAdapter to bind the data to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);

        // Set the adapter for the ListView
        historyListView.setAdapter(adapter);

        TextView emptyView = findViewById(R.id.emptyView);

// ...

        if (historyList.isEmpty()) {
            historyListView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            historyListView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}