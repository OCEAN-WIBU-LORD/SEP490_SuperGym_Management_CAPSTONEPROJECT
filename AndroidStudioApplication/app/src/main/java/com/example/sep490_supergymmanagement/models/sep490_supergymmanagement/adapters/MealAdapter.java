package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Meal;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals;

    public MealAdapter(List<Meal> meals) {
        this.meals = meals;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.mealTitle.setText(meal.getType());
        holder.mealDetails.setText(meal.getDescription());
        holder.mealCalories.setText(meal.getCalories() + " kcal");

        // Set the icon for each meal type
        if (meal.getType().equals("Breakfast")) {
            holder.mealIcon.setImageResource(R.drawable.breakfast_ic);
        } else if (meal.getType().equals("Lunch")) {
            holder.mealIcon.setImageResource(R.drawable.launch);
        } else if (meal.getType().equals("Dinner")) {
            holder.mealIcon.setImageResource(R.drawable.dinner);
        } else if (meal.getType().equals("Snack")) {
            holder.mealIcon.setImageResource(R.drawable.snack);
        }
    }



    // Method to update the meal list
    public void updateMeals(List<Meal> newMeals) {
        this.meals.clear(); // Clear the old data
        this.meals.addAll(newMeals); // Add the new data
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView mealTitle;
        public TextView mealDetails;
        public TextView mealCalories;
        public ImageView mealIcon;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTitle = itemView.findViewById(R.id.mealTitle);
            mealDetails = itemView.findViewById(R.id.mealDescription);
            mealCalories = itemView.findViewById(R.id.mealCalories);
            mealIcon = itemView.findViewById(R.id.mealIcon);
        }
    }
}
