package com.example.coversong.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coversong.R;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private View loginButton;
    private TextView nickName;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                return null;
            }
        };

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

    private void updateKakaoLogin(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null){

                    Log.i(TAG, "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());

                    nickName.setText(user.getKakaoAccount().getProfile().getNickname()); //닉네임 불러오기


                    loginButton.setVisibility(View.GONE);


                } else {

                    nickName.setText(null);

                    loginButton.setVisibility(View.VISIBLE);

                }
                return null;
            }
        });
    }
}
