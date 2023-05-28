package com.example.coversong.fragment;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coversong.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.sdk.user.UserApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        String musicName = arrayList.get(position).getMusic_name().replace("RecordFiles/", "");
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.baseline_audiotrack_24)
                .into(holder.item_cover_image_view);
        String[] musicDataSplit = musicName.split("!@");
        String my_musicName = "";
        String artistName = "";
        if (musicDataSplit.length >= 2) {
            artistName = musicDataSplit[0];
            my_musicName = musicDataSplit[1];
            holder.item_track_text_view.setText(my_musicName);
            holder.item_artist_text_view.setText(artistName);
        }else {
            holder.item_track_text_view.setText(musicName);
            holder.item_artist_text_view.setText(arrayList.get(position).getMusic_maker());
        }
        holder.item_cover_image_view.setImageResource(R.drawable.baseline_audiotrack_24);
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
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String filePath = "RecordFiles/" + musicName;
                    CollectionReference userRef = db.collection("user");
                    userRef.whereEqualTo("video", filePath).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            DocumentReference docRef = userRef.document(documentSnapshot.getId());
                                            docRef.update("like", FieldValue.increment(1))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "like update success");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "error updating like");
                                                        }
                                                    });
                                        }
                                    }else{
                                        Log.d(TAG, "Error getting documents(addLike) :", task.getException());
                                    }
                                }
                            });
                    UserApiClient.getInstance().me((user, throwable) -> {
                        String userId = String.valueOf(user.getId());
                        String playlistCollection = userId + "playlist";
                        CollectionReference myRef = db.collection(playlistCollection);
                        Map<String, Object> playlistUpdate = new HashMap<>();
                        playlistUpdate.put("videoUrl", filePath);

                        myRef.add(playlistUpdate).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot successfully written");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                        return null;
                    });

                    holder.myFavorite_btn.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String filePath = "RecordFiles/" + musicName;
                    CollectionReference userRef = db.collection("user");
                    userRef.whereEqualTo("video", filePath).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            DocumentReference docRef = userRef.document(documentSnapshot.getId());
                                            docRef.update("like", FieldValue.increment(-1))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "like update success");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "error updating like");
                                                        }
                                                    });
                                        }
                                    }else{
                                        Log.d(TAG, "Error getting documents(addLike) :", task.getException());
                                    }
                                }
                            });
                    UserApiClient.getInstance().me((user, throwable) -> {
                        String playlistCollection = user.getId() + "playlist";
                        CollectionReference myRef = db.collection(playlistCollection);
                        Query query = myRef.whereEqualTo("videoUrl", filePath);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                        db.collection(playlistCollection).document(documentSnapshot.getId()).delete();
                                    }
                                }else{
                                    Log.d(TAG, "플레이리스트 제거 실패", task.getException());
                                }
                            }
                        });

                        return null;
                    });

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