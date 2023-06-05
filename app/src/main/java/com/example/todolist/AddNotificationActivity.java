package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class AddNotificationActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    Button btnSetAlarm, btnTimePicker, btnDatePicker, btnCancelAlarm;
    TextView tvDate;
    NotificationHelper mNotificationHelper;
    Calendar c;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnSetAlarm = findViewById(R.id.btn_set_notification);
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm);
        btnTimePicker = findViewById(R.id.btn_open_time_picker);
        btnDatePicker = findViewById(R.id.btn_date_picker);
        tvDate = findViewById(R.id.tv_date);

        btnSetAlarm.setOnClickListener(this);
        btnCancelAlarm.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);

        c = Calendar.getInstance();

        mNotificationHelper = new NotificationHelper(this);

        checkIfAlarmIsSet();

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("isAlarmSet")) {
                    boolean isAlarmSet = sharedPreferences.getBoolean("isAlarmSet", false);

                    if (isAlarmSet) {
                        btnDatePicker.setEnabled(false);
                        btnDatePicker.setBackgroundResource(R.drawable.round_button_off);
                        btnCancelAlarm.setEnabled(true);
                        btnCancelAlarm.setBackgroundResource(R.drawable.round_button);
                    } else {
                        btnDatePicker.setEnabled(true);
                        btnDatePicker.setBackgroundResource(R.drawable.round_button);
                        btnCancelAlarm.setEnabled(false);
                        btnCancelAlarm.setBackgroundResource(R.drawable.round_button_off);
                        tvDate.setText("Date: ");
                        // save the text in Shared preference so when open the app again the text will show what is need to be shown
                        editor.putString("tvDate", tvDate.getText().toString());
                        editor.apply();
                    }
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        if (btnCancelAlarm.isEnabled())
            tvDate.setText(sharedPreferences.getString("tvDate", "Date: "));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  // This will navigate back to the previous activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v == btnCancelAlarm) {
            cancelAlarm();

            btnDatePicker.setEnabled(true);
            btnDatePicker.setBackgroundResource(R.drawable.round_button);
            btnCancelAlarm.setEnabled(false);
            btnCancelAlarm.setBackgroundResource(R.drawable.round_button_off);

            tvDate.setText("Date: ");
            // save the text in Shared preference so when open the app again the text will show what is need to be shown
            editor.putString("tvDate", tvDate.getText().toString());
            editor.apply();

            Toast.makeText(AddNotificationActivity.this, "Alarm canceled", Toast.LENGTH_SHORT).show();
            // update the shared preferences to indicate that there is no alarm
            editor.putBoolean("isAlarmSet", false);
            editor.apply();
        } else if (v == btnSetAlarm) {
            startAlarm(c);
            btnSetAlarm.setEnabled(false);
            btnSetAlarm.setBackgroundResource(R.drawable.round_button_off);
            btnCancelAlarm.setEnabled(true);
            btnCancelAlarm.setBackgroundResource(R.drawable.round_button);

            Toast.makeText(AddNotificationActivity.this, "Alarm is set", Toast.LENGTH_SHORT).show();
            // update the shared preferences to indicate the there is alarm
            editor.putBoolean("isAlarmSet", true);
            editor.apply();
        } else if (v == btnTimePicker) {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        } else if (v == btnDatePicker) {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        tvDate.setText(tvDate.getText() + DateFormat.getInstance().format(c.getTime()));
        // save the text in Shared preference so when open the app again the text will show what is need to be shown
        editor.putString("tvDate", tvDate.getText().toString());
        editor.apply();

        btnSetAlarm.setEnabled(true);
        btnSetAlarm.setBackgroundResource(R.drawable.round_button);
        btnTimePicker.setEnabled(false);
        btnTimePicker.setBackgroundResource(R.drawable.round_button_off);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        btnTimePicker.setEnabled(true);
        btnTimePicker.setBackgroundResource(R.drawable.round_button);
        btnDatePicker.setEnabled(false);
        btnDatePicker.setBackgroundResource(R.drawable.round_button_off);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void checkIfAlarmIsSet() { // check if alarm is set and adjust the buttons accordingly
        boolean isAlarmSet = sharedPreferences.getBoolean("isAlarmSet", false);

        if (isAlarmSet) {
            btnDatePicker.setEnabled(false);
            btnDatePicker.setBackgroundResource(R.drawable.round_button_off);
            btnCancelAlarm.setEnabled(true);
            btnCancelAlarm.setBackgroundResource(R.drawable.round_button);
        } else {
            btnDatePicker.setEnabled(true);
            btnDatePicker.setBackgroundResource(R.drawable.round_button);
            btnCancelAlarm.setEnabled(false);
            btnCancelAlarm.setBackgroundResource(R.drawable.round_button_off);

        }
    }
}