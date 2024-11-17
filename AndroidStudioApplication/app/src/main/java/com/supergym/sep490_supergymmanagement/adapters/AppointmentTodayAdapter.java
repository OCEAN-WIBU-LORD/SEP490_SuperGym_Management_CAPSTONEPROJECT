package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Appointment;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentTodayAdapter extends RecyclerView.Adapter<AppointmentTodayAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> appointments;
    private List<Trainer> doctors;

    public AppointmentTodayAdapter(Context context, List<Appointment> appointments, List<Trainer> doctors) {
        this.context = context;
        this.appointments = appointments;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.appointment_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment a = appointments.get(position);
        Trainer d = findDoctorById(a.getDoctorId()); // Tìm bác sĩ tương ứng với cuộc hẹn
        holder.setData(a, d);
    }

    public Trainer findDoctorById(String doctorId) {
        for (Trainer doctor : doctors) {
           /* if (doctor.getTrainerId().equals(doctorId)) {
                return doctor;
            }*/
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void updateAppointments(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtStartTime;
        private TextView txtEndTime;
        private TextView txtDoctorName;
        private TextView txtDoctorInfo;

        private void bindingView() {
            txtStartTime = itemView.findViewById(R.id.startTime);
            txtEndTime = itemView.findViewById(R.id.endTime);
            txtDoctorName = itemView.findViewById(R.id.doctorName);
            txtDoctorInfo = itemView.findViewById(R.id.doctorInfor);
        }

        private void bindingAction() {
        }

        private void onItemViewClick(View view) {
        }

        public ViewHolder(@NonNull View v) {
            super(v);
            bindingView();
            bindingAction();
        }

        public void setData(Appointment appointment, Trainer doctor) {
            txtStartTime.setText(formatTimestampToTime(appointment.getStartTime()));
            txtEndTime.setText(formatTimestampToTime(appointment.getEndTime()));
            if (doctor != null) {
                txtDoctorName.setText(doctor.getName());
              /*  txtDoctorInfo.setText(doctor.getBio());*/
            } else {
                txtDoctorName.setText("Unknown Doctor");
                txtDoctorInfo.setText("");
            }
        }

        private String formatTimestampToTime(Timestamp timestamp) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
    }
}
