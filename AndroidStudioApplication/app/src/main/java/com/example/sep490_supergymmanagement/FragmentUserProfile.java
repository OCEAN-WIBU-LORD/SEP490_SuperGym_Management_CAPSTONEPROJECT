package com.example.sep490_supergymmanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import com.example.sep490_supergymmanagement.fragments.HomeFragment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class FragmentUserProfile extends Fragment {

    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;
    private static final String PHONE_NUMBER = "0978788128";
    private Boolean isAuthenticated = false;
    private Button takePermission, cardViewer, generateQrCodeBtn;;
    private CardView returnBtn, editProfile;
    private FirebaseAuth mAuth;
    private ImageView userAvatarImg ;
    private TextView tvName, tvPhone, tvDob;
    private ConstraintLayout logOutBtn;

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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        new UserResp().getUser(currentUser.getUid(), new Callback<User>() {
            @Override
            public void onCallback(List<User> objects) {
                // Set user data to the views
                User user = objects.get(0);
                tvName.setText(user.getName());
                tvPhone.setText(user.getPhone());
                String dob = "";
                if (user.getDob() != null) {
                    dob = new SimpleDateFormat("dd/MM/yyyy").format(user.getDob());
                }
                tvDob.setText(dob);
            }
        });
        returnBtn  = view.findViewById(R.id.returnBtn);
        editProfile = view.findViewById(R.id.editCardView);
        //      cardViewer.setOnClickListener(v -> replaceFragment(new LibraryFragment()));
        editProfile.setOnClickListener(v->replaceFragment(new FragmentEditProfile()));
        returnBtn.setOnClickListener(v -> replaceFragment(new HomeFragment()));

        logOutBtn = view.findViewById(R.id.logOutBtn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Log Out successfully", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });


        // Find the button and set up a click listener
        generateQrCodeBtn = view.findViewById(R.id.generateQrCodeBtn);

        generateQrCodeBtn.setOnClickListener(v -> generateBankTransferQrCode());
        return view;
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
                        // Retrieve and set other user data
                        String name = dataSnapshot.child("name").getValue(String.class);

                        String dob = "";
                        DataSnapshot dobSnapshot = dataSnapshot.child("dob").child("time");
                        if (dobSnapshot.exists()) {
                            long dobTimestamp = dobSnapshot.getValue(Long.class);
                            if (dobTimestamp != 0) {
                                com.google.firebase.Timestamp dobTs = new com.google.firebase.Timestamp(dobTimestamp, 0);
                                dob = new SimpleDateFormat("dd/MM/yyyy").format(dobTs.toDate());
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
        fragmentTransaction.replace(R.id.frame_layout, fragment);
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
        String bankAccountNumber = "123456789"; // Receiver's bank account number
        String bankIdentifier = "BANK001"; // Unique bank identifier (e.g., SWIFT or UPI ID)
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




}
