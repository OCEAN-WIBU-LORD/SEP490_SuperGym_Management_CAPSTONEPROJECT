package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Appointment;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.User;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories.AppointmentResp;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.views.appointment.AppointmentDetailsActivity;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder> {
    private Context context;
    private List<Appointment> appointments;
    private List<User> users;

    public DoctorAppointmentAdapter(Context context, List<Appointment> appointments, List<User> users) {
        this.context = context;
        this.appointments = appointments;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.appointment_all, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment p = appointments.get(position);
        User d = findUserById(p.getUserId());
        holder.setData(p, d);
    }

    private User findUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void updateData(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtStartTime;
        private TextView txtEndTime;
        private TextView txtDoctorName;
        private TextView txtDoctorInfo;
        private TextView txtDateBooking;
        private Button btnReschedule;
        private Button btnCancel, btnStatus;

        private void bindingView() {
            txtStartTime = itemView.findViewById(R.id.appointmentStartTime);
            txtEndTime = itemView.findViewById(R.id.appointmentEndTime);
            txtDoctorName = itemView.findViewById(R.id.doctorNameAppointment);
            txtDoctorInfo = itemView.findViewById(R.id.doctorInforAppointment);
            txtDateBooking = itemView.findViewById(R.id.dateBooking);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnStatus = itemView.findViewById(R.id.btnStatus);
        }

        private void bindingAction() {
            btnReschedule.setOnClickListener(this::onbtnRescheduleClick);
            btnCancel.setOnClickListener(this::onbtnCancelClick);
        }

        private void onbtnCancelClick(View view) {
            int position = getAdapterPosition();
            Appointment appointment = appointments.get(position);
            if (appointment.getStatus().equals("unconfirmed")) {
                appointment.setStatus("canceled");
            }
            updateAppointment(appointment);

        }

        private void onbtnRescheduleClick(View view) {
            int position = getAdapterPosition();
            Appointment appointment = appointments.get(position);
            if (appointment.getStatus().equals("unconfirmed")) {
                appointment.setStatus("confirmed");
                updateAppointment(appointment);

            } else if (appointment.getStatus().equals("confirmed")) {
                appointment.setStatus("in progress");
                updateAppointment(appointment);
                Intent intent = new Intent(context, AppointmentDetailsActivity.class);
                intent.putExtra("appointmentId", appointment.getAppointmentId());
                context.startActivity(intent);
            } else if (appointment.getStatus().equals("in progress")) {
                appointment.setStatus("in progress");
                updateAppointment(appointment);
                Intent intent = new Intent(context, AppointmentDetailsActivity.class);
                intent.putExtra("appointmentId", appointment.getAppointmentId());
                context.startActivity(intent);
            }


        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bindingView();
            bindingAction();

        }

        private void setStatus(Appointment appointment) {
            btnStatus.setText(appointment.getStatus().toUpperCase());
            btnStatus.setEnabled(false);
        }

        public void setData(Appointment p) {
        }

        public void setData(Appointment appointment, User user) {
            txtStartTime.setText(formatTimestampToTime(appointment.getStartTime()));
            txtEndTime.setText(formatTimestampToTime(appointment.getEndTime()));
            txtDateBooking.setText(formatTimestampToDate(appointment.getStartTime()));

            if (appointment.getStatus().equals("unconfirmed")) {
                btnReschedule.setText("Accept");
                btnCancel.setText("Decline");
            } else if (appointment.getStatus().equals("canceled")) {
                btnReschedule.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            } else if (appointment.getStatus().equals("confirmed")) {
                btnReschedule.setText("Start appointment");
                btnReschedule.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
            } else if (appointment.getStatus().equals("in progress")) {
                btnReschedule.setText("Continue appointment");
                btnReschedule.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
            } else if (appointment.getStatus().equals("finished")) {
                btnReschedule.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                txtDoctorInfo.setText("COMPLETED");
            }
            if (user != null) {
                txtDoctorName.setText(user.getName());
                txtDoctorInfo.setText(user.getPhone());
            } else {
                txtDoctorName.setText("Unknown Doctor");
                txtDoctorInfo.setText("");
            }
            setStatus(appointment);
        }

        private String formatTimestampToTime(Timestamp timestamp) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(date);
        }


        private String formatTimestampToDate(Timestamp timestamp) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM yyyy", Locale.getDefault());
            return sdf.format(date);
        }

    }

    private void updateAppointment(Appointment appointment) {
        AppointmentResp appointmentResp = new AppointmentResp();
        appointmentResp.updateAppointment(appointment, new Callback<Appointment>() {
            @Override
            public void onCallback(List<Appointment> objects) {
                if (!objects.isEmpty()) {
                    Appointment updated = objects.get(0);
                    for (int i = 0; i < appointments.size(); i++) {
                        if (appointments.get(i).getAppointmentId().equals(updated.getAppointmentId())) {
                            appointments.set(i, updated);
                            notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }
        });
    }


}
