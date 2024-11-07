package com.example.sep490_supergymmanagement;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sep490_supergymmanagement.FaceRecognition.GraphicOverlay;
import com.example.sep490_supergymmanagement.FaceRecognition.SimilarityClassifier;
import com.example.sep490_supergymmanagement.ImageToBitmapConverter.YuvToRgbConverter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FaceCaptureActivity extends AppCompatActivity  implements TextToSpeech.OnInitListener {
    private static final String TAG = "FaceCaptureActivity";
    private static final int PERMISSION_CODE = 1001;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;
    private GraphicOverlay graphicOverlay;
    private ImageView previewImg;
    private TextView detectionTextView;
    private TextToSpeech tts;
    private final HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces
    private Interpreter tfLite;
    private boolean flipX = false;
    private boolean start = true;
    private float[][] embeddings;

    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;
    private static final int INPUT_SIZE = 112;
    private static final int OUTPUT_SIZE=192;

    private static String finalName ="NotFound";

    private String  nameFinal = null;
    private CardView btnReturn;

    private Bitmap capturedFaceImage = null;  // method to get the captured image (e.g., from the camera)
    private String userId = "user123"; // Replace this with the actual user ID

    private boolean isWelcomeMessagePlaying = false; // Flag to track if welcome message is playing


    private String lastRecognizedName = ""; // Keeps track of the last recognized name
    private boolean isNameChanged = false; // Flag to check if the name has changed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText( FaceCaptureActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        tts = new TextToSpeech(this, this);
        userId = user.getUid();
        setContentView(R.layout.activity_face_capture);
        previewView = findViewById(R.id.previewView);
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        previewImg = findViewById(R.id.preview_img);
        detectionTextView = findViewById(R.id.detection_text);
        loadUserInfor();
        ImageButton addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener((v -> addFace()));

        btnReturn = findViewById(R.id.returnCardView);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simulate the back button press
                onBackPressed();
            }
        });


        ImageButton switchCamBtn = findViewById(R.id.switch_camera);
        switchCamBtn.setOnClickListener((view -> switchCamera()));

        loadModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }


    /** Permissions Handler */
    private void getPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (requestCode == PERMISSION_CODE) {
            setupCamera();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /** Setup camera & use cases */
    private void startCamera() {
        if(ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            getPermissions();
        }
    }

    private void setupCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindAllCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "cameraProviderFuture.addListener Error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
            } else {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.d("TTS", "Speech started");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.d("TTS", "Speech completed");
                        isWelcomeMessagePlaying = false; // Reset flag when done
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e("TTS", "Speech error");
                        isWelcomeMessagePlaying = false; // Reset flag on error
                    }
                });
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }



    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        builder.setTargetAspectRatio(AspectRatio.RATIO_4_3);
        builder.setTargetRotation(getRotation());

        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider
                    .bindToLifecycle(this, cameraSelector, previewUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind preview", e);
        }
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }

        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        builder.setTargetAspectRatio(AspectRatio.RATIO_4_3);
        builder.setTargetRotation(getRotation());

        analysisUseCase = builder.build();
        analysisUseCase.setAnalyzer(cameraExecutor, this::analyze);

        try {
            cameraProvider
                    .bindToLifecycle(this, cameraSelector, analysisUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind analysis", e);
        }
    }

    protected int getRotation() throws NullPointerException {
        return previewView.getDisplay().getRotation();
    }

    private void switchCamera() {
        if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            lensFacing = CameraSelector.LENS_FACING_FRONT;
            flipX = true;
        } else {
            lensFacing = CameraSelector.LENS_FACING_BACK;
            flipX = false;
        }

        if(cameraProvider != null) cameraProvider.unbindAll();
        startCamera();
    }

    /** Face detection processor */
    @SuppressLint("UnsafeOptInUsageError")
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );



        FaceDetector faceDetector = FaceDetection.getClient();

        faceDetector.process(inputImage)
                .addOnSuccessListener(faces -> onSuccessListener(faces, inputImage))
                .addOnFailureListener(e -> Log.e(TAG, "Barcode process failure", e))
                .addOnCompleteListener(task -> image.close());
    }

    private Bitmap convertInputImageToBitmap(InputImage inputImage) {
        if (inputImage.getMediaImage() != null) {
            // Convert to Bitmap using YuvToRgbConverter or similar logic
            Image mediaImage = inputImage.getMediaImage();
            Bitmap bitmap = imageToBitmap(mediaImage);
            mediaImage.close();
            return bitmap;
        }
        return null;
    }

    private Bitmap imageToBitmap(Image image) {
        // Create a YUV to RGB converter if needed or use ImageUtils from AndroidX
        YuvToRgbConverter converter = new YuvToRgbConverter(this);
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        converter.yuvToRgb(image, bitmap);
        return bitmap;
    }



    private void onSuccessListener(List<Face> faces, InputImage inputImage) {
        Rect boundingBox = null;
        String name = null;
        float scaleX = (float) previewView.getWidth() / (float) inputImage.getHeight();
        float scaleY = (float) previewView.getHeight() / (float) inputImage.getWidth();

        if(faces.size() > 0) {
            detectionTextView.setText(R.string.face_detected);
            // get first face detected
            Face face = faces.get(0);

            // get bounding box of face;
            boundingBox = face.getBoundingBox();

            // convert img to bitmap & crop img
            Bitmap bitmap = mediaImgToBmp(
                    inputImage.getMediaImage(),
                    inputImage.getRotationDegrees(),
                    boundingBox);

            if(start) name = recognizeImage(bitmap);
            if(name != null) detectionTextView.setText("Face Detected: "+ name);
            if(finalName != null) detectionTextView.setText(finalName);
        }
        else {
            detectionTextView.setText(R.string.no_face_detected);
        }

        graphicOverlay.draw(boundingBox, scaleX, scaleY, name);
    }

    /** Recognize Processor */
    /** Recognize Processor */
    /** Recognize Processor */
    /** Recognize Processor */


    private void addFace() {
        start = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register FaceID for User:");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(nameFinal);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        // Make the input uneditable
        input.setFocusable(false);
        input.setClickable(false);
        input.setMaxWidth(200);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("ADD", (dialog, which) -> {
            String faceName = input.getText().toString(); // Face name entered by the user
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("faces");

            // Query Firebase to check if userId already exists
            databaseRef.orderByChild("userId").equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // userId already registered
                                Toast.makeText(getApplicationContext(), "Face already registered for this user. Cannot register again.", Toast.LENGTH_SHORT).show();
                                start = true; // Reset start flag
                            } else {
                                // userId not yet registered; proceed with registration
                                SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                                        "0", "", -1f);
                                result.setExtra(embeddings); // embeddings should be a float[][] representing the face features

                                // Add the result to the registered map
                                registered.put(faceName, result);
                                start = true;

                                // Convert float[][] to List<List<Float>> for Firebase storage
                                List<List<Float>> embeddingList = new ArrayList<>();
                                for (float[] row : embeddings) {
                                    List<Float> rowList = new ArrayList<>();
                                    for (float value : row) {
                                        rowList.add(value);
                                    }
                                    embeddingList.add(rowList);
                                }

                                // Prepare data to store in Firebase
                                Map<String, Object> faceData = new HashMap<>();
                                faceData.put("userId", userId); // Include userId in the data
                                faceData.put("name", faceName);
                                faceData.put("embeddings", embeddingList);

                                // Push data to Firebase
                                databaseRef.child(faceName).setValue(faceData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Successfully written to Firebase
                                            Toast.makeText(getApplicationContext(), "Face data stored in Firebase", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to write to Firebase
                                            Toast.makeText(getApplicationContext(), "Failed to store face data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle potential errors
                            Toast.makeText(getApplicationContext(), "Error checking data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            start = true;
                        }
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            start = true;
            dialog.cancel();
        });

        builder.show();
    }
    private void loadUserInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                         nameFinal = dataSnapshot.child("name").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }


    public String recognizeImage(final Bitmap bitmap) {
        // Set image to preview
        previewImg.setImageBitmap(bitmap);

        // Create ByteBuffer to store normalized image
        ByteBuffer imgData = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3 * 4);
        imgData.order(ByteOrder.nativeOrder());

        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];

        // Get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();

        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                int pixelValue = intValues[i * INPUT_SIZE + j];
                imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            }
        }

        // imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();
        embeddings = new float[1][OUTPUT_SIZE]; // Output of model will be stored in this variable
        outputMap.put(0, embeddings);

        // Run model
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); // Run model to get embeddings

        // Retrieve registered faces from Firebase and compare
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("faces");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float minDistance = Float.MAX_VALUE;
                String closestName = "unknown";

                for (DataSnapshot faceSnapshot : dataSnapshot.getChildren()) {
                    String name = faceSnapshot.child("name").getValue(String.class);
                    List<List<Double>> storedEmbeddingList = (List<List<Double>>) faceSnapshot.child("embeddings").getValue();

                    // Convert List<List<Double>> to float[][]
                    float[][] storedEmbedding = new float[storedEmbeddingList.size()][storedEmbeddingList.get(0).size()];
                    for (int i = 0; i < storedEmbeddingList.size(); i++) {
                        for (int j = 0; j < storedEmbeddingList.get(i).size(); j++) {
                            storedEmbedding[i][j] = storedEmbeddingList.get(i).get(j).floatValue();
                        }
                    }

                    // Calculate distance between current embedding and registered embedding
                    float distance = calculateDistance(embeddings[0], storedEmbedding[0]);

                    if (distance < minDistance) {
                        minDistance = distance;
                        closestName = name;
                        finalName = closestName;
                    }
                }

                // Check if the closest match is within the acceptable threshold
                if (minDistance < 1.000f) {
                    delayedWelcomeMessage(closestName);
                    finalName = closestName;
                } else {
                    finalName = closestName;
                    closestName = "unknown";  // Assign 'unknown' if no match found within threshold
                }

                // Check if the name has changed from the last recognized name
                if (!closestName.equals(lastRecognizedName)) {
                    isNameChanged = true;
                    // Store face image with the correct name
                    storeRecognizedFaceImage(bitmap, closestName);
                    lastRecognizedName = closestName; // Update the last recognized name after storing
                } else {
                    isNameChanged = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getApplicationContext(), "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }


    // Method to upload recognized face image to Firebase Storage
    private void storeRecognizedFaceImage(Bitmap bitmap, String userId) {
        // Convert Bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Create a unique file name based on user ID and timestamp
        String fileName = "faces/" + (userId.equals("Unknown") ? "Unknown" : userId) + "_" + System.currentTimeMillis() + ".jpg";

        // Upload image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);
        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getApplicationContext(), "Face image saved successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to save face image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void delayedWelcomeMessage(final String closestName) {
        // Check if the welcome message is already playing
        if (!isWelcomeMessagePlaying) {
            isWelcomeMessagePlaying = true; // Set flag to prevent multiple calls

            // Delay the execution of playWelcomeMessage by 5 seconds
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    playWelcomeMessage(closestName);
                }
            }, 0000); // 5000 ms = 5 seconds
        }
    }

    public void playWelcomeMessage(String closestName) {
        if (tts != null && isWelcomeMessagePlaying) {
            String welcomeMessage = "Welcome to SuperGym, " + closestName + "!";
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "welcomeMessage");

            // Queue the message to play to completion
            tts.speak(welcomeMessage, TextToSpeech.QUEUE_FLUSH, params);
        }
    }
    // Helper method to calculate distance between two embeddings
    private float calculateDistance(float[] embedding1, float[] embedding2) {
        float sum = 0;
        for (int i = 0; i < embedding1.length; i++) {
            float diff = embedding1[i] - embedding2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }


    //Compare Faces by distance between face embeddings
    private Pair<String, Float> findNearest(float[] emb) {

        Pair<String, Float> ret = null;
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {

            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                ret = new Pair<>(name, distance);
            }
        }

        return ret;

    }

    /** Bitmap Converter */
    private Bitmap mediaImgToBmp(Image image, int rotation, Rect boundingBox) {
        //Convert media image to Bitmap
        Bitmap frame_bmp = toBitmap(image);

        //Adjust orientation of Face
        Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rotation, flipX);

        //Crop out bounding box from whole Bitmap(image)
        float padding = 0.0f;
        RectF adjustedBoundingBox = new RectF(
                boundingBox.left - padding,
                boundingBox.top - padding,
                boundingBox.right + padding,
                boundingBox.bottom + padding);
        Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, adjustedBoundingBox);

        //Resize bitmap to 112,112
        return getResizedBitmap(cropped_face);
    }

    private Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) 112) / width;
        float scaleHeight = ((float) 112) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawRect(//from  w w  w. ja v  a  2s. c  om
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        canvas.drawBitmap(source, matrix, paint);

        if (source != null && !source.isRecycled()) {
            source.recycle();
        }

        return resultBitmap;
    }

    private static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }

    private Bitmap toBitmap(Image image) {

        byte[] nv21=YUV_420_888toNV21(image);


        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    /** Model loader */
    @SuppressWarnings("deprecation")
    private void loadModel() {
        try {
            //model name
            String modelFile = "mobile_face_net.tflite";
            tfLite = new Interpreter(loadModelFile(FaceCaptureActivity.this, modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}