package com.example.coversong.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coversong.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    final private MediaPlayer mediaPlayer;
    private ArrayList<music> arrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    boolean isFavorite = false;

    public MusicAdapter(MediaPlayer mediaPlayer, ArrayList<music> arrayList, Context context) {
        this.mediaPlayer = mediaPlayer;
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.myFavorite_btn.setBackgroundResource(R.drawable.baseline_favorite_border_24);

        String musicName = arrayList.get(position).getMusic_name();
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImage())
                .into(holder.item_cover_image_view);
        holder.item_track_text_view.setText(musicName);
        holder.item_artist_text_view.setText(arrayList.get(position).getMusic_maker());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int clickedPosition = holder.getAdapterPosition();
                    onItemClickListener.onItemClick(clickedPosition);
                }
            }
        });
        holder.myFavorite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭 시 즐겨찾기 상태 변경
                isFavorite = !isFavorite;
                if (isFavorite) {
                    holder.myFavorite_btn.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                } else {
                    holder.myFavorite_btn.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return (arrayList != null ? arrayList.size() : 0);
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView item_cover_image_view;
        TextView item_track_text_view;
        TextView item_artist_text_view;
        ImageView myFavorite_btn;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            this.item_cover_image_view = itemView.findViewById((R.id.item_cover_image_view));
            this.item_track_text_view = itemView.findViewById(R.id.item_track_text_view);
            this.item_artist_text_view = itemView.findViewById(R.id.item_artist_text_view);
            this.myFavorite_btn = itemView.findViewById(R.id.myFavorite_btn);
        }
    }
}