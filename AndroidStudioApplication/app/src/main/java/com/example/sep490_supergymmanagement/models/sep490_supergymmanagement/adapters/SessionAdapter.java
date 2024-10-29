package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters;
//session adapter cho HomeActivity

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.SessionDetailActivity;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Session;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private List<Session> sessions;
    private Context context;

    public SessionAdapter(Context context) {
        this.context = context;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        if (session != null) {
            holder.sessionName.setText(session.getName());
            holder.sessionMonth.setText(session.getMonth()); // Thay đổi ở đây
            holder.sessionDay.setText(session.getDay()); // Thay đổi ở đây

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra("SESSION_ID", session.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return sessions != null ? sessions.size() : 0;
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView sessionName;
        TextView sessionMonth; // Thêm TextView cho tháng
        TextView sessionDay;   // Thêm TextView cho ngày

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionName = itemView.findViewById(R.id.session_name);
            sessionMonth = itemView.findViewById(R.id.session_month); // Khởi tạo TextView cho tháng
            sessionDay = itemView.findViewById(R.id.session_day);     // Khởi tạo TextView cho ngày
        }
    }
}
