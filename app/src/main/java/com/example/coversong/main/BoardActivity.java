package com.example.coversong.main;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.coversong.R;
import com.example.coversong.fragment.BoardHome;
import com.example.coversong.fragment.BoardMic;
import com.example.coversong.fragment.BoardMore;
import com.example.coversong.fragment.BoardPlaylist;
import com.example.coversong.fragment.BoardProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * 홈화면 액티비티
 *
 * @ TODO: 2023-03-26 설정 화면 구현
 *
 */
public class BoardActivity extends AppCompatActivity {

    BoardHome boardHome;
    BoardPlaylist boardPlaylist;
    BoardMore boardMore;
    BoardMic boardMic;
    BoardProfile boardProfile;

    @Override
        protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        boardHome = new BoardHome();
        boardPlaylist = new BoardPlaylist();
        boardMore = new BoardMore();
        boardMic = new BoardMic();
        boardProfile = new BoardProfile();

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, boardHome).commit();

        NavigationBarView navigationBarView = findViewById(R.id.menu_bottom_navigation);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch(item.getItemId()){
                    case R.id.menu_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout,boardHome).commit();
                        return true;
                    case R.id.menu_playlist:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout,boardPlaylist).commit();
                        return true;
                    case R.id.menu_mic:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout,boardMic).commit();
                        return true;
                    case R.id.menu_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout,boardProfile).commit();
                        return true;
                    case R.id.menu_more:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout,boardMore).commit();
                        return true;
                }
                return false;
            }
        });
    
    }
}