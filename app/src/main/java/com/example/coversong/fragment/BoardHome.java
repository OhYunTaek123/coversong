package com.example.coversong.fragment;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.coversong.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.exoplayer2.util.Log;
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
    private boolean isMusicPlaying = false;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("RecordFiles");
    private MusicAdapter adapter;

    private RecyclerView recyclerView;
    private ArrayList<music> arrayList;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView start_pause_btn;
    private  ImageView item_cover_image_view2;
    private TextView track_name;
    private TextView artist_name;
    private SeekBar play_list_seek_bar;
    private MediaPlayer mediaPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_home, container, false);

        recyclerView = view.findViewById(R.id.Newest_list);
        start_pause_btn = view.findViewById(R.id.start_pause_btn);
        track_name = view.findViewById(R.id.track_name);
        artist_name = view.findViewById(R.id.artist_name);
        play_list_seek_bar = view.findViewById(R.id.play_list_seek_bar);
        item_cover_image_view2 = view.findViewById(R.id.item_cover_image_view2);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        adapter = new MusicAdapter(new MediaPlayer(), arrayList, getContext());
        recyclerView.setAdapter(adapter);

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                arrayList.clear();
                for (StorageReference item : listResult.getItems()) {
                    music music = new music();
                    music.setMusic_name(item.getName());
                    music.setMusic_maker("Unknown");
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            music.setImage(uri.toString());
                            arrayList.add(music);
                            adapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 다운로드 URL을 얻는데 실패한 경우
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 오류
            }
        });

        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                music clickedMusic = arrayList.get(position);
                String musicUrl = clickedMusic.getImage();
                isMusicPlaying = true;
                start_pause_btn.setImageResource(R.drawable.ic_baseline_pause_48);
                item_cover_image_view2.setImageResource(R.drawable.baseline_audiotrack_24);
                playMusic(musicUrl);
                updateUI(clickedMusic);
            }
        });

        return view;
    }

    private void playMusic(String musicUrl) {
        try {
            if (mediaPlayer != null) {
                if (isMusicPlaying) {
                    mediaPlayer.pause();
                    isMusicPlaying = false;
                    start_pause_btn.setImageResource(R.drawable.ic_baseline_pause_48);
                }
                mediaPlayer.reset();
            } else {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                play_list_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.pause();
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.start();
                    }
                });
            }

            mediaPlayer.setDataSource(musicUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
            start_pause_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isMusicPlaying) {
                        mediaPlayer.pause();
                        isMusicPlaying = false;
                        start_pause_btn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    } else {
                        mediaPlayer.start();
                        isMusicPlaying = true;
                        start_pause_btn.setImageResource(R.drawable.ic_baseline_pause_48);
                    }
                }
            });
            isMusicPlaying = true;
            updateSeekBar(play_list_seek_bar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(music selectedMusic) {
        track_name.setText(selectedMusic.getMusic_name());
        artist_name.setText("Unknown");
    }

    private void updateSeekBar(SeekBar seekBar) {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int totalDuration = mediaPlayer.getDuration();
            seekBar.setMax(totalDuration);
            seekBar.setProgress(currentPosition);
        }

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar(seekBar);
            }
        };
        handler.postDelayed(runnable, 1000); // 1초마다 업데이트

        // SeekBar의 변경 이벤트 처리
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }
}