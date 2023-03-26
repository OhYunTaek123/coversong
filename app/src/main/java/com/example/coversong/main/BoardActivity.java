package com.example.coversong.main;

import android.os.Bundle;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.coversong.R;
import com.example.coversong.fragment.BoardHome;
import com.example.coversong.fragment.BoardMore;
import com.example.coversong.fragment.BoardPlaylist;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 홈화면 액티비티
 *
 * @ TODO: 2023-03-26 설정 화면 구현
 *
 */
public class BoardActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private BoardHome fragmentHome = new BoardHome();
    private BoardPlaylist fragmentPlaylist = new BoardPlaylist();
    private BoardMore fragmentMore = new BoardMore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_board);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()){
                case R.id.menu_home:
                    transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
                    break;

                case R.id.menu_playlist:
                    transaction.replace(R.id.menu_frame_layout, fragmentPlaylist).commitAllowingStateLoss();
                    break;

                case R.id.menu_more:
                    transaction.replace(R.id.menu_frame_layout, fragmentMore).commitAllowingStateLoss();
                    break;
            }

            return true;
        }
    }

}