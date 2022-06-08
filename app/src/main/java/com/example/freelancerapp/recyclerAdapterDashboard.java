package com.example.freelancerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerAdapterDashboard extends RecyclerView.Adapter<recyclerAdapterDashboard.MyViewHolder> {
    private ArrayList<UserAppointment> userArrayList = new ArrayList<>();
    private OnNoteListenerdashboard listenerdb;

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

        holder.nameTxt.setText(name);
        holder.serviceTxt.setText(service);
        holder.dateTxt.setText(date);
        holder.timeTxt.setText(time);
        holder.paymentTxt.setText(payment);
        holder.markAsDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerdb.onItemClicked(userArrayList.get(holder.getAdapterPosition()));
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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt, serviceTxt,timeTxt,dateTxt,paymentTxt;
        private Button markAsDoneBtn, cancelBtn, msgBtn;

        public MyViewHolder(final View view, OnNoteListenerdashboard listener){
            super(view);
            nameTxt = view.findViewById(R.id.name_of_provider_dboard);
            serviceTxt = view.findViewById(R.id.service_dboard);
            dateTxt = view.findViewById(R.id.date_dboard);
            timeTxt = view.findViewById(R.id.time_dboard);
            paymentTxt = view.findViewById(R.id.payment);
            markAsDoneBtn = view.findViewById(R.id.btnMarkAsDone);
            cancelBtn = view.findViewById(R.id.btnCancelAppointment);
            msgBtn = view.findViewById(R.id.btnMsg);
        }
    }
}
