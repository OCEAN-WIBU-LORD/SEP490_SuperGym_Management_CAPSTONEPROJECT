package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.VH> {

    private Context context;
    private List<String> times;
    private List<String> disabledTimes; // List of disabled times
    private int selectedItem = RecyclerView.NO_POSITION; // Selected item position, initially no item selected

    public TimeAdapter(Context context, List<String> times) {
        this.context = context;
        this.times = times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
        notifyDataSetChanged(); // Update RecyclerView when selectable times list changes
    }

    public void setDisabledTimes(List<String> disabledTimes) {
        this.disabledTimes = disabledTimes;
        notifyDataSetChanged(); // Update RecyclerView when disabled times list changes
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_time, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() / 4.0f);
        view.setLayoutParams(layoutParams);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String time = times.get(position);
        holder.setData(time);

        // Check if the time is in disabled list
        if (disabledTimes != null && disabledTimes.contains(time)) {
            holder.setEnabled(false); // Disable item if it's in disabled list
        } else {
            holder.setEnabled(true);
        }

        // Check if the item is selected or not
        if (position == selectedItem) {
            holder.setSelected(true); // Set background color to blue, text color to white
        } else {
            holder.setSelected(false); // Reset to default background
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save selected item position
                int previousSelectedItem = selectedItem;
                selectedItem = holder.getAdapterPosition();

                // Notify to update UI for selected and previously selected item
                notifyItemChanged(selectedItem);
                notifyItemChanged(previousSelectedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public int getSelectedTimePosition() {
        return selectedItem;
    }

    protected class VH extends RecyclerView.ViewHolder {
        private TextView txtTime;

        public VH(@NonNull View v) {
            super(v);
            txtTime = v.findViewById(R.id.txtTime);
        }

        public void setData(String time) {
            txtTime.setText(time);
        }

        public void setEnabled(boolean enabled) {
            itemView.setEnabled(enabled);
            itemView.setAlpha(enabled ? 1.0f : 0.5f); // Điều chỉnh độ mờ để chỉ ra tính khả dụng
        }

        public void setSelected(boolean selected) {
            if (selected) {
                itemView.setBackgroundResource(R.color.light_blue_600); // Thiết lập background màu xanh
                txtTime.setTextColor(context.getResources().getColor(android.R.color.white)); // Thiết lập màu chữ trắng
            } else {
                itemView.setBackgroundResource(android.R.color.transparent); // Đặt lại background mặc định
                txtTime.setTextColor(context.getResources().getColor(android.R.color.black)); // Đặt lại màu chữ mặc định
            }
        }
    }
}
