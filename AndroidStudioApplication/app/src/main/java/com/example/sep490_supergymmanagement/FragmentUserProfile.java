package com.example.sep490_supergymmanagement;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.example.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.example.sep490_supergymmanagement.fragments.BMI_Statistic_Fragment;
import com.example.sep490_supergymmanagement.fragments.Diet_Eating_Fragment;
import com.example.sep490_supergymmanagement.fragments.Diet_Fragment2;
import com.example.sep490_supergymmanagement.fragments.FeedbackFragment;
import com.example.sep490_supergymmanagement.fragments.HomeFragment;
import com.example.sep490_supergymmanagement.models.MembershipPackage;
import com.example.sep490_supergymmanagement.models.QrCodeRequest;
import com.example.sep490_supergymmanagement.models.QrCodeResponse;
import com.example.sep490_supergymmanagement.models.User;
import com.example.sep490_supergymmanagement.repositories.UserResp;
import com.example.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import android.util.Base64;


import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import retrofit2.Call;
import retrofit2.Response;

public class FragmentUserProfile extends Fragment {

    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;
    private static final String PHONE_NUMBER = "0978788128";
    private Boolean isAuthenticated = false;
    private Button takePermission, cardViewer, generateQrCodeBtn, btn_bmi_Statistic, btnFeedBack, dietBtn, resetPasswordBtn;
    private CardView returnBtn, editProfile;
    private FirebaseAuth mAuth;

    private String publicName;
    private ImageView userAvatarImg ;
    private TextView tvName, tvPhone, tvDob;
    private Button logOutBtn;

    private String dobText;


    private Date dob;

    private int currentQrIndex = 0; // To keep track of which QR code is currently being shown
    private List<String> qrCodes = new ArrayList<>(); // Store QR code data
    private List<String> qrNames = new ArrayList<>(); // Store QR code membership names
    private List<String> priceSubsInfo = new ArrayList<>(); // Store QR code prices
    private String userInforTransfer;




    public FragmentUserProfile() {
        // Required empty public constructor
    }

    public static FragmentUserProfile newInstance(String param1, String param2) {
        FragmentUserProfile fragment = new FragmentUserProfile();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUserInfor();
        loadUserAvatar();
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_user_profile, container, false);
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvDob = view.findViewById(R.id.tvDob);
        userAvatarImg = view.findViewById(R.id.userAvatarImg);
        btn_bmi_Statistic = view.findViewById(R.id.bmi_Statistic);

        btnFeedBack = view.findViewById(R.id.btnFeedBack);

        dietBtn = view.findViewById(R.id.dietBtn);
        resetPasswordBtn = view.findViewById(R.id.resetPasswordBtn);
        mAuth = FirebaseAuth.getInstance();

        returnBtn  = view.findViewById(R.id.returnBtn);
        editProfile = view.findViewById(R.id.editCardView);
        //      cardViewer.setOnClickListener(v -> replaceFragment(new LibraryFragment()));
        editProfile.setOnClickListener(v->replaceFragment(new FragmentEditProfile()));
        returnBtn.setOnClickListener(v -> replaceFragment(new HomeFragment()));
        btn_bmi_Statistic.setOnClickListener(V -> replaceFragment(new BMI_Statistic_Fragment()));
        dietBtn.setOnClickListener(V -> replaceFragment(new Diet_Eating_Fragment()));
        Button transactionHistoryBtn = view.findViewById(R.id.text_price4);
        transactionHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
        btnFeedBack.setOnClickListener(V -> replaceFragment(new FeedbackFragment()));


        resetPasswordBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start ScheduleTrainerDetailsActivity
                Intent intent = new Intent(getActivity(), ResetPassActivity.class); // Assuming ScheduleTrainerDetails is now an Activity
                startActivity(intent);
            }
        });
        logOutBtn = view.findViewById(R.id.logOutBtn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the custom layout for the dialog
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View dialogView = inflater.inflate(R.layout.dialog_logout_confirmation, null);

                // Create a new dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                // Find the buttons and set their click listeners
                Button btnYes = dialogView.findViewById(R.id.btn_yes);
                Button btnNo = dialogView.findViewById(R.id.btn_no);

                // Create the dialog
                AlertDialog dialog = builder.create();

                // Handle the Yes button click
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Perform logout operation
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Log Out successfully", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });

                // Handle the No button click
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Just dismiss the dialog
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });




        // Find the button and set up a click listener
        generateQrCodeBtn = view.findViewById(R.id.generateQrCodeBtn);

        //generateQrCodeBtn.setOnClickListener(v -> generateQrCodeDialog());
        generateQrCodeBtn.setOnClickListener(new View.OnClickListener()
        {
             public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MembershipActivity.class);
               // Assuming ScheduleTrainerDetails is now an Activity
                 startActivity(intent);
             }
        }
);


        return view;
    }
    // Show the loading spinner

    private void loadUserInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String avatarBase64 = dataSnapshot.child("userAvatar").getValue(String.class);
                        if (avatarBase64 != null) {
                            try {
                                Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
                                userAvatarImg.setImageBitmap(avatarBitmap);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                // Handle the case where the Base64 string could not be decoded
                            }
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
    public Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment);  // Use add instead of replace
        fragmentTransaction.addToBackStack(null);  // Add to backstack so user can go back
        fragmentTransaction.commit();
    }




    private void requestCallPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CALL_PHONE)) {
                showPermissionRationale();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
            }
        } else {
            Toast.makeText(getContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
            if (isAuthenticated) {
                makePhoneCall();
            }
        }
    }

    private void showPermissionRationale() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Needed")
                .setMessage("This permission is needed to make phone calls directly from the app.")
                .setPositiveButton("OK", (dialog, which) -> {
                    isAuthenticated = true;
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                showPermissionRequiredWarning();
            }
        }
    }

    private void makePhoneCall() {
        String dial = "tel:" + PHONE_NUMBER;
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
    }

    private void showPermissionRequiredWarning() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Required")
                .setMessage("Phone call permission is required to call the host. Please grant the permission.")
                .setPositiveButton("OK", (dialog, which) -> requestCallPermission())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    private void generateQrCode() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            try {
                // Generate QR code
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                        userId, BarcodeFormat.QR_CODE, 400, 400);
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                // Create a dialog to show the QR code
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.qr_code_dialog, null);

                // Set the ImageView with the generated QR code
                ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
                qrCodeImageView.setImageBitmap(bitmap);

                builder.setView(dialogView)
                        .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();

            } catch (WriterException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error generating QR Code", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generateBankTransferQrCode() {
        // Example payment details
        String bankAccountNumber = "17001010977197"; // Receiver's bank account number
        String bankIdentifier = "MCOBVNVX007"; // Unique bank identifier (e.g., SWIFT or UPI ID)
        String amount = "100.00"; // Amount to transfer
        String currency = "USD"; // Currency code
        String paymentRef = "INV001"; // Optional: Payment reference

        // Create a custom string for the QR code (you can follow specific formats like UPI or others)
        String qrCodeData = "BANK_TRANSFER|" +
                "BANK_ID:" + bankIdentifier + "|" +
                "ACCOUNT:" + bankAccountNumber + "|" +
                "AMOUNT:" + amount + "|" +
                "CURRENCY:" + currency + "|" +
                "REFERENCE:" + paymentRef;

        try {
            // Generate QR code
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                    qrCodeData, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Create a dialog to show the QR code
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.qr_code_dialog, null);

            // Set the ImageView with the generated QR code
            ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
            qrCodeImageView.setImageBitmap(bitmap);

            builder.setView(dialogView)
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }


    private void generateTransferQrCode() {
        // The QR code data you provided
        String qrCodeData = "00020101021238500010A000000727012000069704260106ODGLAB0208QRIBFTTA53037045406" +
                "100000" +
                "5802VN62170813" +
                "TransferInfor" +
                "63042A48";

        try {
            // Generate the QR code using ZXing
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                    qrCodeData, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Display the QR code in an AlertDialog or directly in an ImageView
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.qr_code_dialog, null);

            ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
            qrCodeImageView.setImageBitmap(bitmap);

            builder.setView(dialogView)
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }




    private void generateTransferQrCodeWithUserId() {
        // Step 1: Retrieve the user ID from Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid(); // Firebase user ID

        // Step 2: Create a QR code string with user-specific data
        // Replace the placeholders with actual transaction data as needed
        String bankQrCodeTemplate = "00020101021238500010A000000727012000069704260106ODGLAB0208QRIBFTTA530370454061000005802VN62170813TransferInforUSERID_PLACEHOLDER63042A48";

        // Insert the Firebase user ID into the QR code data (as part of "TransferInfor")
        String qrCodeData = bankQrCodeTemplate.replace("USERID_PLACEHOLDER", userId);

        // Step 3: Generate the QR code
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                    qrCodeData, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Show the generated QR code in an ImageView or Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.qr_code_dialog, null);

            ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
            qrCodeImageView.setImageBitmap(bitmap);

            builder.setView(dialogView)
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadUserAvatar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String avatarBase64 = dataSnapshot.child("userAvatar").getValue(String.class);
                        if (avatarBase64 != null) {
                            try {
                                Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
                                userAvatarImg.setImageBitmap(avatarBitmap);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                // Handle the case where the Base64 string could not be decoded
                            }
                        }
                        // Retrieve dob components if they exist
                        DataSnapshot dobSnapshot = dataSnapshot.child("dob");
                        if (dobSnapshot.exists()) {
                            int year = dobSnapshot.child("year").getValue(Integer.class) + 1900; // Adjust year as needed
                            int month = dobSnapshot.child("month").getValue(Integer.class); // Month is 0-based in Date
                            int day = dobSnapshot.child("date").getValue(Integer.class);

                            // Create the Date object
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day);
                            Date dob = calendar.getTime();
                            String dobText = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dob);
                            tvDob.setText(dobText);
                        } else {
                            tvDob.setText("");
                            System.out.println("DOB data is missing or malformed");
                        }


                        // Retrieve and set other user data
                        String name = dataSnapshot.child("name").getValue(String.class);

                        userInforTransfer = userId;
                        publicName = userId;


                        String gender = dataSnapshot.child("gender").getValue(String.class);

                        if (name != null) {
                            tvName.setText(name);
                        }
                        if (gender != null) {
                            tvPhone.setText(gender);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }




}
