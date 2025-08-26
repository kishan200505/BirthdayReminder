package com.example.birthdayreminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class EditBirthdayActivity extends AppCompatActivity {
    private EditText nameEditText;
    private DatePicker datePicker;
    private DatabaseHelper dbHelper;
    private int birthdayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_birthday);

        nameEditText = findViewById(R.id.nameEditText);
        datePicker = findViewById(R.id.datePicker);
        Button saveButton = findViewById(R.id.saveButton);
        dbHelper = new DatabaseHelper(this);

        // Get birthday data from Intent
        Intent intent = getIntent();
        birthdayId = intent.getIntExtra("id", -1);
        String name = intent.getStringExtra("name");
        int month = intent.getIntExtra("month", 1);
        int day = intent.getIntExtra("day", 1);

        // Validate intent data
        if (birthdayId == -1 || name == null) {
            Toast.makeText(this, "Invalid birthday data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields
        nameEditText.setText(name);
        datePicker.updateDate(2025, month - 1, day);

        saveButton.setOnClickListener(v -> saveBirthday());
    }

    private void saveBirthday() {
        String name = nameEditText.getText().toString().trim();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();

        // Validate inputs
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date
        if (!isValidDate(month, day)) {
            Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update database
        boolean success = dbHelper.updateBirthday(birthdayId, name, month, day);
        if (success) {
            Toast.makeText(this, "Birthday updated", Toast.LENGTH_SHORT).show();
            finish(); // Makes it return to MainActivity
        } else {
            Toast.makeText(this, "Error updating birthday", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidDate(int month, int day) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.getTime();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}