package com.reodinas2.memoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.number.CompactNotation;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.reodinas2.memoapp.adapter.MemoAdapter;
import com.reodinas2.memoapp.api.MemoApi;
import com.reodinas2.memoapp.api.NetworkClient;
import com.reodinas2.memoapp.api.UserApi;
import com.reodinas2.memoapp.config.Config;
import com.reodinas2.memoapp.model.Memo;
import com.reodinas2.memoapp.model.MemoList;
import com.reodinas2.memoapp.model.UserRes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    Button btnAdd;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    MemoAdapter adapter;
    ArrayList<Memo> memoArrayList = new ArrayList<>();

    String accessToken;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 억세스토큰이 저장되어 있으면,
        // 로그인한 유저이므로 메인액티비티를 실행하고,

        // 그렇지 않으면,
        // 회원가입 액티비티를 실행하고 메인액티비티는 종료!

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        accessToken = sp.getString(Config.ACCESS_TOKEN, "");

        if (accessToken.isEmpty()){

            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 회원가입/로그인 유저면, 아래 코드를 실행하도록 둔다.
        btnAdd = findViewById(R.id.btnAdd);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // 네트워크로부터 내 메모를 가져온다.
        getNetworkData();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getNetworkData() {

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);

        MemoApi api = retrofit.create(MemoApi.class);

        Call<MemoList> call = api.getMemoList("Bearer "+accessToken, 0, 20);

        call.enqueue(new Callback<MemoList>() {
            @Override
            public void onResponse(Call<MemoList> call, Response<MemoList> response) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    // 정상적으로 데이터를 받았으니, 리사이클러뷰에 표시
                    MemoList memoList = response.body();

                    memoArrayList.addAll(memoList.getItems());

                    adapter = new MemoAdapter(MainActivity.this, memoArrayList);
                    recyclerView.setAdapter(adapter);


                }else{
                    Toast.makeText(MainActivity.this, "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<MemoList> call, Throwable t) {

                progressBar.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menuLogout){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("로그아웃");
            builder.setMessage("로그아웃 하시겠습니까?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    showProgress("로그아웃 중...");

                    Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);

                    UserApi api = retrofit.create(UserApi.class);

                    Call<UserRes> call = api.logout("Bearer "+accessToken);

                    call.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                            dismissProgress();

                            if (response.isSuccessful()) {
                                // SharedPreference에 저장한 토큰을 초기화한다!
                                SharedPreferences sp = getApplication().getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(Config.ACCESS_TOKEN, "");
                                editor.apply();

                                // 기획: 앱 종료인 경우
//                                finish();

                                // 기획: 로그아웃하면, 로그인 화면을 띄우도록
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }else {

                            }
                        }

                        @Override
                        public void onFailure(Call<UserRes> call, Throwable t) {
                            dismissProgress();

                        }
                    });


                }
            });
            builder.setNegativeButton("NO", null);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
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