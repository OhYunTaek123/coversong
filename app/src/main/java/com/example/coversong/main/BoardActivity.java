package com.example.coversong.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import main.WriteActivity;
import oss.data.BoardItem;
import oss.data.REF;
import oss.fragment.ChatFragment;
import oss.fragment.HomeFragment;
import oss.fragment.NearFragment;
import oss.fragment.SetFragment;

/**
 * 게시판 액티비티
 *
 * @ TODO: 2021-07-26 설정 화면 구현
 * @see HomeFragment ,NearFagment,ChatFragment
 */
public class BoardActivity extends AppCompatActivity {
    public HomeFragment homeFragment;
    private NearFragment nearFragment;
    private ChatFragment chatFragment;
    private SetFragment setFragment;
    private FloatingActionButton writeButton;

    private DatabaseReference myRef;
    private ActivityResultLauncher<Intent> launcher;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //액션바 보이기
        setTheme(R.style.Theme_NeedBlood);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.board);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        myRef = FirebaseDatabase.getInstance().getReference(REF.LIST.name());

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        BoardItem boardItem = data.getParcelableExtra(REF.LIST.toString());
                        Log.d("launch", "sethospital:"+boardItem.hospital);
                        boardItem.key = myRef.push().getKey();
                        myRef.child(boardItem.key).setValue(boardItem);
                        Log.d("launch", "sethospital:"+boardItem.hospital);
                        HomeFragment.update();
                    }
                });

        //계정정보
        Intent userIntent = getIntent();
        user = userIntent.getParcelableExtra(REF.USER.name());
        if(!user.isAnonymous())
            Toast.makeText(this, user.getDisplayName() + getString(R.string.signin_complete), Toast.LENGTH_SHORT).show();

        /*프래그먼트*/
        homeFragment = new HomeFragment();
        nearFragment = new NearFragment();
        chatFragment = new ChatFragment();
        setFragment = new SetFragment();

        /*게시판 글쓰기 버튼*/
        writeButton = findViewById(R.id.home_add_button);
        writeButton.setOnClickListener(v -> {
            if (user.isAnonymous()) {
                Toast.makeText(getApplicationContext(), "로그인 하세욤", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, WriteActivity.class);
            intent.putExtra(REF.LIST.name(), new BoardItem(user));
            launcher.launch(intent);
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    writeButton.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle(R.string.board);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                case R.id.nearMap:
                    writeButton.setVisibility(View.INVISIBLE);
                    getSupportActionBar().setTitle(R.string.map);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, nearFragment).commit();
                    return true;
                case R.id.chatting:
                    writeButton.setVisibility(View.INVISIBLE);
                    getSupportActionBar().setTitle(R.string.chat);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
                    return true;
                case R.id.setting:
                    writeButton.setVisibility(View.INVISIBLE);
                    getSupportActionBar().setTitle("설정");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, setFragment).commit();
                    return true;
            }
            return false;
        });
    }

}