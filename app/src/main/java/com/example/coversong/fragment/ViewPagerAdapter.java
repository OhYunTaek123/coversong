package com.example.coversong.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    // 프래그먼트 교체를 구현하는 곳
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragFirstintro.newInstance();
            case 1:
                return FragSecondintro.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    // 하단 탭 레이아웃 텍스트를 선언해주는 곳
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "1.게시판";
            case 1:
                return "2. 채팅";
            default:
                return null;
        }
    }
}
