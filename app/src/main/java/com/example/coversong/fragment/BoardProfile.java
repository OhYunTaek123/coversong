package com.example.coversong.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coversong.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;

/**
 *
 * 홈 화면 탭
 *
 */
public class BoardProfile extends Fragment {
    private RecyclerView myCoverSongRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<music> arrayList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private MusicAdapter adapter;
    private TextView account_post_count;
    private int videoCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_profile, container, false);

        myCoverSongRecyclerView = view.findViewById(R.id.myCover_song);
        account_post_count = view.findViewById(R.id.account_post_count);
        myCoverSongRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        myCoverSongRecyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        int videoCount = 0;

        MusicAdapter adapter = new MusicAdapter(new MediaPlayer(), arrayList, getContext());
        myCoverSongRecyclerView.setAdapter(adapter);

        UserApiClient.getInstance().me((user, throwable) -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = String.valueOf(user.getId());
            DocumentReference docRef = db.collection("user").document(userId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String videoPath = documentSnapshot.getString("video");
                    if (videoPath != null) {
                        account_post_count.setText("1");
                    } else {
                        // videoPath가 null인 경우 처리
                        account_post_count.setText("0");
                    }
                    if (videoPath != null) {
                        StorageReference storageRef = storage.getReference().child(videoPath);
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            music music = new music();
                            music.setMusic_name(storageRef.getName());
                            music.setMusic_maker("Unknown");
                            music.setImage(uri.toString());
                            arrayList.add(music);
                            adapter.notifyDataSetChanged();
                        }).addOnFailureListener(e -> {
                            // 다운로드 URL을 얻는데 실패한 경우
                        });
                    }
                }
            }).addOnFailureListener(e -> {
            });
            return null;
        });

        return view;
    }
}