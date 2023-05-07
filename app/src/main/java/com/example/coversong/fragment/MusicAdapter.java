package com.example.coversong.fragment;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coversong.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class MusicAdapter extends FirestoreRecyclerAdapter<StorageReference, MusicAdapter.MusicViewHolder> {
    final private MediaPlayer mediaPlayer;

    public MusicAdapter(@NonNull FirestoreRecyclerOptions<StorageReference> options) {
        super(options);
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
    protected void onBindViewHolder(@NonNull MusicViewHolder holder, int position, @NonNull StorageReference musicRef) {
        holder.musicTitleTextView.setText(musicRef.getName());

        holder.playButton.setOnClickListener(view -> {
            FirebaseStorage.getInstance().getApp().getApplicationContext();
            mediaPlayer.reset();
            musicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                try {
                    mediaPlayer.setDataSource(view.getContext(), uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
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