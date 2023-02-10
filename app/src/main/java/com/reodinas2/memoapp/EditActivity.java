package com.reodinas2.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.reodinas2.memoapp.api.MemoApi;
import com.reodinas2.memoapp.api.NetworkClient;
import com.reodinas2.memoapp.config.Config;
import com.reodinas2.memoapp.model.Memo;
import com.reodinas2.memoapp.model.MemoList;
import com.reodinas2.memoapp.model.Res;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditActivity extends AppCompatActivity {

    EditText editTitle;
    EditText editContent;
    Button btnDate;
    Button btnTime;
    Button btnSave;
    Memo memo;
    private String date;
    private String time;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        btnSave = findViewById(R.id.btnSave);

        memo = (Memo) getIntent().getSerializableExtra("memo");

        editTitle.setText(memo.getTitle());
        editContent.setText(memo.getContent());
        // "2023-01-16T12:00:00" => {"2023-01-06", "12:00"}
        String[] dateArray = memo.getDatetime().substring(0, 15+1).split("T");
        date = dateArray[0];
        time = dateArray[1];

        btnDate.setText(date);
        btnTime.setText(time);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 오늘 날짜 가져오기
                Calendar current = Calendar.getInstance();

                new DatePickerDialog(
                        EditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                Log.i("MEMO_APP", "년도 : " + i + ", 월 :"+i1+", 일 :"+i2);
                                // i : 년도, i1 : 월(0~11) , i2 : 일

                                int month = i1 + 1;
                                String strMonth;
                                if(month < 10){
                                    strMonth = "0"+month;
                                }else{
                                    strMonth = ""+month;
                                }

                                String strDay;
                                if(i2 < 10){
                                    strDay = "0"+i2;
                                }else{
                                    strDay= ""+i2;
                                }

                                date = i + "-" + strMonth + "-" + strDay;
                                btnDate.setText(date);

                            }
                        },
                        current.get(Calendar.YEAR),
                        current.get(Calendar.MONTH),
                        current.get(Calendar.DAY_OF_MONTH)
                ).show();

            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar current = Calendar.getInstance();

                new TimePickerDialog(
                        EditActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                Log.i("MEMO_APP", "시간 : " + i + ", 분 :"+i1);
                                // i: hour, i1: minutes
                                String strHour;
                                if (i < 10){
                                    strHour = "0" + i;
                                }else {
                                    strHour = "" + i;
                                }

                                String strMin;
                                if (i1 < 10){
                                    strMin = "0" + i1;
                                }else {
                                    strMin = "" + i1;
                                }

                                time = strHour + ":" + strMin;
                                btnTime.setText(time);
                            }
                        },
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        true
                ).show();
            }

        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty() || date == null || time == null ){
                    Toast.makeText(EditActivity.this, "모든 항목은 필수입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String datetime = date + " " + time;

                //네트워크 호출하는 코드

                showProgress("메모 수정 중...");

                Retrofit retrofit = NetworkClient.getRetrofitClient(EditActivity.this);

                MemoApi api = retrofit.create(MemoApi.class);

                SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                String accessToken = "Bearer " + sp.getString(Config.ACCESS_TOKEN, "");

                memo.setTitle(title);
                memo.setContent(content);
                memo.setDatetime(datetime);

                Call<Res> call = api.updateMemo(memo.getId(), accessToken, memo);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        dismissProgress();

                        if (response.isSuccessful()) {
                            finish();
                        }else {
                            Toast.makeText(EditActivity.this, "정상동작하지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        dismissProgress();
                    }
                });

            }
        });

    }

    // 네트워크 로직 처리시에 화면에 보여주는 함수
    void showProgress(String message){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }


    // 로직처리가 끝나면 화면에서 사라지는 함수
    void dismissProgress(){
        dialog.dismiss();
    }
}