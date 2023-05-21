package com.example.coversong.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.coversong.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.kakao.sdk.link.LinkApi;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * 홈 화면 탭
 *
 */
public class BoardHome extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("RecordFiles");
    private MusicAdapter adapter;

    private  RecyclerView recyclerView;
    private ArrayList<music> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_home, container, false);

        recyclerView = view.findViewById(R.id.Newest_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();



        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                arrayList.clear();
                for (StorageReference item : listResult.getItems()) {
                    music music = new music();
                    music.setImage(item.toString());
                    music.setMusic_name(item.getName());
                    music.setMusic_maker("Unknown");
                    arrayList.add(music);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 오류
            }
        });
        /* recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), new OnItemClickListener() {

        })); */

        adapter = new MusicAdapter(new MediaPlayer(), arrayList, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

}