package com.bignerdranch.android.blackboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.hdodenhof.circleimageview.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InformationActivity extends AppCompatActivity {

    private Retrofit mRetrofit;
    private API information;
    private EditText mInformationEditText;
    private Retrofit mRetrofit1;
    private API change;
    private Button informationButton;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mInformationEditText = findViewById(R.id.information_nickname);
        informationButton = findViewById(R.id.information_change);
        backButton = findViewById(R.id.information_back);


        sendNetworkRequest();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = getIntent();
                Integer back = intent1.getIntExtra("from",0);
                if(back==0){
                    Intent intent = new Intent(InformationActivity.this, PageActivity.class);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(InformationActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        });

        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangeName changeInformation = new ChangeName(
                        mInformationEditText.getText().toString()
                );

                changeInformation(changeInformation);

                Intent intent1 = getIntent();
                Integer back = intent1.getIntExtra("from",0);
                if(back==0){
                    Intent intent = new Intent(InformationActivity.this, PageActivity.class);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(InformationActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

    }


    public void sendNetworkRequest() {
        //创建OkHttp client
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);//此处有四个级别，body为显示所有

        //判断是开发者模式，则调用OkHttp日志记录拦截器，方便debug
        if(BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(logging);
        }
        //构建retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://119.3.2.168:8080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();

        SharedPreferences p = getSharedPreferences("myPreferences", MODE_PRIVATE);
        String Authorization = p.getString("token","null");

        information = mRetrofit.create(API.class);
        Call<Information> call = information.get(Authorization);

        call.enqueue(new Callback<Information>() {
            @Override
            public void onResponse(Call<Information> call, Response<Information> response) {
                if (response.isSuccessful()) {

                    String nickname = response.body().getData().getNickname();
                    mInformationEditText.setText(nickname);


                } else {
                    Toast.makeText(InformationActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Information> call, Throwable t) {
                Toast.makeText(InformationActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void changeInformation(ChangeName changeName) {

        //创建OkHttp client
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);//此处有四个级别，body为显示所有

        //判断是开发者模式，则调用OkHttp日志记录拦截器，方便debug
        if(BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(logging);
        }
        //构建retrofit
        mRetrofit1 = new Retrofit.Builder()
                .baseUrl("http://119.3.2.168:8080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();

        SharedPreferences p = getSharedPreferences("myPreferences", MODE_PRIVATE);
        String Authorization = p.getString("token","null");

        change = mRetrofit1.create(API.class);
        Call<ChangeName> call = change.put(changeName,Authorization);
        //回调产生结果
        call.enqueue(new Callback<ChangeName>() {
            @Override
            public void onResponse(Call<ChangeName> call, Response<ChangeName> response) {
                if (response.isSuccessful()) {

//                    Intent intent = new Intent(InformationActivity.this, SettingsActivity.class);
//                    startActivity(intent);

                } else {
                    Log.d("RegisterActivity", "error");
                    Toast.makeText(InformationActivity.this, "出错了", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangeName> call, Throwable t) {
                Toast.makeText(InformationActivity.this, "出错了", Toast.LENGTH_SHORT).show();
            }
        });
    }

}