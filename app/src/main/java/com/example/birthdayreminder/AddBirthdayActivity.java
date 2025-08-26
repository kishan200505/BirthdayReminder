package com.example.birthdayreminder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AddBirthdayActivity extends AppCompatActivity {
    private EditText nameEditText;
    private DatePicker datePicker;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);

        nameEditText = findViewById(R.id.nameEditText);
        datePicker = findViewById(R.id.datePicker);
        Button saveButton = findViewById(R.id.saveButton);
        dbHelper = new DatabaseHelper(this);

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

        // Save to database
        boolean success = dbHelper.addBirthday(name, month, day);
        if (success) {
            Toast.makeText(this, "Birthday saved", Toast.LENGTH_SHORT).show();
            finish(); //This makes it return to MainActivity
        } else {
            Toast.makeText(this, "Error saving birthday", Toast.LENGTH_SHORT).show();
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