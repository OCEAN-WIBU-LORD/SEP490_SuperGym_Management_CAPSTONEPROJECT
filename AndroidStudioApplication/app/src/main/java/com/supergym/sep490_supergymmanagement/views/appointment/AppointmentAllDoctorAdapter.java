package com.supergym.sep490_supergymmanagement.views.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Appointment;
import com.supergym.sep490_supergymmanagement.utils.Utils;

import java.util.List;

public class AppointmentAllDoctorAdapter extends RecyclerView.Adapter<AppointmentAllDoctorAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;

    public AppointmentAllDoctorAdapter(List<Appointment> appointmentList, Context context) {
        this.appointmentList = appointmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_all_doctor, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.dateBooking.setText(Utils.formatTimestampToDate(appointment.getStartTime()));
        holder.appointmentStartTime.setText(Utils.formatTimestampToTime(appointment.getStartTime()));
        holder.appointmentEndTime.setText(Utils.formatTimestampToTime(appointment.getEndTime()));
//        holder.doctorNameAppointment.setText(appointment.getDoctorName());
//        holder.doctorInforAppointment.setText(appointment.getDoctorInfo());
        // Set image for imgAvatar here
        // Set onClickListeners for btnReschedule and btnCancel here
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView dateBooking, appointmentStartTime, appointmentEndTime, doctorNameAppointment, doctorInforAppointment;
        ImageView imgAvatar;
        Button btnReschedule, btnCancel;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            dateBooking = itemView.findViewById(R.id.dateBooking);
            appointmentStartTime = itemView.findViewById(R.id.appointmentStartTime);
            appointmentEndTime = itemView.findViewById(R.id.appointmentEndTime);
            doctorNameAppointment = itemView.findViewById(R.id.doctorNameAppointment);
            doctorInforAppointment = itemView.findViewById(R.id.doctorInforAppointment);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}