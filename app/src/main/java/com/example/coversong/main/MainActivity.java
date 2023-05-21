package com.example.coversong.main;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coversong.R;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "KakaoLogin";
    private View loginButton, passButton;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        loginButton = findViewById(R.id.main_kakao_button);
        passButton = findViewById(R.id.main_pass_button);
        Function2<OAuthToken , Throwable, Unit> callback = (oAuthToken , throwable) -> {
            Log.e(TAG, "CallBack Method");
            if (oAuthToken != null) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                AuthCredential credential = OAuthProvider.getCredential("kakao.com", null, oAuthToken.getAccessToken());

                mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                        }else{
                            Log.w(TAG, "서버 로그인 실패", task.getException());
                        }
                    }
                });
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

            } else {

                loginButton.setVisibility(View.VISIBLE);
                passButton.setVisibility(View.GONE);
            }
            return null;
        });
    }
    /*
    private void firebaseAuthWithKakao(OAuthToken oAuthToken){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = OAuthProvider.getCredential("kakao.com", oAuthToken);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                }else{
                    Log.w(TAG, "서버 로그인 실패", task.getException());
                }
            }
        });
    }*/
}
