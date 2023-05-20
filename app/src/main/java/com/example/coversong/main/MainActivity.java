package com.example.coversong.main;




import android.content.Intent;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coversong.R;
import com.google.android.exoplayer2.util.Log;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;



public class MainActivity extends AppCompatActivity {


    private static final String TAG = "KakaoLogin";
    private View loginButton, passButton;

    private static final String TAG = "MainActivity";

    private View loginButton;
    private TextView nickName;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        loginButton = findViewById(R.id.main_kakao_button);
        passButton = findViewById(R.id.main_pass_button);
        Function2<OAuthToken, Throwable, Unit> callback = (oAuthToken, throwable) -> {
            Log.e(TAG, "CallBack Method");
            if (oAuthToken != null) {
        loginButton = findViewById(R.id.main_kakao_button);
        nickName = findViewById(R.id.nickname);

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if(oAuthToken != null){
                    //TBD
                }
                if (throwable != null) {
                    //TBD
                }
                updateKakaoLogin();
            }else{
                Log.e(TAG, "invoke: login fail");
            }
            return null;
        };

        loginButton.setOnClickListener(v -> {
            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)){
                UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
            } else {
                UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this,callback);
            }
        });


        passButton.setOnClickListener(v -> moveBoardActivity());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                }

            }
        });

        updateKakaoLogin();
    }



    private void moveBoardActivity(){
        Intent intent = new Intent(MainActivity.this, BoardActivity.class);
        startActivity(intent);
    }

    private void updateKakaoLogin(){
        UserApiClient.getInstance().me((user, throwable) -> {
            if (user != null){


                Intent intent = new Intent(MainActivity.this, BoardActivity.class);
                startActivity(intent);

                    Log.i(TAG, "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());

                    nickName.setText(user.getKakaoAccount().getProfile().getNickname()); //닉네임 불러오기


                    loginButton.setVisibility(View.GONE);


            } else {


                loginButton.setVisibility(View.VISIBLE);
                passButton.setVisibility(View.GONE);

                    nickName.setText(null);

                    loginButton.setVisibility(View.VISIBLE);

                }
                return null;
            }
            return null;
        });
    }
}
