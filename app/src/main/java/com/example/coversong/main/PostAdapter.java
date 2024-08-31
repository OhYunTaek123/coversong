package com.example.coversong.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coversong.R;

import java.util.ArrayList;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Map<String, Object>> postList;

    public PostAdapter(Context context, ArrayList<Map<String, Object>> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> postData = postList.get(position);
        String title = (String) postData.get("title");
        String id = (String) postData.get("publisher");
        String content = (String) postData.get("contents");

        holder.titleTextView.setText("제목: " + title);
        holder.idTextView.setText("ID: " + id);
        holder.contentTextView.setText("내용: " + content);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView idTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.post_item_view);
            idTextView = itemView.findViewById(R.id.post_name_view);
            contentTextView = itemView.findViewById(R.id.post_text_view);
        }
    }
}