package com.twb.poker.chatbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.poker.R;

import java.util.ArrayList;
import java.util.List;

public class ChatBoxRecyclerAdapter extends RecyclerView.Adapter<ChatBoxRecyclerAdapter.ChatBoxViewHolder> {
    private List<String> items = new ArrayList<>();

    public void add(String item) {
        items.add(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatBoxItemLayout = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.chat_box_item_layout, parent, false);
        return new ChatBoxViewHolder(chatBoxItemLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxViewHolder holder, int position) {
        String item = items.get(position);
        holder.chatBoxItemTextView.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ChatBoxViewHolder extends RecyclerView.ViewHolder {
        final TextView chatBoxItemTextView;

        ChatBoxViewHolder(View view) {
            super(view);
            chatBoxItemTextView = view.findViewById(R.id.chatBoxItemTextView);
        }
    }
}
