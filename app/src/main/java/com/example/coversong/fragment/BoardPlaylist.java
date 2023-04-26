package com.example.coversong.fragment;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.coversong.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 홈 플레이리스트
 *
 */
public class BoardPlaylist extends Fragment {
    MediaPlayer mediaPlayer;
    RecyclerView recyclerView;
    SeekBar playerSeekBar;
    TextView playTimeTextView, totalTimeTextView;
    ImageButton playControlButton, skipNextButton, skipPrevButton;
    Button favoriteButton;
    int currentMusicPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_playlist, container, false);

        List<String> musicList = new ArrayList<>();
        // 노래 파일 리스트를 추가
        musicList.add("song1.mp3");
        musicList.add("song2.mp3");
        musicList.add("song3.mp3");

        recyclerView = view.findViewById(R.id.play_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MusicAdapter musicAdapter = new MusicAdapter(musicList);
        recyclerView.setAdapter(musicAdapter);

        playerSeekBar = view.findViewById(R.id.player_seek_bar);
        playTimeTextView = view.findViewById(R.id.play_time_text_view);
        totalTimeTextView = view.findViewById(R.id.total_time_text_view);
        playControlButton = view.findViewById(R.id.play_control_image_view);
        skipNextButton = view.findViewById(R.id.skip_next_image_view);
        skipPrevButton = view.findViewById(R.id.skip_prev_image_view);
        favoriteButton = view.findViewById(R.id.play_list_favorite);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            // 현재 곡이 끝나면 다음 곡으로 이동
            currentMusicPosition++;
            if (currentMusicPosition >= musicList.size()) {
                currentMusicPosition = 0;
            }
            playMusic(musicList.get(currentMusicPosition));
        });

        playControlButton.setOnClickListener(view1 -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            } else {
                mediaPlayer.start();
                playControlButton.setImageResource(R.drawable.ic_baseline_pause_48);
            }
        });

        skipNextButton.setOnClickListener(view1 -> {
            currentMusicPosition++;
            if (currentMusicPosition >= musicList.size()) {
                currentMusicPosition = 0;
            }
            playMusic(musicList.get(currentMusicPosition));
        });

        skipPrevButton.setOnClickListener(view1 -> {
            currentMusicPosition--;
            if (currentMusicPosition < 0) {
                currentMusicPosition = musicList.size() - 1;
            }
            playMusic(musicList.get(currentMusicPosition));
        });

        favoriteButton.setOnClickListener(view1 -> {
            // 좋아요 버튼 클릭 시 동작 추가
        });

        return view;
    }
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void playMusic(String musicFileName) {
        try {
            AssetFileDescriptor afd = requireContext().getAssets().openFd(musicFileName);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playControlButton.setImageResource(R.drawable.ic_baseline_pause_48);

            int totalDuration = mediaPlayer.getDuration();
            totalTimeTextView.setText(formatTime(totalDuration));
            playerSeekBar.setMax(totalDuration);

            new Thread(() -> {
                while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentDuration = mediaPlayer.getCurrentPosition();
                    playerSeekBar.setProgress(currentDuration);
                    playTimeTextView.setText(formatTime(currentDuration));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}