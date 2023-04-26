package com.example.coversong.fragment;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coversong.R;

import java.io.IOException;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    final private List<String> musicList;
    final private MediaPlayer mediaPlayer;

    public MusicAdapter(List<String> musicList) {
        this.musicList = musicList;
        mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        String music = musicList.get(position);
        holder.musicTitleTextView.setText(music);

        holder.playButton.setOnClickListener(view -> {
            try (AssetFileDescriptor afd = holder.itemView.getContext().getAssets().openFd(music)) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView musicTitleTextView;
        Button playButton;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            musicTitleTextView = itemView.findViewById(R.id.item_track_text_view);
            playButton = itemView.findViewById(R.id.play_control_image_view);
        }
    }
}