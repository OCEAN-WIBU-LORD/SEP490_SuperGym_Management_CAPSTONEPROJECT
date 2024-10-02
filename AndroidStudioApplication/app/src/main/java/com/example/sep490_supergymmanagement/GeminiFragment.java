package com.example.sep490_supergymmanagement;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple Fragment subclass for Gemini chat functionality.
 */
public class GeminiFragment extends Fragment {

    private EditText messageField;
    private TextView chatHistory;
    private Button sendButton,testBtn, plusBtn, addButton, removeImBtn, minimizeBtn;
    public TextView theMessage;
    public  String theTestedMessage;
    public  TextView textView;
    private static final int IMAGE_PICK_CODE = 1000;  // Request code for picking image
    private ImageView imageView;
    private  Boolean imageIncluded = false;
    private Bitmap imageSent;
    private CardView cardView;

    // TODO: Replace with your actual API integration logic
    private String sendMessageToGemini(String message) {

        // 1. Replace with your actual URL endpoint for sending messages to Gemini AI
        String baseUrl = "https://GEMINI_AI_ENDPOINT/v1/text"; // Replace with actual endpoint

        // 2. Replace with your authentication logic (likely requires an API key or token)
        String apiKey = "AIzaSyB0AqWRypzhded3lxp6y--iGR1AMrcowO4"; // Replace with your actual API key

        HttpURLConnection connection = null;
        try {
            // 3. Create a URL object with the endpoint and query parameters (if applicable)
            URL url = new URL(baseUrl + "?api_key=" + apiKey);

            // 4. Create an HttpURLConnection object for making the POST request
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json"); // Assuming JSON format

            // 5. Prepare the request body with the user message in JSON format
            JSONObject requestBody = new JSONObject();
            requestBody.put("text", message); // Replace "text" with the actual field name in Gemini AI API
            String requestBodyString = requestBody.toString();

            // 6. Set the request body using an OutputStreamWriter
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(requestBodyString);
            writer.close();

            // 7. Check for successful connection and handle errors
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Error sending message to Gemini AI: " + connection.getResponseMessage());
            }

            // 8. Read the response from the API (assuming JSON format)
            StringBuilder responseStringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }
            reader.close();
            String responseString = responseStringBuilder.toString();

            // 9. Parse the JSON response to extract the AI response (replace with actual field names)
            JSONObject responseJson = new JSONObject(responseString);
            String aiResponse = responseJson.getString("response"); // Replace with actual response field name

            return aiResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with Gemini AI: " + e.getMessage();
        } finally {
            // Close the connection (optional)
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    public void buttonCallGeminiAPI(View view){
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDXLYx3ij16NZVjfGUi63q9OHIyU-YTeMk");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(theTestedMessage)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    textView.append("\nYou: " + theTestedMessage + "\nGemini: " + resultText +"\n");
                }

                @Override
                public void onFailure(Throwable t) {
                    textView.setText(t.toString());
                    t.printStackTrace();
                }
            }, getActivity().getMainExecutor());
        }
    }




    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
        imageIncluded =true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    imageView.setImageBitmap(bitmap);
                    imageIncluded = true;  // Set the flag here
                    imageSent = bitmap;
                    cardView.setVisibility(View.VISIBLE);
                    removeImBtn.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }


    public void buttonCallGeminiVisionAPI(View view){
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyDXLYx3ij16NZVjfGUi63q9OHIyU-YTeMk");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Bitmap bitmap = imageSent;
        Content content = new Content.Builder()
                .addImage(bitmap)
                .addText(theTestedMessage)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    textView.append("\nYou: " + theTestedMessage + "\nGemini: " + resultText +"\n");
                }

                @Override
                public void onFailure(Throwable t) {
                    textView.setText(t.toString());
                    t.printStackTrace();
                }
            }, getActivity().getMainExecutor());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gemini, container, false);
        textView = view.findViewById(R.id.txtResponseVip);
        textView.setMovementMethod(new ScrollingMovementMethod());
        addButton = view.findViewById(R.id.add_button);
        imageIncluded = false;
        plusBtn= view.findViewById(R.id.plusBtn);
        minimizeBtn = view.findViewById(R.id.abstractBtn);
        removeImBtn = view.findViewById(R.id.close_btn);
        cardView = view.findViewById(R.id.my_card_view);
        imageView = view.findViewById(R.id.image_view);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.VISIBLE);
                plusBtn.setVisibility(View.INVISIBLE);
                removeImBtn.setVisibility(View.VISIBLE);
            }
        });
        minimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.INVISIBLE);
                plusBtn.setVisibility(View.VISIBLE);
                removeImBtn.setVisibility(View.INVISIBLE);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.INVISIBLE);
                plusBtn.setVisibility(View.VISIBLE);
                removeImBtn.setVisibility(View.INVISIBLE);
            }
        });
        removeImBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                cardView.setVisibility(View.INVISIBLE);
                plusBtn.setVisibility(View.INVISIBLE);
                removeImBtn.setVisibility(View.INVISIBLE);
            }
        });


        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageIncluded = true;
                    pickImage();

                }
            });
        }
        EditText messageField = view.findViewById(R.id.txtMessage); // Store the reference here
//        chatHistory = view.findViewById(R.id.textView22);
//        sendButton = view.findViewById(R.id.button3);
        testBtn = view.findViewById(R.id.btnTest);

        // ... rest of the code
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userMessage = messageField.getText().toString();
                theTestedMessage = userMessage;

                if (imageIncluded) {
                    buttonCallGeminiVisionAPI(view);
                } else {
                    buttonCallGeminiAPI(view);
                }

                messageField.setText("");
            }
        });

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String userMessage = messageField.getText().toString();
//
//                // Call the function to send message to Gemini AI (replace with actual API call)
//                String response = sendMessageToGemini(userMessage);
//
//                // Update the chat history with user message and response
//                chatHistory.append("\nYou: " + userMessage + "\nGemini: " + response);
//
//                // Clear the message field for the next input
//                messageField.setText("");
//            }
//        });

        return view;
    }
}
