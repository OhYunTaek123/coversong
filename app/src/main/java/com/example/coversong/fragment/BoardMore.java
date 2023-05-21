package com.example.coversong.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.coversong.R;
import com.example.coversong.main.MainActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kakao.sdk.user.UserApiClient;

/**
 * 홈 더보기
 *
 */
public class BoardMore extends Fragment {

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_more, container, false);

        SwitchMaterial pushSwitch = (SwitchMaterial) view.findViewById(R.id.pushSwitch);
        SwitchMaterial autoSwitch = (SwitchMaterial) view.findViewById(R.id.autoPlay);

        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pushSwitch.isChecked()) {

                } else {
                    // 푸시 알림 끄기
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
        return view;
    }
}