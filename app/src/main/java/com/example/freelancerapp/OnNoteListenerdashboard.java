package com.example.freelancerapp;

public interface OnNoteListenerdashboard {
    void onItemClicked(UserAppointment userAppointment);

    void onItemClickedCancel(UserAppointment userAppointment);

    void onItemClickedMessage(UserAppointment userAppointment);

    void onItemClickedConfirm(UserAppointment userAppointment);


}
