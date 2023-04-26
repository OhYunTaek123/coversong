package com.example.coversong.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.coversong.R;

/**
 * 홈 더보기
 *
 */
public class BoardMore extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_more, container, false);

        final Switch pushSwitch = view.findViewById(R.id.pushSwitch);
        final Switch autoSwitch = view.findViewById(R.id.autoPlay);

        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pushSwitch.isChecked()) {
                    // 푸시 알림 켜기
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

        return view;
    }
}