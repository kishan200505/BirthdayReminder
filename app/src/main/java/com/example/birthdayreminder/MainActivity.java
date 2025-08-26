package com.example.birthdayreminder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView birthdayListView;
    private TextView upcomingTextView;
    private SearchView searchView;
    private DatabaseHelper dbHelper;
    private BirthdayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        birthdayListView = findViewById(R.id.birthdayListView);
        upcomingTextView = findViewById(R.id.upcomingTextView);
        searchView = findViewById(R.id.searchView);
        Button addButton = findViewById(R.id.addButton);
        dbHelper = new DatabaseHelper(this);

        // Load birthdays
        loadBirthdays();

        // Set up Add Birthday button
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddBirthdayActivity.class);
            startActivity(intent);
        });

        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // Add click listener for ListView items
        birthdayListView.setOnItemClickListener((parent, view, position, id) -> {
            Birthday birthday = adapter.getItem(position);
            if (birthday != null) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Birthday Details")
                        .setMessage("Name: " + birthday.getName() + "\nDate: " + String.format("%02d/%02d", birthday.getMonth(), birthday.getDay()))
                        .setPositiveButton("OK", null)
                        .setNeutralButton("Edit", (dialog, which) -> {
                            Intent intent = new Intent(MainActivity.this, EditBirthdayActivity.class);
                            intent.putExtra("id", birthday.getId());
                            intent.putExtra("name", birthday.getName());
                            intent.putExtra("month", birthday.getMonth());
                            intent.putExtra("day", birthday.getDay());
                            startActivity(intent);
                        })
                        .setNegativeButton("Delete", (dialog, which) -> {
                            boolean success = dbHelper.deleteBirthday(birthday.getId());
                            if (success) {
                                loadBirthdays(); // Refresh list
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Refreshes the List each time
        loadBirthdays();
    }

    private void loadBirthdays() {
        // Loads upcoming birthdays
        List<Birthday> upcoming = dbHelper.getUpcomingBirthdays(30);
        StringBuilder upcomingText = new StringBuilder("Upcoming Birthdays (Next 30 Days):\n");
        if (upcoming.isEmpty()) {
            upcomingText.append("None");
        } else {
            for (Birthday b : upcoming) {
                upcomingText.append(b.toString()).append("\n");
            }
        }
        upcomingTextView.setText(upcomingText.toString());

        // Loads all birthdays
        List<Birthday> birthdays = dbHelper.getAllBirthdays();
        if (adapter == null) {
            adapter = new BirthdayAdapter(this, birthdays);
            birthdayListView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(birthdays);
            adapter.notifyDataSetChanged();
        }
    }
}