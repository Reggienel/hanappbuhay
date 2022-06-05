package com.example.freelancerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    private ArrayList<User> userArrayList = new ArrayList<>();
    private OnNoteListener listener;

    public recyclerAdapter(ArrayList<User> userArrayList, OnNoteListener listener){
        this.userArrayList = userArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        String name = userArrayList.get(position).getUsername();
        holder.nameTxt.setText(name);
        holder.imgClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(userArrayList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt;
        private ImageView imgClick;

        public MyViewHolder(final View view, OnNoteListener listener){
            super(view);
            nameTxt = view.findViewById(R.id.name_of_provider);
            imgClick = view.findViewById(R.id.dp_clean_1);
        }
    }
}
