package com.supergym.sep490_supergymmanagement;
import static com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient.getApiService;

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
import android.widget.LinearLayout;
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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.supergym.sep490_supergymmanagement.FaceRecognition.GraphicOverlay;
import com.supergym.sep490_supergymmanagement.ImageToBitmapConverter.YuvToRgbConverter;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.CheckInService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
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
import com.supergym.sep490_supergymmanagement.models.CheckInRequest;
import com.supergym.sep490_supergymmanagement.models.CheckInResponse;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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
   /* private final HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces*/
    private Interpreter tfLite;
    private boolean flipX = false;
    private boolean start = true;
    private float[][] embeddings;

    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;
    private static final int INPUT_SIZE = 112;
    private static final int OUTPUT_SIZE=192;

    private static String finalName ="NotFound";
    private boolean isRegistered;

    private String  nameFinal = null;
    private CardView btnReturn;

    private Bitmap capturedFaceImage = null;  // method to get the captured image (e.g., from the camera)
    private String userId = "user123", faceNameTxt; // Replace this with the actual user ID

    private boolean isWelcomeMessagePlaying = false; // Flag to track if welcome message is playing


    private String lastRecognizedName = ""; // Keeps track of the last recognized name
    private boolean isNameChanged = false; // Flag to check if the name has changed
    private String roleCheck = null;



    private String userFaceIdFinal = "unknown";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
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

        getUserNameByUserId(userId).addOnSuccessListener(userName -> {
            // Successfully retrieved the user's name
            faceNameTxt = userName;
        }).addOnFailureListener(e -> {
            // Handle errors
            Toast.makeText(getApplicationContext(), "Error get User Name " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

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
        MyApp app = (MyApp) getApplicationContext();
        String userRole = app.getUserRole();
        if ("admin".equals(userRole)) {
            Toast.makeText(this, "This is Admin FaceCheck!", Toast.LENGTH_SHORT).show();
            roleCheck = "admin";
        }


        ImageButton switchCamBtn = findViewById(R.id.switch_camera);
        switchCamBtn.setOnClickListener((view -> switchCamera()));

        loadModel();
    }



///////========================================Start Referenced Code =============================================================================================//////////////////////////





    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }
    /** Setup camera & use cases */
    private void startCamera() {
        if(ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            getPermissions();
        }
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
    //Compare Faces by distance between face embeddings



    //Referenced Code
    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }
    //End Referenced Code

    //Referenced Code
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
    //End Referenced Code
//Referenced Code
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
    //End Referenced Code
    //Referenced Code
    protected int getRotation() throws NullPointerException {
        return previewView.getDisplay().getRotation();
    }
    //End Referenced Code
    //Referenced Code
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
//End Referenced Code
    /** Face detection processor */
    @SuppressLint("UnsafeOptInUsageError")
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );


            /* Check Face exist in cam*/
        FaceDetector faceDetector = FaceDetection.getClient();

        faceDetector.process(inputImage)
                .addOnSuccessListener(faces -> {
                    onSuccessListener(faces, inputImage);
                    ImageButton addBtn = findViewById(R.id.add_btn);
                    // Enable or disable the button based on face detection
                    if (faces.isEmpty()) {
                        addBtn.setEnabled(false); // Disable button if no face detected
                    } else {
                        addBtn.setEnabled(true); // Enable button if at least one face is detected
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Face detection failure", e))
                .addOnCompleteListener(task -> image.close());
    }
    //Referenced Code
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
//End Referenced Code


    private Bitmap imageToBitmap(Image image) {
        // Create a YUV to RGB converter if needed or use ImageUtils from AndroidX
        YuvToRgbConverter converter = new YuvToRgbConverter(this);
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        converter.yuvToRgb(image, bitmap);
        return bitmap;
    }


    //Referenced Code
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

            if(start) name = recognizeFace(bitmap);
            if(name != null) detectionTextView.setText("Face Detected: "+ name);
            if(finalName != null) detectionTextView.setText(finalName);
        }
        else {
            detectionTextView.setText(R.string.no_face_detected);
        }

        graphicOverlay.draw(boundingBox, scaleX, scaleY, name);
    }


    private void addFace() {
        start = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register FaceID for User:");

        // Set up the input for the user's name
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Enter Name");
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        nameInput.setMaxWidth(200);

        // Layout to contain multiple inputs
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(nameInput);

        // Conditionally add an email input for admin role
        final EditText emailInput = new EditText(this);


        emailInput.setHint("Enter Email Address");
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setMaxWidth(200);
        layout.addView(emailInput);

        // Allow admins to edit the name
        nameInput.setFocusable(true);
        nameInput.setClickable(true);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("ADD", (dialog, which) -> {
            String faceName = nameInput.getText().toString().trim(); // Name (editable for admin)
            String email = emailInput.getText().toString().trim(); // Email (only for admin)
                getUserIdByEmail(email).addOnSuccessListener(userId -> {
                    checkFaceAndRegisterOrUpdate(faceName, userId); // Check if the face exists
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to find user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    start = true;
                });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            start = true;
            dialog.cancel();
        });

        builder.show();
    }


    //End Referenced Code

    /** Recognize Processor */

    //Referenced Code


    public String recognizeFace(final Bitmap bitmap) {
        try {
            // Set image to preview
            previewImg.setImageBitmap(bitmap);

            // Prepare input for the model
            ByteBuffer imgData = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3 * 4);
            imgData.order(ByteOrder.nativeOrder());
            int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
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

            // Run the model
            Object[] inputArray = {imgData};
            Map<Integer, Object> outputMap = new HashMap<>();

            //Converted Bitmap To Embedding.
            embeddings = new float[1][OUTPUT_SIZE]; // Output will be stored here

            outputMap.put(0, embeddings);

            tfLite.runForMultipleInputsOutputs(inputArray, outputMap);

            // Retrieve registered faces from Firebase
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("faces");
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    float minDifferece = Float.MAX_VALUE;
                    String closestName = "unknown";
                    String closestUserId = "unknown";

                    for (DataSnapshot faceSnapshot : dataSnapshot.getChildren()) {
                        String name = faceSnapshot.child("name").getValue(String.class);
                        String userFaceId = faceSnapshot.child("userId").getValue(String.class);
                        List<List<Double>> storedEmbeddingList = (List<List<Double>>) faceSnapshot.child("embeddings").getValue();

                        // Convert List<List<Double>> to float[][]
                        if (storedEmbeddingList != null) {
                            float[][] storedEmbedding = new float[storedEmbeddingList.size()][storedEmbeddingList.get(0).size()];
                            for (int i = 0; i < storedEmbeddingList.size(); i++) {
                                for (int j = 0; j < storedEmbeddingList.get(i).size(); j++) {
                                    storedEmbedding[i][j] = storedEmbeddingList.get(i).get(j).floatValue();
                                }
                            }

                            //Compare new face with saved Faces.
                            float totalDifference = calculateDifference(embeddings[0], storedEmbedding[0]);
                            if (totalDifference < minDifferece) {
                                minDifferece = totalDifference;
                                closestName = name;
                                userFaceIdFinal = userFaceId;
                                closestUserId = userFaceId;
                            }
                        }
                    }

                    // Evaluate the result
                    if (minDifferece < 0.800f) {
                        delayedWelcomeMessage(closestName, closestUserId);
                        finalName = closestName;

                    } else {
                        closestName = "unknown";
                        finalName = closestName;
                    }

                    if (!closestName.equals(lastRecognizedName)) {
                        lastRecognizedName = closestName;

                        storeRecognizedFaceImage(bitmap, finalName);

                        // Handle the check-in process
                        handleCheckIn(userFaceIdFinal, closestName);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    // Function return the Difference of 2 Faces
    private float calculateDifference(float[] embedding1, float[] embedding2) {
        float sum = 0;
        for (int i = 0; i < embedding1.length; i++) {
            float diff = embedding1[i] - embedding2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
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
    //Referenced Code
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





///////========================================Ending Referenced Code =============================================================================================//////////////////////////



    private Task<String> getUserNameByUserId(String userId) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Query Firebase to get the user's name by userId
        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("dob").child("name").getValue(String.class);
                    taskCompletionSource.setResult(userName); // Complete the task with the user's name
                } else {
                    taskCompletionSource.setException(new Exception("No user found with the given userId."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(new Exception("Database error: " + databaseError.getMessage()));
            }
        });

        return taskCompletionSource.getTask();
    }





    //End Referenced Code
    private void checkFaceAndRegisterOrUpdate(String faceName, String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("faces");

        databaseRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Face already registered
                            new AlertDialog.Builder(FaceCaptureActivity.this)
                                    .setTitle("Face Already Registered")
                                    .setMessage("A face is already registered for this user. Do you want to update the face data?")
                                    .setPositiveButton("Update", (dialog, which) -> {
                                        updateFaceData(faceName, userId); // Update face data
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> {
                                        start = true; // Allow restarting the face capture
                                    })
                                    .show();
                        } else {
                            // No face registered, proceed with registration
                            registerFace(faceName, userId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error checking data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        start = true;
                    }
                });
    }

    private void updateFaceData(String faceName, String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("faces");

        // Prepare updated data
        List<List<Float>> embeddingList = new ArrayList<>();
        for (float[] row : embeddings) {
            List<Float> rowList = new ArrayList<>();
            for (float value : row) {
                rowList.add(value);
            }
            embeddingList.add(rowList);
        }

        Map<String, Object> faceData = new HashMap<>();
        faceData.put("userId", userId);
        faceData.put("name", faceName);
        faceData.put("embeddings", embeddingList);

        // Update the existing record in Firebase
        databaseRef.child(faceName).setValue(faceData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Face data updated successfully in Firebase", Toast.LENGTH_SHORT).show();
                    start = true;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to update face data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    start = true;
                });
    }


    // Define a callback interface
    public interface RegistrationCallback {
        void onResult(boolean isRegistered);
    }

    // Function to check registration status and use the callback to return the result
    private void checkRegistrationVer2(String registrationId, RegistrationCallback callback) {
        ApiService api = RetrofitClient.getApiService(getApplicationContext());

        api.checkRegistration(registrationId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Pass the result to the callback
                    callback.onResult(response.body());
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch registration status.", Toast.LENGTH_SHORT).show();
                    // Pass false as the default in case of failure
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                // Pass false as the default in case of error
                callback.onResult(false);
            }
        });
    }



    private void registerFace(String faceName, String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("faces");

        databaseRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // UserId already registered
                            Toast.makeText(getApplicationContext(), "Face already registered for this user. Cannot register again.", Toast.LENGTH_SHORT).show();
                            start = true;
                        } else {
                            // Convert float[][] to>  List<List<Float>for Firebase storage
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
                            faceData.put("userId", userId);
                            faceData.put("name", faceName);
                            faceData.put("embeddings", embeddingList);

                            // Push data to Firebase
                            databaseRef.child(faceName).setValue(faceData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getApplicationContext(), "Face data stored in Firebase", Toast.LENGTH_SHORT).show();
                                        start = true;
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Failed to store face data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        start = true;
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error checking data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        start = true;
                    }
                });
    }

    private Task<String> getUserIdByEmail(String email) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        databaseRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String userId = childSnapshot.child("userId").getValue(String.class);
                                taskCompletionSource.setResult(userId); // Complete the task with the userId
                                return;
                            }
                        } else {
                            taskCompletionSource.setException(new Exception("No user found with the given email."));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        taskCompletionSource.setException(new Exception("Database error: " + databaseError.getMessage()));
                    }
                });

        return taskCompletionSource.getTask();
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




    /**
     * Handles the check-in process by calling the CheckIn API.
     */



    private void handleCheckIn(String userFaceIdFinaltxt, String name) {
        CheckInService checkInService = new CheckInService(this);

        Date currentDateTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            currentDateTime = java.sql.Timestamp.valueOf(formattedDateTime);
        } else {
            currentDateTime = new java.util.Date();
        }

            CheckInRequest request = new CheckInRequest();
            request.setUserId(userFaceIdFinaltxt); // Replace with the actual user ID
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            String formattedDate = iso8601Format.format(new Date());
            request.setTime(formattedDate);


            Call<CheckInResponse> call = checkInService.checkIn(request);
            call.enqueue(new Callback<CheckInResponse>() {
                @Override
                public void onResponse(Call<CheckInResponse> call, Response<CheckInResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(FaceCaptureActivity.this, "Check-in success!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Log the failure details
                        Log.e("FaceCaptureActivity", "Check-in failed with code: " + response.code() +
                                ", message: " + (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));

                       // Toast.makeText(FaceCaptureActivity.this, "Check-in failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CheckInResponse> call, Throwable t) {
                    // Show a Toast message for the error
                    Toast.makeText(FaceCaptureActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();

                    // Write the error to Logcat
                    Log.e("FaceCaptureActivity", "Network error occurred", t);
                }
            });



    }



    public void delayedWelcomeMessage(final String closestName, final String closestUserId) {
        // Retrieve the role of the user
        loadUserRole(closestUserId, new RoleNameCallback() {
            @Override
            public void onRoleNameRetrieved(String roleName) {
                // Check if the role is "customer"
                if ("customer".equalsIgnoreCase(roleName)) {
                    // If the role is "customer," check registration before triggering the welcome message
                    checkRegistrationVer2(closestUserId, new RegistrationCallback() {
                        @Override
                        public void onResult(boolean isRegistered) {
                            if (isRegistered) {
                                playWelcomeMessage(closestName);
                            } else {
                                playNonRegisteredMessage(closestName);
                            }
                        }
                    });
                } else {
                    // For non-customer roles, directly trigger the welcome message
                    playWelcomeMessage(closestName);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle errors in fetching the role
                Toast.makeText(FaceCaptureActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playNonRegisteredMessage(final String closestName) {
        if (!isWelcomeMessagePlaying) {
            isWelcomeMessagePlaying = true; // Set flag to prevent multiple calls

            String message = closestName + ", User is not Registered Membership or the Membership is Overdue.";
            playMessage(message);
        }
    }


    private void playMessage(String message) {
        if (tts != null && isWelcomeMessagePlaying) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messagePlayback");

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    // No action needed when the message starts
                }

                @Override
                public void onDone(String utteranceId) {
                    if ("messagePlayback".equals(utteranceId)) {
                        isWelcomeMessagePlaying = false; // Reset flag when playback finishes
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if ("messagePlayback".equals(utteranceId)) {
                        isWelcomeMessagePlaying = false; // Reset flag if there’s an error
                    }
                }
            });

            tts.speak(message, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    private void playWelcomeMessage(String closestName) {
        if (!isWelcomeMessagePlaying) {
            isWelcomeMessagePlaying = true; // Set flag to prevent multiple calls

            String welcomeMessage = "Welcome to SuperGym, " + closestName + "!";
            playMessage(welcomeMessage);
        }
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



    // Method to upload recognized face image to Firebase Storage
    private void storeRecognizedFaceImage(Bitmap bitmap, String userId) {
        // Convert Bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Get the current date in yyyy-MM-dd format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateFolder = sdf.format(new Date());  // This will give you the folder name like "2024-11-30"

        // Create a unique file name based on user ID and timestamp
        String fileName = (userId.equals("Unknown") ? "Unknown" : userId) + "_" + System.currentTimeMillis() + ".jpg";

        // Reference to the "/faces/yyyy-MM-dd" folder in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("faces/" + dateFolder + "/" + fileName);

        // Upload the image to Firebase Storage
        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getApplicationContext(), "Face image saved successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to save face image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void loadUserRole(String userId, RoleNameCallback callback) {
        // Reference to the user's details in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the roleId of the user
                    String roleId = dataSnapshot.child("roleId").getValue(String.class);

                    if (roleId != null) {
                        // Fetch role name based on roleId
                        DatabaseReference roleRef = FirebaseDatabase.getInstance().getReference("Roles").child(roleId.trim());
                        roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot roleSnapshot) {
                                if (roleSnapshot.exists()) {
                                    // Retrieve role name
                                    String roleName = roleSnapshot.child("RoleName").getValue(String.class);
                                    if (roleName != null) {
                                        callback.onRoleNameRetrieved(roleName.trim());
                                    } else {
                                        callback.onFailure("Role name not found.");
                                    }
                                } else {
                                    callback.onFailure("Role ID not found.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                callback.onFailure("Failed to retrieve role name: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        callback.onFailure("Role ID is null.");
                    }
                } else {
                    callback.onFailure("User not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure("Failed to load user details: " + databaseError.getMessage());
            }
        });
    }
    public interface RoleNameCallback {
        void onRoleNameRetrieved(String roleName);
        void onFailure(String errorMessage);
    }




}