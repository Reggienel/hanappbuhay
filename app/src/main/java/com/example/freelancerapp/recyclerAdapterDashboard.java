package com.example.freelancerapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class recyclerAdapterDashboard extends RecyclerView.Adapter<recyclerAdapterDashboard.MyViewHolder> {
    private ArrayList<UserAppointment> userArrayList2 = new ArrayList<>();
    private final OnNoteListenerdashboard listenerdb;

    public recyclerAdapterDashboard(ArrayList<UserAppointment> userArrayList2, OnNoteListenerdashboard listenerdb){
        this.userArrayList2 = userArrayList2;
        this.listenerdb = listenerdb;
    }

    @NonNull
    @Override
    public recyclerAdapterDashboard.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items2, parent, false);
        return new MyViewHolder(itemView, listenerdb);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapterDashboard.MyViewHolder holder, int position) {
        String name = userArrayList2.get(position).getName();
        String service = userArrayList2.get(position).getService();
        String time = userArrayList2.get(position).getTime();
        String date = userArrayList2.get(position).getDate();
        String payment = userArrayList2.get(position).getPayment();
        String meetup = userArrayList2.get(position).getMeetup();
        String image = userArrayList2.get(position).getProfile_image_uri();
        String ratingValue = userArrayList2.get(position).getRating();


        if(image != null){Glide.with(holder.imgClick.getContext()).load(image).into(holder.imgClick);}
        if(ratingValue != null){holder.ratingBar.setRating(Float.parseFloat(ratingValue));}

        holder.nameTxt.setText(name);
        holder.serviceTxt.setText(service);
        holder.dateTxt.setText(date);
        holder.timeTxt.setText(time);
        holder.paymentTxt.setText(payment);
        holder.meetTxt.setText(meetup);


        holder.markAsDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.startsWith("Employee")){
                    if(payment.equals("Not Paid")){listenerdb.onItemClicked(userArrayList2.get(holder.getAdapterPosition()));}
                    else{
                        Toast.makeText(view.getContext(), "Already Paid",
                                Toast.LENGTH_SHORT).show();}
                }
                else{
                    listenerdb.onItemClicked(userArrayList2.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerdb.onItemClickedCancel(userArrayList2.get(holder.getAdapterPosition()));
            }
        });
        holder.msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerdb.onItemClickedMessage(userArrayList2.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList2.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameTxt, serviceTxt,timeTxt,dateTxt,paymentTxt,meetTxt;
        private final Button markAsDoneBtn, cancelBtn, msgBtn;
        private final ImageView imgClick;
        private final RatingBar ratingBar;

        public MyViewHolder(final View view, OnNoteListenerdashboard listener){
            super(view);
            nameTxt = view.findViewById(R.id.name_of_provider_dboard);
            serviceTxt = view.findViewById(R.id.service_dboard);
            dateTxt = view.findViewById(R.id.date_dboard);
            timeTxt = view.findViewById(R.id.time_dboard);
            meetTxt = view.findViewById(R.id.meetup);
            paymentTxt = view.findViewById(R.id.payment);
            markAsDoneBtn = view.findViewById(R.id.btnMarkAsDone);
            cancelBtn = view.findViewById(R.id.btnCancelAppointment);
            msgBtn = view.findViewById(R.id.btnMsg);
            imgClick = view.findViewById(R.id.imageViewDashboard);
            ratingBar = view.findViewById(R.id.ratingBarList2);
        }
    }
}
