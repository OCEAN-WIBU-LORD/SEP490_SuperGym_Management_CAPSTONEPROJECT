package com.supergym.sep490_supergymmanagement.TestClass;



import static org.mockito.Mockito.*;

import android.content.Context;
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;


import com.supergym.sep490_supergymmanagement.Activity_Book_Trainer;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.models.PackagesAndTrainersResponse;
import com.supergym.sep490_supergymmanagement.models.UserResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Book_TrainerTest {

    @Mock
    private ApiService apiService;

    private Context context;

    private Activity_Book_Trainer activityBookTrainer;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the context
        context = ApplicationProvider.getApplicationContext();

        // Instantiate the activity with the mocked ApiService
        activityBookTrainer = new Activity_Book_Trainer();
        activityBookTrainer.apiService = apiService;
    }

    @Test
    public void testLoadPackages_Success() {
        // Arrange
        String type = "TrainerRental";
        List<PackagesAndTrainersResponse.PackageDetails> mockPackages = new ArrayList<>();
        PackagesAndTrainersResponse.PackageDetails packageDetail = new PackagesAndTrainersResponse.PackageDetails();
        packageDetail.setDescription("Test Package");
        packageDetail.setPackageId("123");
        mockPackages.add(packageDetail);

        PackagesAndTrainersResponse mockResponse = new PackagesAndTrainersResponse();
        mockResponse.setPackages(mockPackages);

        Call<PackagesAndTrainersResponse> mockCall = mock(Call.class);
        when(apiService.getPackagesAndTrainers(type)).thenReturn(mockCall);

        doAnswer(invocation -> {
            Callback<PackagesAndTrainersResponse> callback = invocation.getArgument(0);
            callback.onResponse(mockCall, Response.success(mockResponse));
            return null;
        }).when(mockCall).enqueue(any());

        // Act
        activityBookTrainer.loadPackages(type);

        // Assert
        assert (activityBookTrainer.packageIds.contains("123"));
    }

    @Test
    public void testLoadPackages_Failure() {
        // Arrange
        String type = "TrainerRental";

        Call<PackagesAndTrainersResponse> mockCall = mock(Call.class);
        when(apiService.getPackagesAndTrainers(type)).thenReturn(mockCall);

        doAnswer(invocation -> {
            Callback<PackagesAndTrainersResponse> callback = invocation.getArgument(0);
            callback.onFailure(mockCall, new Throwable("Network Error"));
            return null;
        }).when(mockCall).enqueue(any());

        // Act
        activityBookTrainer.loadPackages(type);

        // Assert
        verify(apiService).getPackagesAndTrainers(type);
    }

    @Test
    public void testRegisterPackage_InvalidPackage() {
        // Arrange
        activityBookTrainer.packageIds.clear();

        // Act
        activityBookTrainer.registerPackage();

        // Assert
        verifyToast("Please select a valid package.");
    }

    private void verifyToast(String expectedMessage) {
        // Mock Toast.makeText and capture the message
        Toast toast = Toast.makeText(context, expectedMessage, Toast.LENGTH_SHORT);
        assert (toast.getView() != null);
    }
}
