package com.supergym.sep490_supergymmanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.MembershipAdapter;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.MembershipPackage;
import com.supergym.sep490_supergymmanagement.models.QrCodeRequest;
import com.supergym.sep490_supergymmanagement.models.QrCodeResponse;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MembershipActivity extends AppCompatActivity {

    private TextView basicPackageTextView;
    private TextView premiumPackageTextView;
    private TextView elitePackageTextView;
    private CardView btnReturnBtn;
    private FirebaseAuth mAuth;

    private ProgressBar loadingSpinner;


    private int currentQrIndex = 0; // To keep track of which QR code is currently being shown

    private List<String> qrCodes = new ArrayList<>(); // Store QR code data
    private List<String> qrNames = new ArrayList<>(); // Store QR code membership names
    private List<String> priceSubsInfo = new ArrayList<>(); // Store QR code prices

    private MaterialButton basicPackageButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_packages);

        // Initialize the text views for displaying the membership data
        btnReturnBtn = findViewById(R.id.btnReturnBtn);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        mAuth = FirebaseAuth.getInstance();

        //  basicPackageButton4.setOnClickListener(v -> generateQrCodeDialog());
        btnReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                onBackPressed();
            }
        });
//        // Initialize Retrofit
        ApiService apiService = RetrofitClient.getApiService(this);

        // Make the API call
        Call<List<MembershipPackage>> call = apiService.getMembershipPackages();
        call.enqueue(new Callback<List<MembershipPackage>>() {
            @Override
            public void onResponse(Call<List<MembershipPackage>> call, Response<List<MembershipPackage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MembershipPackage> packages = response.body();
                    updateUI(packages);
                } else {
                    try {
                        Log.e("MembershipActivity", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MembershipPackage>> call, Throwable t) {
                Log.e("MembershipActivity", "API Call Failed: " + t.getMessage());
            }
        });
    }

    // Method to update the UI with the fetched data
    private void updateUI(List<MembershipPackage> packages) {
        RecyclerView recyclerView = findViewById(R.id.membershipRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MembershipAdapter adapter = new MembershipAdapter(this, packages);
        recyclerView.setAdapter(adapter);
    }
    private void showLoading() {
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    // Hide the loading spinner
    private void hideLoading() {
        loadingSpinner.setVisibility(View.GONE);
    }


    public void generateQrCodeDialog(String gymMembershipId) {
        // Fetch the QR codes dynamically using the Retrofit API call
        ApiService apiService = RetrofitClient.getApiService(this);
        showLoading();

        // Create a RegisterPackageRequest
        QrCodeRequest request = new QrCodeRequest();
        request.setEmails(Collections.singletonList(FirebaseAuth.getInstance().getCurrentUser().getEmail())); // Use logged-in user's email
        request.setGymMembershipId(gymMembershipId); // Replace with selected GymMembershipId
        request.setQrPayment(true);

        // Optional fields
        request.setDuration(0); // Example duration in months
        request.setSelectedTimeSlot("empty"); // Example time slot
        request.setMonWedFri(true); // Example schedule

        // Call the API to generate QR codes
        apiService.generateQrCodes(request).enqueue(new retrofit2.Callback<List<QrCodeResponse.QrItem>>() {
            @Override
            public void onResponse(Call<List<QrCodeResponse.QrItem>> call, Response<List<QrCodeResponse.QrItem>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<QrCodeResponse.QrItem> qrItems = response.body();

                    if (!qrItems.isEmpty()) {
                        qrCodes.clear();
                        qrNames.clear();
                        priceSubsInfo.clear();

                        for (QrCodeResponse.QrItem qrItem : qrItems) {
                            qrCodes.add(qrItem.getQrDataUrl());
                            qrNames.add(qrItem.getDetails().getGymMembership().getName());
                            priceSubsInfo.add(qrItem.getDetails().getGymMembership().getTotalPrice() + " VND");
                        }

                        showQrCodeDialog();
                    } else {
                        Toast.makeText(MembershipActivity.this, "No QR codes available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Check if the error body contains the message from the exception
                    try {
                        // Assuming the error message is in the response body
                        String errorMessage = response.errorBody().string();
                        Toast.makeText(MembershipActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MembershipActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<QrCodeResponse.QrItem>> call, Throwable t) {
                hideLoading();
                Toast.makeText(MembershipActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Utility method to show the QR code dialog
    private void showQrCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MembershipActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.qr_code_dialog, null);

        ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
        TextView qrCodeInfo = dialogView.findViewById(R.id.tvQrInfo);
        TextView priceSubs = dialogView.findViewById(R.id.priceSubs);

        currentQrIndex = 0; // Initialize the index
        updateQrCodeDisplay(qrCodeImageView, qrCodeInfo, priceSubs, currentQrIndex);

        /*btnNextQr.setOnClickListener(v -> {
            currentQrIndex = (currentQrIndex + 1) % qrCodes.size();
            updateQrCodeDisplay(qrCodeImageView, qrCodeInfo, priceSubs, currentQrIndex);
        });

        btnPrevQr.setOnClickListener(v -> {
            currentQrIndex = (currentQrIndex - 1 + qrCodes.size()) % qrCodes.size();
            updateQrCodeDisplay(qrCodeImageView, qrCodeInfo, priceSubs, currentQrIndex);
        });
*/
        builder.setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateQrCodeDisplay(ImageView qrCodeImageView, TextView qrCodeInfo, TextView priceSubs, int index) {
        try {
            // Check if the qrCodes.get(index) is a Data URI
            if (qrCodes.get(index).startsWith("data:image/png;base64,")) {
                String base64 = qrCodes.get(index).substring("data:image/png;base64,".length());
                byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                qrCodeImageView.setImageBitmap(decodedByte);
            } else {
                // Generate the QR code using ZXing if it's not a Data URI
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                        qrCodes.get(index), BarcodeFormat.QR_CODE, 400, 400);
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qrCodeImageView.setImageBitmap(bitmap);
            }

            // Update the TextViews
            qrCodeInfo.setText(qrNames.get(index)); // Display the membership name
            priceSubs.setText(priceSubsInfo.get(index)); // Display the price
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MembershipActivity.this, "Error displaying QR Code", Toast.LENGTH_SHORT).show();
        }
    }


}