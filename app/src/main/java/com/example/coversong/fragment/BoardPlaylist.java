package com.example.coversong.fragment;

import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.coversong.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 홈 플레이리스트
 *
 */
public class BoardPlaylist extends Fragment {
    private boolean isMusicPlaying = false;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("RecordFiles");
    private MusicAdapter adapter;

    private RecyclerView recyclerView;
    private ArrayList<music> arrayList;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView start_pause_btn;
    private ImageView item_cover_image_view2;
    private TextView track_name;
    private TextView artist_name;
    private TextView play_time_text_view;
    private TextView total_time_text_view;
    private SeekBar play_list_seek_bar;
    private SeekBar player_seek_bar;
    private MediaPlayer mediaPlayer;
    private ImageView play_list_image_view;
    private ImageView skip_prev_image_view;
    private ImageView skip_next_image_view;
    private Group playerViewGroup;
    private Group playListGroup;
    private int currentSongIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_playlist, container, false);

        recyclerView = view.findViewById(R.id.play_list_recycler_view);
        start_pause_btn = view.findViewById(R.id.play_control_image_view);
        track_name = view.findViewById(R.id.track_text_view);
        artist_name = view.findViewById(R.id.artist_text_view);
        play_list_seek_bar = view.findViewById(R.id.play_list_seek_bar_playlist);
        player_seek_bar = view.findViewById(R.id.player_seek_bar);
        item_cover_image_view2 = view.findViewById(R.id.cover_image_view);
        play_list_image_view = view.findViewById(R.id.play_list_image_view);
        playerViewGroup = view.findViewById(R.id.player_view_group);
        playListGroup = view.findViewById(R.id.play_list_group);
        play_time_text_view = view.findViewById(R.id.play_time_text_view);
        total_time_text_view = view.findViewById(R.id.total_time_text_view);
        skip_prev_image_view = view.findViewById(R.id.skip_prev_image_view);
        skip_next_image_view = view.findViewById(R.id.skip_next_image_view);

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
                toggle_view_group();
                playMusic(musicUrl);
                updateUI(clickedMusic);
                currentSongIndex = position;
            }
        });

        play_list_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle_view_group();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (playerViewGroup.getVisibility() == View.VISIBLE) {
                    toggle_view_group();
                } else {
                    // 그냥 뒤로가기 버튼 눌렀을때 나올 코드
                }
            }
        });

        skip_prev_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        skip_next_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        return view;
    }

    private void toggle_view_group(){
        if (playerViewGroup.getVisibility() == View.VISIBLE) {
            playerViewGroup.setVisibility(View.GONE);
            playListGroup.setVisibility(View.VISIBLE);
        } else {
            playerViewGroup.setVisibility(View.VISIBLE);
            playListGroup.setVisibility(View.GONE);
        }
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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong();
                }
            });

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
            updateSeekBar(player_seek_bar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(music selectedMusic) {
        String play_musicName =  selectedMusic.getMusic_name();
        String[] musicDataSplit = play_musicName.split("!@");
        String my_musicName = "";
        String artistName = "";
        if (musicDataSplit.length >= 2) {
            artistName = musicDataSplit[0];
            my_musicName = musicDataSplit[1];
            track_name.setText(my_musicName);
            artist_name.setText(artistName);
        }else {
            track_name.setText(selectedMusic.getMusic_name());
            artist_name.setText("Unknown");
        }

        int currentPosition = mediaPlayer.getCurrentPosition();
        String playTime = millisecondsToTimer(currentPosition);
        play_time_text_view.setText(playTime);

        int totalDuration = mediaPlayer.getDuration();
        String totalTime = millisecondsToTimer(totalDuration);
        total_time_text_view.setText(totalTime);
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            music previousSong = arrayList.get(currentSongIndex);
            String musicUrl = previousSong.getImage();
            playMusic(musicUrl);
            updateUI(previousSong);
        }
    }

    private void playNextSong() {
        if (currentSongIndex < arrayList.size() - 1) {
            currentSongIndex++;
            music nextSong = arrayList.get(currentSongIndex);
            String musicUrl = nextSong.getImage();
            playMusic(musicUrl);
            updateUI(nextSong);
        }
    }

    private String millisecondsToTimer(long milliseconds) {
        String timerString = "";
        String secondsString;

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }

    private void updateSeekBar(SeekBar seekBar) {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int totalDuration = mediaPlayer.getDuration();
            seekBar.setMax(totalDuration);
            seekBar.setProgress(currentPosition);

            String playTime = millisecondsToTimer(currentPosition);
            play_time_text_view.setText(playTime);
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
}