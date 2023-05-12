package com.example.coversong.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.coversong.R;
import com.example.coversong.main.BoardActivity;
import com.example.coversong.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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

        Switch pushSwitch = (Switch) view.findViewById(R.id.pushSwitch);
        Switch autoSwitch = (Switch) view.findViewById(R.id.autoPlay);

        //Intent intent = new Intent(MainActivity.this, MainActivity.class);




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

        return view;
    }
}