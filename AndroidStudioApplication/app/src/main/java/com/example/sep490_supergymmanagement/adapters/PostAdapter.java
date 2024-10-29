package com.example.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {
    private List<Post> data;
    private Context context;

    public PostAdapter(List<Post> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Post p = data.get(position);
        holder.setData(p);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDate;
        private ImageView ivAvatar;

        private void bindingView() {
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }

        public VH(@NonNull View itemView) {
            super(itemView);
            bindingView();
        }

        public void setData(Post p) {
            tvTitle.setText(p.getTitle());

            // Định dạng ngày đăng
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dateString = dateFormat.format(p.getDate());
            tvDate.setText("Ngày đăng: " + dateString);

            // Sử dụng Picasso để tải ảnh từ URL
            Picasso.get()
                    .load(p.getThumbnailUrl())
                    //.placeholder(R.drawable.avatar) // Ảnh mặc định khi tải
                    .error(R.drawable.avatar)       // Ảnh hiển thị khi lỗi
                    .into(ivAvatar);
        }
    }
}
