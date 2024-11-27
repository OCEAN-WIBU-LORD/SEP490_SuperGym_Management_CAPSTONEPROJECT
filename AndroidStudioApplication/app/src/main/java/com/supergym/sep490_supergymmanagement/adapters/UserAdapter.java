package com.supergym.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.SearchUser;
import com.supergym.sep490_supergymmanagement.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<SearchUser> userList;
    private OnUserClickListener onUserClickListener;

    public interface OnUserClickListener {
        void onUserClick(SearchUser user);
    }

    public UserAdapter(List<SearchUser> userList, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        SearchUser user = userList.get(position);
        holder.emailTextView.setText(user.getEmail());
        holder.nameTextView.setText(user.getName());
        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user));
    }
    public void clear() {
        userList.clear(); // Xóa tất cả các phần tử trong danh sách
        notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView, nameTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.userEmail);
            nameTextView = itemView.findViewById(R.id.userName);
        }
    }
}

