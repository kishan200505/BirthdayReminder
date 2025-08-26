package com.example.birthdayreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "birthdays.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "birthdays";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_DAY = "day";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_MONTH + " INTEGER NOT NULL, " +
                COLUMN_DAY + " INTEGER NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addBirthday(String name, int month, int day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_DAY, day);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateBirthday(int id, String name, int month, int day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_DAY, day);
        int result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean deleteBirthday(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public List<Birthday> getAllBirthdays() {
        List<Birthday> birthdays = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_MONTH + ", " + COLUMN_DAY, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int month = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONTH));
                int day = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY));
                birthdays.add(new Birthday(id, name, month, day));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return birthdays;
    }

    public List<Birthday> getUpcomingBirthdays(int daysAhead) {
        List<Birthday> upcoming = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        int currentDayOfYear = today.get(Calendar.DAY_OF_YEAR);
        int currentYear = today.get(Calendar.YEAR);

        List<Birthday> allBirthdays = getAllBirthdays();
        for (Birthday b : allBirthdays) {
            Calendar bday = Calendar.getInstance();
            bday.set(Calendar.MONTH, b.getMonth() - 1);
            bday.set(Calendar.DAY_OF_MONTH, b.getDay());
            int bdayDayOfYear = bday.get(Calendar.DAY_OF_YEAR);

            int daysUntil = bdayDayOfYear - currentDayOfYear;
            if (daysUntil < 0) {

                daysUntil += 365;
            }
            if (daysUntil <= daysAhead) {
                upcoming.add(b);
            }
        }
        return upcoming;
    }
}