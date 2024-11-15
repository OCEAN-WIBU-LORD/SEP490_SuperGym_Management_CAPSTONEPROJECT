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

import java.util.ArrayList;
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
        basicPackageTextView = findViewById(R.id.basicPackageTextView);
        premiumPackageTextView = findViewById(R.id.premiumPackageTextView);
        elitePackageTextView = findViewById(R.id.elitePackageTextView);
        btnReturnBtn = findViewById(R.id.btnReturnBtn);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        basicPackageButton4 = findViewById(R.id.basicPackageButton4);
        mAuth = FirebaseAuth.getInstance();

        basicPackageButton4.setOnClickListener(v -> generateQrCodeDialog());
        btnReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                onBackPressed();
            }
        });
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-csharp-api-url.com/") // replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        // Make the API call
        Call<List<MembershipPackage>> call = api.getMembershipPackages();
        call.enqueue(new Callback<List<MembershipPackage>>() {
            @Override
            public void onResponse(Call<List<MembershipPackage>> call, Response<List<MembershipPackage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle the API response
                    List<MembershipPackage> packages = response.body();
                    updateUI(packages);
                } else {
                    Log.e("MembershipActivity", "API Response Failed");
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
        for (MembershipPackage membershipPackage : packages) {
            switch (membershipPackage.getName()) {
                case "Basic":
                    basicPackageTextView.setText(membershipPackage.getPrice() + " - " + membershipPackage.getDescription());
                    break;
                case "Premium":
                    premiumPackageTextView.setText(membershipPackage.getPrice() + " - " + membershipPackage.getDescription());
                    break;
                case "Elite":
                    elitePackageTextView.setText(membershipPackage.getPrice() + " - " + membershipPackage.getDescription());
                    break;
            }
        }
    }
    private void showLoading() {
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    // Hide the loading spinner
    private void hideLoading() {
        loadingSpinner.setVisibility(View.GONE);
    }



    private void generateQrCodeDialog() {
        // Fetch the QR codes dynamically using the Retrofit API call
        ApiService apiService = RetrofitClient.getApiService();
        showLoading();

        // Assuming you have the user UID stored
        String uid = mAuth.getUid(); // Replace this with the actual user UID
        QrCodeRequest request = new QrCodeRequest(uid);

        // Call the API to generate QR codes
        apiService.generateQrCodes(request).enqueue(new retrofit2.Callback<QrCodeResponse>() {
            @Override
            public void onResponse(Call<QrCodeResponse> call, Response<QrCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the QR items from the API response
                    hideLoading();
                    List<QrCodeResponse.QrItem> qrItems = response.body().getQrList();

                    // Check if the list is not empty
                    if (!qrItems.isEmpty()) {
                        // Clear any existing data
                        qrCodes.clear();
                        qrNames.clear();
                        priceSubsInfo.clear();

                        for (QrCodeResponse.QrItem qrItem : qrItems) {
                            qrCodes.add(qrItem.getQrDataUrl()); // Get the QR data URL
                            qrNames.add(qrItem.getCourseDetails().getCourseName()); // Get the course name
                            priceSubsInfo.add(qrItem.getCourseDetails().getCoursePrice() + " VND"); // Get the course price
                        }

                        // Create the AlertDialog and set up its layout
                        AlertDialog.Builder builder = new AlertDialog.Builder(MembershipActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.qr_code_dialog, null);

                        ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
                        TextView qrCodeInfo = dialogView.findViewById(R.id.tvQrInfo);
                        TextView priceSubs = dialogView.findViewById(R.id.priceSubs);
                        Button btnPrevQr = dialogView.findViewById(R.id.btnPrevQr);
                        Button btnNextQr = dialogView.findViewById(R.id.btnNextQr);

                        TextView tvUserTransferNote = dialogView.findViewById(R.id.tvUserTransferNote);
                        tvUserTransferNote.setText(Html.fromHtml("<u>Wait 1 to 2 mins for the payment confirmation</u>"));



                        // Display the first QR code and its corresponding course info
                        currentQrIndex = 0; // Initialize the index to 0
                        updateQrCodeDisplay(qrCodeImageView, qrCodeInfo, priceSubs, currentQrIndex);

                        // Handle the "Next" button click to go to the next QR code
                        btnNextQr.setOnClickListener(v -> {
                            currentQrIndex = (currentQrIndex + 1) % qrCodes.size(); // Loop to the next QR code
                            updateQrCodeDisplay(qrCodeImageView, qrCodeInfo, priceSubs, currentQrIndex);
                        });

                        // Handle the "Previous" button click to go to the previous QR code
                        btnPrevQr.setOnClickListener(v -> {
                            currentQrIndex = (currentQrIndex - 1 + qrCodes.size()) % qrCodes.size(); // Loop to the previous QR code
                            updateQrCodeDisplay(qrCodeImageView, qrCodeInfo, priceSubs, currentQrIndex);
                        });

                        builder.setView(dialogView)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    } else {
                        // Handle the case when no QR codes are returned
                        Toast.makeText(MembershipActivity.this, "No QR codes available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideLoading();
                    // Handle the case when the API call fails
                    Toast.makeText(MembershipActivity.this, "Failed to fetch QR codes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QrCodeResponse> call, Throwable t) {
                // Handle the case when the API call fails
                Log.e("Error", "Failed to fetch QR codes", t);
                Toast.makeText(MembershipActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
