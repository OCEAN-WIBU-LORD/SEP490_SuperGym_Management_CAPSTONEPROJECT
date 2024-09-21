package com.example.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.Appointment;
import com.example.sep490_supergymmanagement.models.Trainer;
import com.example.sep490_supergymmanagement.repositories.AppointmentResp;
import com.example.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.example.sep490_supergymmanagement.views.appointment.AppointmentDetailsActivity;
import com.example.sep490_supergymmanagement.views.appointment.UpdateScheduleActivity;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllAppointmentAdapter extends RecyclerView.Adapter<AllAppointmentAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> appointments;
    private List<Trainer> doctors;

    public AllAppointmentAdapter(Context context, List<Appointment> appointments, List<Trainer> doctors) {
        this.context = context;
        this.appointments = appointments;
        this.doctors = doctors;
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
        Appointment a = appointments.get(position);
        Trainer d = findDoctorById(a.getDoctorId()); // Find the doctor corresponding to the appointment
        holder.setData(a, d);
    }

    public Trainer findDoctorById(String doctorId) {
        for (Trainer doctor : doctors) {
            if (doctor.getTrainerId().equals(doctorId)) {
                return doctor;
            }
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
        private TextView txtDateBooking;
        private Button btnCancel; // Add a reference to the Cancel button
        private Button btnReschedule; // Add a reference to the Reschedule button
        private TextView completedText;
        private Button btnStatus;

        private void bindingView() {
            txtStartTime = itemView.findViewById(R.id.appointmentStartTime);
            txtEndTime = itemView.findViewById(R.id.appointmentEndTime);
            txtDoctorName = itemView.findViewById(R.id.doctorNameAppointment);
            txtDoctorInfo = itemView.findViewById(R.id.doctorInforAppointment);
            txtDateBooking = itemView.findViewById(R.id.dateBooking);
            btnCancel = itemView.findViewById(R.id.btnCancel); // Bind the Cancel button
            btnReschedule = itemView.findViewById(R.id.btnReschedule); // Bind the Reschedule button
            completedText = itemView.findViewById(R.id.completedText);
            btnStatus = itemView.findViewById(R.id.btnStatus);
        }

        private void bindingAction() {
            itemView.setOnClickListener(this::onItemViewClick);
            btnCancel.setOnClickListener(this::onCancelClick); // Set click listener for Cancel button
            btnReschedule.setOnClickListener(this::onRescheduleClick); // Set click listener for Reschedule button
        }

        // Navigate to appointment details activity
        private void onItemViewClick(View view) {
            Appointment appointment = appointments.get(getAdapterPosition());

            Intent intent = new Intent(context, AppointmentDetailsActivity.class);
            intent.putExtra("appointmentId", appointment.getAppointmentId());
            context.startActivity(intent);
        }

        // Handle Cancel button click
        private void onCancelClick(View view) {
            int position = getAdapterPosition();
            Appointment appointment = appointments.get(position);
            String appointmentId = appointment.getAppointmentId();

            // Show confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Appointment")
                    .setMessage("Do you want to cancel this appointment?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Delete the appointment
                            AppointmentResp appointmentResp = new AppointmentResp();
                            appointmentResp.deleteAppointment(appointmentId, new Callback<Appointment>() {
                                @Override
                                public void onCallback(List<Appointment> objects) {
                                    // Remove the appointment from the list and notify the adapter
                                    appointments.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, appointments.size());
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        private void setStatus(Appointment appointment) {
            btnStatus.setText(appointment.getStatus().toUpperCase());
            btnStatus.setEnabled(false);
        }

        // Handle Reschedule button click
        private void onRescheduleClick(View view) {
            int position = getAdapterPosition();
            Appointment appointment = appointments.get(position);
            Trainer doctor = findDoctorById(appointment.getDoctorId());

            // Create intent to navigate to UpdateScheduleActivity
            Intent intent = new Intent(context, UpdateScheduleActivity.class);

            // Pass appointment details
            intent.putExtra("appointmentId", appointment.getAppointmentId());
            intent.putExtra("startTime", appointment.getStartTime().toDate().getTime()); // Pass timestamp as long
            intent.putExtra("endTime", appointment.getEndTime().toDate().getTime()); // Pass timestamp as long
            intent.putExtra("note", appointment.getNote());
            intent.putExtra("status", appointment.getStatus());
            intent.putExtra("diseaseId", appointment.getTrainingLessionId());
            intent.putExtra("doctorId", appointment.getDoctorId());
            intent.putExtra("userId", appointment.getUserId());
            intent.putExtra("recordId", appointment.getRecordId());

            // Pass doctor details
            intent.putExtra("selectedTrainer",doctor);
            intent.putExtra("doctorId", doctor.getTrainerId());
            intent.putExtra("userId", doctor.getUserId());
            intent.putExtra("bio", doctor.getBio());
            intent.putExtra("diseaseIds", doctor.getTrainingLessionIds());
            intent.putExtra("name", doctor.getName());

            context.startActivity(intent);
        }

        public ViewHolder(@NonNull View v) {
            super(v);
            bindingView();
            bindingAction();
        }

        public void setData(Appointment appointment, Trainer doctor) {
            txtStartTime.setText(formatTimestampToTime(appointment.getStartTime()));
            txtEndTime.setText(formatTimestampToTime(appointment.getEndTime()));
            txtDateBooking.setText(formatTimestampToDate(appointment.getStartTime()));
            if(appointment.getStatus().equals("unconfirmed")) {
                btnCancel.setVisibility(View.VISIBLE);
                btnReschedule.setVisibility(View.VISIBLE);
            }else if(appointment.getStatus().equals("canceled")) {
                btnCancel.setVisibility(View.GONE);
                btnReschedule.setVisibility(View.GONE);
                completedText.setText("HAS BEEN CANCLED BY DOCTOR");
                completedText.setVisibility(View.VISIBLE);
            }else if(appointment.getStatus().equals("confirmed")) {
                btnReschedule.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                completedText.setText("CONFIRMED BY DOCTOR");
                completedText.setVisibility(View.VISIBLE);

            }else if(appointment.getStatus().equals("in progress")) {
                btnReschedule.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                completedText.setText("IN PROGRESS");
                completedText.setVisibility(View.VISIBLE);
            }else if(appointment.getStatus().equals("finished")) {
                btnReschedule.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                completedText.setText("COMPLETED");
                completedText.setVisibility(View.VISIBLE);
            }
            if (doctor != null) {
                txtDoctorName.setText(doctor.getName());
                txtDoctorInfo.setText(doctor.getBio());
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
}
