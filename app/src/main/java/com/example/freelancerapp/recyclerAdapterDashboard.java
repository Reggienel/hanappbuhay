package com.example.freelancerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class recyclerAdapterDashboard extends RecyclerView.Adapter<recyclerAdapterDashboard.MyViewHolder> {
    private ArrayList<UserAppointment> userArrayList = new ArrayList<>();
    private final OnNoteListenerdashboard listenerdb;

    public recyclerAdapterDashboard(ArrayList<UserAppointment> userArrayList, OnNoteListenerdashboard listenerdb){
        this.userArrayList = userArrayList;
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
        String name = userArrayList.get(position).getName();
        String service = userArrayList.get(position).getService();
        String time = userArrayList.get(position).getTime();
        String date = userArrayList.get(position).getDate();
        String payment = userArrayList.get(position).getPayment();
        String meetup = userArrayList.get(position).getMeetup();
        String image = userArrayList.get(position).getProfile_image_uri();


        if(image != null){Glide.with(holder.imgClick.getContext()).load(image).into(holder.imgClick);}

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
                    if(payment.equals("Not Paid")){listenerdb.onItemClicked(userArrayList.get(holder.getAdapterPosition()));}
                    else{ Toast.makeText(view.getContext(), "Already Paid",
                                Toast.LENGTH_SHORT).show();}
                }
                else{
                    listenerdb.onItemClicked(userArrayList.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerdb.onItemClickedCancel(userArrayList.get(holder.getAdapterPosition()));
            }
        });
        holder.msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerdb.onItemClickedMessage(userArrayList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameTxt, serviceTxt,timeTxt,dateTxt,paymentTxt,meetTxt;
        private final Button markAsDoneBtn, cancelBtn, msgBtn;
        private final ImageView imgClick;

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
        }
    }
}
