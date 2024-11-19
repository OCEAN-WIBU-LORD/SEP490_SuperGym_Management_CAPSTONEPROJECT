package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.supergym.sep490_supergymmanagement.models.Trainer;

public class TrainerDetailActivity extends AppCompatActivity {
    private TextView trainerName, trainerEmail, trainerPhone, trainerGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_detail);

        // Initialize views
        trainerName = findViewById(R.id.trainer_name);
        trainerEmail = findViewById(R.id.trainer_email);
        trainerPhone = findViewById(R.id.trainer_phone);
        trainerGender = findViewById(R.id.trainer_gender);

        // Get the passed Trainer object
        Trainer trainer = (Trainer) getIntent().getSerializableExtra("selectedTrainer");

        // Populate views with Trainer details
        if (trainer != null) {
            trainerName.setText(trainer.getName());
            trainerEmail.setText(trainer.getEmail());
            trainerPhone.setText(trainer.getPhone());
            trainerGender.setText(trainer.getGender());
        }
    }
}
