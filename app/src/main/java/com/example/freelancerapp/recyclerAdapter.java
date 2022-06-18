package com.example.freelancerapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    private ArrayList<User> userArrayList = new ArrayList<>();
    private final OnNoteListener listener;

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
        String location = userArrayList.get(position).getLocation();
        String image = userArrayList.get(position).getProfile_image_uri();
        String ratingValue = userArrayList.get(position).getRating();
        String ratingCount = userArrayList.get(position).getUserratingcount();
        String price = userArrayList.get(position).getServiceprice();
        String availability = userArrayList.get(position).getAvailability();

        if(image != null){Glide.with(holder.imgClick.getContext()).load(image).into(holder.imgClick);}
        if(location != null){holder.locationTxt.setText(location);}
        if(ratingValue != null){holder.ratingBar.setRating(Float.parseFloat(ratingValue));}
        if(ratingCount != null){holder.ratingTxt.setText(ratingCount);}
        if(price != null){holder.priceTxt.setText(price);}
        if(availability != null){holder.availabilityTxt.setText(availability);}

        holder.nameTxt.setText(name);

        holder.imgClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(userArrayList.get(holder.getAdapterPosition()));
                Log.d("Services", "onClick: "+userArrayList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameTxt, locationTxt, ratingTxt, priceTxt, availabilityTxt;
        private final ImageView imgClick;
        private final RatingBar ratingBar;

        public MyViewHolder(final View view, OnNoteListener listener){
            super(view);
            nameTxt = view.findViewById(R.id.name_of_provider);
            locationTxt = view.findViewById(R.id.rlocation);
            imgClick = view.findViewById(R.id.dp_clean_1);
            ratingBar = view.findViewById(R.id.ratingBarList1);
            ratingTxt = view.findViewById(R.id.rRatingCount);
            priceTxt = view.findViewById(R.id.rPrice);
            availabilityTxt = view.findViewById(R.id.rAvailable);
        }
    }
}
