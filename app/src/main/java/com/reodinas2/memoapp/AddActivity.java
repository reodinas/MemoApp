package com.reodinas2.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    EditText editTitle;
    EditText editContent;
    Button btnDate;
    Button btnTime;
    Button btnSave;

    String date;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTitle = findViewById(R.id.editTitle);
        editContent =findViewById(R.id.editContent);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        btnSave = findViewById(R.id.btnSave);

        Calendar calender = Calendar.getInstance();
        int pYear = calender.get(Calendar.YEAR);
        int pMonth = calender.get(Calendar.MONTH);
        int pDay = calender.get(Calendar.DAY_OF_MONTH);
        int pHour = calender.get(Calendar.HOUR_OF_DAY);
        int pMinute = calender.get(Calendar.MINUTE);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Log.i("DATE", year+"/"+month+"/"+day);
                                Log.i("DATE", String.valueOf(datePicker));

                                month += 1;
                                date = year+"-"+month+"-"+day;

                            }
                        }, pYear, pMonth, pDay);

                datePickerDialog.show();



            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog
                        = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        time = i + ":" + i1;
                        Log.i("DATE", time);
                    }
                }, pHour, pMinute, true);

                timePickerDialog.show();


            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();


            }
        });
    }
}