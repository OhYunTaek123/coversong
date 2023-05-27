package com.example.coversong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coversong.R;
import com.example.coversong.main.MainActivity;
import com.example.coversong.main.PostMiddleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.user.UserApiClient;

/**
 * 홈 더보기
 *
 */
public class BoardMore extends Fragment {
    /* Context BoardActivity; */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_more, container, false);

        TextView user_id = view.findViewById(R.id.user_id);
        Switch pushSwitch = (Switch) view.findViewById(R.id.pushSwitch);
        Switch autoSwitch = (Switch) view.findViewById(R.id.autoPlay);
        TextView floatingActionButton = view.findViewById(R.id.floatingActionButton);

        UserApiClient.getInstance().me((user, throwable) -> {
            if(user!=null){
                String userId = String.valueOf(user.getId());
                user_id.setText("ID: " + userId);
            }
            return null;
        });

        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pushSwitch.isChecked()) {
                    // 푸시 알림 켜기
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {

                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        System.out.println("토큰생성 실패");
                                        return;
                                    }

                                    // Get new FCM registration token
                                    String token = task.getResult();

                                    // Log and toast

                                    System.out.println(token);

                                    Toast.makeText(getActivity(), "토큰 : " +
                                            token, Toast.LENGTH_SHORT).show();
                                }
                            });
                    System.out.println("푸쉬 알림키기 성공");


                } else {
                    // 푸시 알림 끄기
                    FirebaseMessaging.getInstance().deleteToken(); // 토큰파괴
                    System.out.println("푸쉬 알림끄기 성공");
                }
            }
        });

        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (autoSwitch.isChecked()){
                    // 백그라운드 뮤직 키기

                }
                else{
                    // 백그라운드 뮤직 끄기
                }
            }
        });

        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().logout(error-> {
                    if(error != null) {
                        Toast.makeText(getActivity(), "로그아웃 실패", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(getActivity(), "로그아웃 성공", Toast.LENGTH_SHORT);
                    }
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return null;
                });
            }
        });

        View.OnClickListener onClickListener = (v) -> {
            switch(1) {
                case R.id.floatingActionButton:
                    Intent intent = new Intent(getActivity(), PostMiddleActivity.class);
                    startActivity(intent);
                    break;

            }
        };
        view.findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostMiddleActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
}