package com.example.coversong.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coversong.R;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private View loginButton, passButton;
    private TextView nickName;
    private ImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.main_kakao_button);
        passButton = findViewById(R.id.main_pass_button);
        profileImage = findViewById(R.id.imageLogo);

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {

                }
                if (throwable != null){

                }
                updateKakaoLogin();
                return null;
            }
        };
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this,callback);
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

                    loginButton.setVisibility(View.GONE);
                    passButton.setVisibility(View.VISIBLE);

                } else {

                    loginButton.setVisibility(View.VISIBLE);
                    passButton.setVisibility(View.GONE);
                }
                return null;
            }
        });
    }
}
