package com.example.coversong.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import oss.main.R;

public class FragFirstintro extends Fragment {
    private View view;

    public static FragFirstintro newInstance(){
        FragFirstintro fragFirstintro = new FragFirstintro();
        return  fragFirstintro;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_firstintro, container, false);

        return view;
    }
}
