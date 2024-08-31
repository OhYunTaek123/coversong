package com.example.coversong.main;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coversong.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class PostMiddleActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView listView;
    ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
    private static final String TAG = "PostMiddleActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = findViewById(R.id.listView);
        Button reg_button = findViewById(R.id.reg_button);
        readPost();

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostMiddleActivity.this, WritePostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void readPost() {
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        arrayList.clear(); // arrayList 초기화
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // arraylist에 추가
                                arrayList.add(document.getData());
                                Log.d(TAG, document.getId() + "=>" + document.getData());
                            }
                            // Adapter 생성 및 recyclerView에 설정
                            PostAdapter adapter = new PostAdapter(PostMiddleActivity.this, arrayList);
                            listView.setAdapter(adapter);
                            listView.setLayoutManager(new LinearLayoutManager(PostMiddleActivity.this));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}