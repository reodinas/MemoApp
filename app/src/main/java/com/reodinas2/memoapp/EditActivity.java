package com.reodinas2.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.reodinas2.memoapp.api.MemoApi;
import com.reodinas2.memoapp.api.NetworkClient;
import com.reodinas2.memoapp.config.Config;
import com.reodinas2.memoapp.model.Memo;
import com.reodinas2.memoapp.model.MemoList;
import com.reodinas2.memoapp.model.Res;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditActivity extends AppCompatActivity {

    EditText editTitle;
    EditText editDatetime;
    EditText editContent;
    Button btnSave;
    Memo memo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editTitle = findViewById(R.id.editTitle);
        editDatetime = findViewById(R.id.editDatetime);
        editContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);

        memo = (Memo) getIntent().getSerializableExtra("memo");

        editTitle.setText(memo.getTitle());
        editDatetime.setText(memo.getDatetime());
        editContent.setText(memo.getContent());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = editTitle.getText().toString().trim();
                String datetime = editDatetime.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                if (title.isEmpty() || datetime.isEmpty() || content.isEmpty()){
                    Toast.makeText(EditActivity.this, "모두 필수항목입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                memo.setTitle(title);
                memo.setDatetime(datetime);
                memo.setContent(content);


                Retrofit retrofit = NetworkClient.getRetrofitClient(EditActivity.this);

                MemoApi api = retrofit.create(MemoApi.class);

                SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                String accessToken = "Bearer " + sp.getString(Config.ACCESS_TOKEN, "");

                Call<Res> call = api.updateMemo(memo.getId(), accessToken, memo);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {

                        if (response.isSuccessful()) {

                            finish();

                        } else {
                            Toast.makeText(EditActivity.this, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {

                    }
                });


            }
        });
    }
}