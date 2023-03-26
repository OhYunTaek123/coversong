package com.example.coversong.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import oss.main.ElementActivity;
import oss.main.R;

/**
 * 게시판 항목 뷰 어댑터
 *
 * @see BoardItem
 */
public class BoardItemAdapter extends RecyclerView.Adapter<BoardItemAdapter.ViewHolder> {

    private static ArrayList<BoardItem> itemList = new ArrayList<>();
    private final Context context;

    public BoardItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public BoardItemAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(itemList.get(position));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItemList(ArrayList<BoardItem> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView info;
        TextView profile;
        TextView writer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.item_profile);
            info = itemView.findViewById(R.id.item_info);
            writer = itemView.findViewById(R.id.item_writer);

            //항목 클릭 이벤트
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    BoardItem item = itemList.get(pos);
                    Intent intent = new Intent(v.getContext(), ElementActivity.class);
                    intent.putExtra(REF.LIST.name(), item);
                    context.startActivity(intent);
                }
            });
        }

        void onBind(BoardItem item) {
            profile.setText(item.patient);
            info.setText(item.hospital);
            writer.setText(item.writer);
        }
    }
}
