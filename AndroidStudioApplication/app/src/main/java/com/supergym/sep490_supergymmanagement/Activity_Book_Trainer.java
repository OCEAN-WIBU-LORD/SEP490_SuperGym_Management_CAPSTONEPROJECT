package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.supergym.sep490_supergymmanagement.adapters.UserAdapter;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.PackagesAndTrainersResponse;
import com.supergym.sep490_supergymmanagement.models.QrCodeRentalResponse;
import com.supergym.sep490_supergymmanagement.models.QrCodeBoxingResponse;
import com.supergym.sep490_supergymmanagement.models.RegisterPackageRequest;
import com.supergym.sep490_supergymmanagement.models.SearchUser;
import com.supergym.sep490_supergymmanagement.models.TimeSlot;
import com.supergym.sep490_supergymmanagement.models.UserResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Book_Trainer extends AppCompatActivity {
    private Spinner packageSpinner, trainerSpinner;
    private RadioGroup trainerTypeRadioGroup, optionRadioGroup;
    private GridLayout dayGridLayout;
    private CardView returnBtn;
    private SeekBar sessionTimeSeekBar;
    private TextView selectedTimeSlotText, trainerNameTextView, trainerBioTextView, qrCodeImageView;
    private EditText extraUsersEditText, sessionCountEditText;
    private SearchView userSearchView;
    private RecyclerView userRecyclerView;
    private List<String> extraEmails = new ArrayList<>();
    private List<SearchUser> userList;
    private UserAdapter userAdapter;
    private EditText extraUserEditText;
    public ApiService apiService;
    public List<String> packageIds = new ArrayList<>();
    private List<String> trainerIds = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private ImageView dropdownBtnPackage, dropdownBtn;
    CheckBox monday  , tuesday, saturday, wednesday, friday, thursday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_trainer);

        // Khởi tạo ApiService

        apiService = RetrofitClient.getApiService(this);

        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simulate the back button press
                onBackPressed();
            }
        });
        // Khởi tạo các thành phần giao diện
        initViews();

        // Cài đặt các sự kiện lắng nghe
        setupListeners();

        // Cài đặt các chức năng radio button và checkbox
        functionedRadioBtn();

        // Tải danh sách TimeSlots
        loadTimeSlots();

        // Thiết lập RecyclerView và logic tìm kiếm email
        setupSearchEmailLogic();

        // Tải dữ liệu packages mặc định cho loại ban đầu
        trainerTypeRadioGroup.check(R.id.radioGym); // Mặc định là Gym
        loadPackagesWithType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearForm();
    }


    private void clearForm() {
        // Clear Spinner selections
        packageSpinner.setSelection(0);
        trainerSpinner.setSelection(0);

        // Clear RadioGroups
        trainerTypeRadioGroup.clearCheck();
        optionRadioGroup.clearCheck();

        // Clear SeekBar
        sessionTimeSeekBar.setProgress(0);

        // Clear TextViews
        selectedTimeSlotText.setText("");
        trainerNameTextView.setText("");
        trainerBioTextView.setText("");

        // Clear EditTexts
        extraUsersEditText.setText("");
        sessionCountEditText.setText("");

        // Clear SearchView if it is not null
        if (userSearchView != null) {
            userSearchView.setQuery("", false);
        }

        // Clear RecyclerView Adapter data
        if (userAdapter != null) {
            userAdapter.clear();
        }

        // Clear CheckBoxes
        monday.setChecked(false);
        tuesday.setChecked(false);
        wednesday.setChecked(false);
        thursday.setChecked(false);
        friday.setChecked(false);
        saturday.setChecked(false);

        // Reset the CheckBoxes to their disabled state
        resetCheckboxes();
    }


    private void initViews() {
        packageSpinner = findViewById(R.id.trainerPackages);
        trainerSpinner = findViewById(R.id.trainerSpinner);
        trainerTypeRadioGroup = findViewById(R.id.trainerTypeRadioGroup);
        optionRadioGroup = findViewById(R.id.optionRadioGroup);
        dayGridLayout = findViewById(R.id.dayGridLayout);
        sessionTimeSeekBar = findViewById(R.id.sessionTimeSeekBar);
        selectedTimeSlotText = findViewById(R.id.selectedTimeSlotText);
        trainerNameTextView = findViewById(R.id.trainerNameTextView);
        trainerBioTextView = findViewById(R.id.bioTextView);
        extraUsersEditText = findViewById(R.id.extraUser);
        sessionCountEditText = findViewById(R.id.sessionCountEditText);
        dropdownBtnPackage= findViewById(R.id.dropdownBtnPackage);
        dropdownBtn = findViewById(R.id.dropdownBtn);
        // Make the ImageView trigger the Spinner click
        dropdownBtnPackage.setOnClickListener(v -> packageSpinner.performClick());
        dropdownBtn.setOnClickListener(v -> trainerSpinner.performClick());
    }

    private void setupListeners() {
        // Lắng nghe thay đổi loại gói tập
        trainerTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String type;
            if (checkedId == R.id.radioGym) {
                type = "TrainerRental";
            } else if (checkedId == R.id.radioBoxing) {
                type = "Boxing";
            } else {
                type = "TrainerRental"; // Mặc định
            }
            loadPackages(type);
        });

        // Lắng nghe sự thay đổi của packageSpinner
        packageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (packageIds.isEmpty()) {
                    Toast.makeText(Activity_Book_Trainer.this, "No packages available.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (position >= 0 && position < packageIds.size()) {
                    String selectedPackageId = packageIds.get(position);
                    loadTrainers(selectedPackageId);
                } else {
                    Toast.makeText(Activity_Book_Trainer.this, "Invalid package selected.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lắng nghe sự thay đổi của trainerSpinner
        trainerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (trainerIds.isEmpty()) {
                    Toast.makeText(Activity_Book_Trainer.this, "No trainers available.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (position >= 0 && position < trainerIds.size()) {
                    String selectedTrainerId = trainerIds.get(position);
                    loadTrainerDetails(selectedTrainerId);
                } else {
                    Toast.makeText(Activity_Book_Trainer.this, "Invalid trainer selected.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lắng nghe sự thay đổi của sessionTimeSeekBar
        sessionTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (timeSlots.isEmpty()) {
                    Toast.makeText(Activity_Book_Trainer.this, "No time slots available.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (progress >= 0 && progress < timeSlots.size()) {
                    TimeSlot selectedTimeSlot = timeSlots.get(progress);
                    selectedTimeSlotText.setText(selectedTimeSlot.getTime()); // Hiển thị thời gian
                } else {
                    selectedTimeSlotText.setText("Invalid time slot.");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Lắng nghe sự kiện nút submit
        findViewById(R.id.submit_button).setOnClickListener(v -> registerPackage());
    }

    private void functionedRadioBtn() {
        monday = findViewById(R.id.checkboxMonday);
        tuesday = findViewById(R.id.checkboxTuesday);
        wednesday = findViewById(R.id.checkboxWednesday);
        thursday = findViewById(R.id.checkboxThursday);
        friday = findViewById(R.id.checkboxFriday);
        saturday = findViewById(R.id.checkboxSaturday);
        sessionCountEditText = findViewById(R.id.sessionCountEditText);  // Đảm bảo bạn đã khai báo sessionCountEditText

        optionRadioGroup.setVisibility(View.GONE);

        trainerTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            resetCheckboxes(); // Reset tất cả checkbox

            if (checkedId == R.id.radioBoxing) {
                optionRadioGroup.setVisibility(View.VISIBLE); // Hiển thị các tùy chọn
                enableAllCheckboxes(); // Bật tất cả checkbox nhưng không cho phép thay đổi trạng thái
                sessionCountEditText.setVisibility(View.GONE); // Ẩn EditText khi chọn boxing
            } else if (checkedId == R.id.radioGym) {
                optionRadioGroup.setVisibility(View.GONE); // Ẩn tùy chọn
                selectAndDisableAll(monday, tuesday, wednesday, thursday, friday); // Tắt và giữ các checkbox đã chọn
                sessionCountEditText.setVisibility(View.VISIBLE); // Hiển thị EditText khi chọn gym
            } else {
                optionRadioGroup.setVisibility(View.GONE);
                enableAllCheckboxes(); // Bật tất cả checkbox nhưng không cho phép thay đổi trạng thái
                sessionCountEditText.setVisibility(View.VISIBLE); // Hiển thị EditText cho loại khác
            }

            // Tải dữ liệu packages cho loại được chọn
            loadPackagesWithType();
        });

        optionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            resetCheckboxes();

            if (checkedId == R.id.option1) {
                enableCombination(monday, wednesday, friday);
            } else if (checkedId == R.id.option2) {
                enableCombination(tuesday, thursday, saturday);
            }
        });
    }


    // Hàm bật tất cả các CheckBox nhưng không cho phép thay đổi trạng thái
    private void enableAllCheckboxes() {
        monday.setEnabled(false);
        tuesday.setEnabled(false);
        wednesday.setEnabled(false);
        thursday.setEnabled(false);
        friday.setEnabled(false);
        saturday.setEnabled(false);

        // Đảm bảo các checkbox không thể nhấn được nữa
        monday.setClickable(false);
        tuesday.setClickable(false);
        wednesday.setClickable(false);
        thursday.setClickable(false);
        friday.setClickable(false);
        saturday.setClickable(false);
    }

    // Hàm tắt các checkbox và bỏ chọn tất cả
    private void resetCheckboxes() {
        monday.setChecked(false);
        tuesday.setChecked(false);
        wednesday.setChecked(false);
        thursday.setChecked(false);
        friday.setChecked(false);
        saturday.setChecked(false);

        monday.setEnabled(false);
        tuesday.setEnabled(false);
        wednesday.setEnabled(false);
        thursday.setEnabled(false);
        friday.setEnabled(false);
        saturday.setEnabled(false);

        // Đảm bảo các checkbox không thể nhấn được nữa
        monday.setClickable(false);
        tuesday.setClickable(false);
        wednesday.setClickable(false);
        thursday.setClickable(false);
        friday.setClickable(false);
        saturday.setClickable(false);
    }

    // Hàm chỉ chọn và vô hiệu hóa các checkbox nhất định
    private void selectAndDisableAll(CheckBox... checkBoxes) {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(true);  // Đặt các checkbox đã chọn
            checkBox.setEnabled(false); // Tắt tương tác với checkbox
            checkBox.setClickable(false); // Đảm bảo không thể nhấn vào checkbox
        }
    }

    // Hàm cho phép một số checkbox có thể chọn và vô hiệu hóa các checkbox còn lại
    private void enableCombination(CheckBox... combination) {
        for (CheckBox checkBox : combination) {
            checkBox.setChecked(true);  // Đặt checkbox đã chọn
            checkBox.setEnabled(false); // Tắt tương tác với checkbox
            checkBox.setClickable(false); // Đảm bảo không thể nhấn vào checkbox
        }
    }



    private void loadPackagesWithType() {
        // Lấy type từ RadioGroup
        String type = getSelectedPackageType();

        // Gọi API với type được xác định
        loadPackages(type);
    }

    private void updateTrainerSpinner(List<UserResponse> trainers) {
        ArrayAdapter<UserResponse> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainerSpinner.setAdapter(adapter);
    }

    public void loadPackages(String type) {
        apiService.getPackagesAndTrainers(type).enqueue(new Callback<PackagesAndTrainersResponse>() {
            @Override
            public void onResponse(Call<PackagesAndTrainersResponse> call, Response<PackagesAndTrainersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PackagesAndTrainersResponse packagesAndTrainers = response.body();

                    // Xử lý danh sách gói tập
                    List<PackagesAndTrainersResponse.PackageDetails> packages = packagesAndTrainers.getPackages();
                    if (packages == null || packages.isEmpty()) {
                        //Toast.makeText(Activity_Book_Trainer.this, "No packages found.", Toast.LENGTH_SHORT).show();
                        packageIds.clear(); // Xóa danh sách cũ
                        updateSpinner(packageSpinner, new ArrayList<>()); // Làm rỗng Spinner
                        return;
                    }

                    // Danh sách UserResponse cho Spinner
                    List<UserResponse> packageResponses = new ArrayList<>();
                    packageIds.clear(); // Xóa danh sách cũ để đảm bảo không bị trùng lặp

                    for (PackagesAndTrainersResponse.PackageDetails pkg : packages) {
                        String description = pkg.getDescription() != null ? pkg.getDescription() : "Unnamed Package";

                        // Tạo UserResponse từ PackageDetails
                        UserResponse packageResponse = new UserResponse();
                        packageResponse.setName(description);
                        packageResponse.setUserId(pkg.getPackageId());
                        packageResponses.add(packageResponse);

                        // Thêm vào danh sách ID
                        packageIds.add(pkg.getPackageId());
                    }

                    // Cập nhật Spinner
                    updateSpinner(packageSpinner, packageResponses);

                } else {
                    // Trường hợp lỗi HTTP hoặc không có body
                    String errorMessage = "Failed to load packages: ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (Exception e) {
                            errorMessage += "Error parsing error body.";
                        }
                    } else {
                        errorMessage += "Response is empty.";
                    }

                    Toast.makeText(Activity_Book_Trainer.this, errorMessage, Toast.LENGTH_SHORT).show();
                    packageIds.clear(); // Xóa danh sách cũ
                    updateSpinner(packageSpinner, new ArrayList<>()); // Làm rỗng Spinner
                }
            }

            @Override
            public void onFailure(Call<PackagesAndTrainersResponse> call, Throwable t) {
                Toast.makeText(Activity_Book_Trainer.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                packageIds.clear(); // Xóa danh sách cũ
                updateSpinner(packageSpinner, new ArrayList<>()); // Làm rỗng Spinner
            }
        });
    }


    private void loadTrainers(String packageId) {
        // Xác định loại package (boxingOptionId hoặc rentalOptionId) từ RadioGroup
        String boxingOptionId = null;
        String rentalOptionId = null;

        if (trainerTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioBoxing) {
            boxingOptionId = packageId; // Nếu là Boxing, truyền giá trị vào boxingOptionId
        } else {
            rentalOptionId = packageId; // Nếu là Rental, truyền giá trị vào rentalOptionId
        }

        // Gọi API với boxingOptionId hoặc rentalOptionId
        apiService.getTrainersByOption(boxingOptionId, rentalOptionId).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy danh sách huấn luyện viên từ phản hồi
                    List<UserResponse> trainers = response.body();
                    trainerIds.clear();

                    // Xử lý danh sách huấn luyện viên
                    for (UserResponse trainer : trainers) {
                        // Thêm Trainer vào danh sách ID
                        trainerIds.add(trainer.getUserId());

                        // Kiểm tra điều kiện của gói
                        if (!trainer.isMonthlyPackage()) {
                            enableDaySelection(); // Cho phép chọn ngày
//                            Toast.makeText(Activity_Book_Trainer.this,
//                                    "Sessions: " + trainer.getMinSessions() + " - " + trainer.getMaxSessions(),
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            selectAndDisableAll(monday, tuesday, wednesday, thursday, friday, saturday); // Check tất cả ngày
                        }

                        if (trainer.getMemberCount() > 1) {
                            enableEmailSearch(trainer.getMemberCount()); // Cho phép thêm số lượng email
                        }
                    }

                    // Cập nhật Spinner với danh sách trainers
                    updateSpinner(trainerSpinner, trainers);
                } else {
                    // Trường hợp không có trainer hoặc lỗi khác
                    String errorMessage = "Failed to load trainers. ";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMessage += "Error parsing response.";
                    }
                    Toast.makeText(Activity_Book_Trainer.this, errorMessage, Toast.LENGTH_LONG).show();

                    // Làm rỗng Spinner
                    trainerIds.clear();
                    updateSpinner(trainerSpinner, new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                // Xử lý lỗi khi không kết nối được API
                Toast.makeText(Activity_Book_Trainer.this, "Failed to load trainers: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();

                // Làm rỗng Spinner
                trainerIds.clear();
                updateSpinner(trainerSpinner, new ArrayList<>());
            }
        });
    }


    // Helper để bật tính năng chọn ngày
    private void enableDaySelection() {
        monday.setEnabled(true);
        tuesday.setEnabled(true);
        wednesday.setEnabled(true);
        thursday.setEnabled(true);
        friday.setEnabled(true);
        saturday.setEnabled(true);
    }

    // Helper để cho phép thêm email theo `memberCount`
    private void enableEmailSearch(int memberCount) {
        // Thông báo số lượng email có thể thêm
        Toast.makeText(this, "You can add up to " + memberCount + " additional users.", Toast.LENGTH_SHORT).show();

        // Giới hạn số lượng email trong EditText
        extraUserEditText = findViewById(R.id.extraUser);
        extraUserEditText.setHint("Enter up to " + memberCount + " email(s), separated by commas");

        // Khởi tạo logic tìm kiếm email
        setupSearchEmailLogic();
    }



    private void loadTrainerDetails(String trainerId) {
        // Kiểm tra `trainerId` có tồn tại trong danh sách không
        if (!trainerIds.contains(trainerId)) {
            Toast.makeText(this, "Trainer not found in the list.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xác định loại gói (Boxing hoặc Rental)
        String selectedPackageType = getSelectedPackageType();
        String boxingOptionId = null;
        String rentalOptionId = null;

        if ("Boxing".equals(selectedPackageType)) {
            // Truyền boxingOptionId nếu gói là Boxing
            boxingOptionId = packageIds.get(packageSpinner.getSelectedItemPosition());
        } else {
            // Truyền rentalOptionId nếu gói là Rental
            rentalOptionId = packageIds.get(packageSpinner.getSelectedItemPosition());
        }

        // Gọi API với boxingOptionId hoặc rentalOptionId
        apiService.getTrainersByOption(boxingOptionId, rentalOptionId).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserResponse> trainers = response.body();

                    // Tìm trainer theo `trainerId`
                    for (UserResponse trainer : trainers) {
                        if (trainerId.equals(trainer.getUserId())) {
                            trainerNameTextView.setText("Trainer Name: " + (trainer.getName() != null ? trainer.getName() : "Unknown"));
                            trainerBioTextView.setText("Specialization: " + (trainer.getSpecialization() != null ? trainer.getSpecialization() : "No specialization available"));

                            // Kiểm tra `memberCount` để hiển thị hoặc ẩn giao diện
                            if (trainer.getMemberCount() == 1) {
                                handleMemberCountVisibility(1); // Ẩn giao diện thêm người dùng
                            } else if (trainer.getMemberCount() > 1) {
                                handleMemberCountVisibility(trainer.getMemberCount()); // Hiển thị giao diện tìm kiếm email
                            }
                            return;
                        }
                    }
                    Toast.makeText(Activity_Book_Trainer.this, "Trainer details not found.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = "Failed to load trainer details.";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " " + response.errorBody().string();
                        } catch (Exception e) {
                            errorMessage += " Error parsing error body.";
                        }
                    }
                    Log.e("TrainerDetailsAPI", errorMessage);
                    Toast.makeText(Activity_Book_Trainer.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                Log.e("TrainerDetailsAPI", "API Failure: " + t.getMessage(), t);
                Toast.makeText(Activity_Book_Trainer.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleMemberCountVisibility(int memberCount) {
        // Tìm các thành phần giao diện cần ẩn/hiện
        TextView addExtraUsersText = findViewById(R.id.addExtraUsersText);
        LinearLayout extraUsersLayout = findViewById(R.id.extraUsersLayout);

        if (memberCount == 1) {
            // Ẩn giao diện thêm người dùng nếu memberCount là 1
            addExtraUsersText.setVisibility(View.GONE);
            extraUsersLayout.setVisibility(View.GONE);
        } else {
            // Hiện giao diện nếu memberCount lớn hơn 1
            addExtraUsersText.setVisibility(View.VISIBLE);
            extraUsersLayout.setVisibility(View.VISIBLE);
            enableEmailSearch(memberCount); // Kích hoạt logic tìm kiếm email
        }
    }


    private void loadTimeSlots() {
        apiService.getTimeSlots().enqueue(new Callback<List<TimeSlot>>() {
            @Override
            public void onResponse(Call<List<TimeSlot>> call, Response<List<TimeSlot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    timeSlots.clear();
                    timeSlots.addAll(response.body());

                    if (!timeSlots.isEmpty()) {
                        sessionTimeSeekBar.setMax(timeSlots.size() - 1);
                        selectedTimeSlotText.setText(timeSlots.get(0).getTime()); // Hiển thị giá trị đầu tiên
                    } else {
                        Toast.makeText(Activity_Book_Trainer.this, "No time slots available.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity_Book_Trainer.this, "Failed to load time slots.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TimeSlot>> call, Throwable t) {
                Toast.makeText(Activity_Book_Trainer.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchEmailLogic() {
        // Khởi tạo RecyclerView
        userRecyclerView = findViewById(R.id.userRecyclerView);
        if (userRecyclerView == null) {
            throw new NullPointerException("RecyclerView is not defined in the layout or ID does not match.");
        }

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Cài đặt LayoutManager

        // Khởi tạo danh sách và Adapter
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, user -> {
            addEmailToExtraUser(user.getEmail());
        });

        userRecyclerView.setAdapter(userAdapter); // Liên kết Adapter với RecyclerView

        // Khởi tạo EditText và SearchView
        extraUserEditText = findViewById(R.id.extraUser);
        userSearchView = findViewById(R.id.userSearchView);

        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    Toast.makeText(Activity_Book_Trainer.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                } else {
                    searchUsers(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    searchUsers(newText);
                } else {
                    // Nếu xóa hết text trong SearchView, xóa danh sách kết quả
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }


    private void addEmailToExtraUser(String email) {
        String existingEmails = extraUserEditText.getText().toString().trim();

        if (!existingEmails.isEmpty()) {
            existingEmails += "," + email;
        } else {
            existingEmails = email;
        }

        extraUserEditText.setText(existingEmails);

        // Reset SearchView sau khi thêm email
        userSearchView.setQuery("", false);
        userSearchView.clearFocus();

        // Xóa danh sách kết quả tìm kiếm
        userList.clear();
        userAdapter.notifyDataSetChanged();
    }


    // Tìm kiếm người dùng qua API
    private void searchUsers(String email) {
        apiService.getUserByEmail(email).enqueue(new Callback<SearchUser>() {
            @Override
            public void onResponse(Call<SearchUser> call, Response<SearchUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SearchUser user = response.body();

                    // Kiểm tra xem email đã tồn tại trong EditText chưa
                    String existingEmails = extraUserEditText.getText().toString().trim();
                    List<String> emailList = new ArrayList<>(Arrays.asList(existingEmails.split(",\\s*")));

                    if (emailList.contains(user.getEmail())) {
                        Toast.makeText(Activity_Book_Trainer.this, "Email đã được thêm.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Cập nhật danh sách người dùng hiển thị
                    userList.clear();
                    userList.add(user);
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Activity_Book_Trainer.this, "No user found with this email.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchUser> call, Throwable t) {
                Toast.makeText(Activity_Book_Trainer.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void updateSpinner(Spinner spinner, List<UserResponse> items) {
        ArrayAdapter<UserResponse> adapter = new ArrayAdapter<UserResponse>(this, android.R.layout.simple_spinner_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(items.get(position).getName()); // Hiển thị tên của UserResponse
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(items.get(position).getName()); // Hiển thị tên trong dropdown
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }



    public void registerPackage() {
        Button submitButton = findViewById(R.id.submit_button);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Ẩn nút submit và hiển thị ProgressBar
        submitButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (packageIds.isEmpty() || packageSpinner.getSelectedItemPosition() >= packageIds.size()) {
            Toast.makeText(this, "Please select a valid package.", Toast.LENGTH_SHORT).show();
            submitButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (trainerIds.isEmpty() || trainerSpinner.getSelectedItemPosition() >= trainerIds.size()) {
            Toast.makeText(this, "Please select a valid trainer.", Toast.LENGTH_SHORT).show();
            submitButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (timeSlots.isEmpty() || sessionTimeSeekBar.getProgress() >= timeSlots.size()) {
            Toast.makeText(this, "Please select a valid time slot.", Toast.LENGTH_SHORT).show();
            submitButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "User not logged in or email not found. Please login again.", Toast.LENGTH_SHORT).show();
            submitButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        String userEmail = currentUser.getEmail(); // Current user's email
        RegisterPackageRequest request = new RegisterPackageRequest();

        String extraUsers = extraUsersEditText.getText().toString();
        List<String> emails = new ArrayList<>();
        emails.add(userEmail); // Add current user's email
        if (!extraUsers.isEmpty()) {
            emails.addAll(Arrays.asList(extraUsers.split(","))); // Add additional emails
        }
        request.setEmails(emails);

        String selectedPackageType = getSelectedPackageType();
        UserResponse selectedTrainer = (UserResponse) trainerSpinner.getSelectedItem();
        if (selectedTrainer == null) {
            Toast.makeText(this, "Failed to determine trainer. Please try again.", Toast.LENGTH_SHORT).show();
            submitButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        String selectedPackageId = null;

        if ("Boxing".equals(selectedPackageType)) {
            selectedPackageId = selectedTrainer.getBoxingMembershipPlanId();
            if (selectedPackageId == null) {
                Toast.makeText(this, "Invalid Boxing Membership Plan ID.", Toast.LENGTH_SHORT).show();
                submitButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            request.setBoxingMembershipPlanId(selectedPackageId);
            request.setQrPayment(true); // Always QR payment
            request.setDuration(1);

            String tsid = timeSlots.get(sessionTimeSeekBar.getProgress()).getTimeSlotId();
            if (tsid == null) {
                Toast.makeText(this, "Invalid Time Slot ID.", Toast.LENGTH_SHORT).show();
                submitButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            request.setSelectedTimeSlot(tsid);

            boolean isMonWedFri = optionRadioGroup.getCheckedRadioButtonId() == R.id.option1;
            request.setMonWedFri(isMonWedFri);

            apiService.createBoxingRegistration(request).enqueue(new Callback<List<QrCodeBoxingResponse.QrItem>>() {
                @Override
                public void onResponse(Call<List<QrCodeBoxingResponse.QrItem>> call, Response<List<QrCodeBoxingResponse.QrItem>> response) {
                    handleBoxingApiResponse(response, submitButton, progressBar);
                }

                @Override
                public void onFailure(Call<List<QrCodeBoxingResponse.QrItem>> call, Throwable t) {
                    handleApiFailure(t, submitButton, progressBar);
                }
            });
        } else if ("TrainerRental".equals(selectedPackageType)) {
            selectedPackageId = selectedTrainer.getTrainerRentalPlanId();

            if (selectedPackageId == null) {
                Toast.makeText(this, "Invalid Trainer Rental Plan ID.", Toast.LENGTH_SHORT).show();
                submitButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }

            String sessionCountInput = sessionCountEditText.getText().toString().trim();
            if (sessionCountInput.isEmpty()) {
                Toast.makeText(this, "Please input number of sessions", Toast.LENGTH_SHORT).show();
                submitButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }

            try {
                int sessionCount = Integer.parseInt(sessionCountInput);
                request.setDuration(sessionCount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number of sessions", Toast.LENGTH_SHORT).show();
                submitButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }

            request.setTrainerRentalPlanId(selectedPackageId);
            request.setQrPayment(true); // Always QR payment

            String tsid = timeSlots.get(sessionTimeSeekBar.getProgress()).getTimeSlotId();
            if (tsid == null) {
                Toast.makeText(this, "Invalid Time Slot ID.", Toast.LENGTH_SHORT).show();
                submitButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            request.setSelectedTimeSlot(tsid);

            apiService.createTrainerRentalRegistration(request).enqueue(new Callback<List<QrCodeRentalResponse.QrItem>>() {
                @Override
                public void onResponse(Call<List<QrCodeRentalResponse.QrItem>> call, Response<List<QrCodeRentalResponse.QrItem>> response) {
                    handleRentalApiResponse(response, submitButton, progressBar);
                }

                @Override
                public void onFailure(Call<List<QrCodeRentalResponse.QrItem>> call, Throwable t) {
                    handleApiFailure(t, submitButton, progressBar);
                }
            });
        }
    }

    private void handleBoxingApiResponse(Response<List<QrCodeBoxingResponse.QrItem>> response, Button submitButton, ProgressBar progressBar) {
        // Ẩn ProgressBar và hiển thị lại submitButton
        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        if (response.isSuccessful() && response.body() != null) {
            List<QrCodeBoxingResponse.QrItem> qrItems = response.body();
            if (!qrItems.isEmpty()) {
                String qrDataUrl = qrItems.get(0).getQrDataUrl();
                Log.d("API Response", "QR Data URL: " + qrDataUrl);

                if (qrDataUrl != null && !qrDataUrl.isEmpty()) {
                    // Remove Base64 prefix if present
                    if (qrDataUrl.startsWith("data:image/png;base64,")) {
                        qrDataUrl = qrDataUrl.substring("data:image/png;base64,".length());
                    }

                    Intent intent = new Intent(Activity_Book_Trainer.this, QrCodeDisplayActivity.class);
                    intent.putExtra("qr_base64", qrDataUrl); // Pass Base64 string
                    startActivity(intent);
                } else {
                    Log.e("QR Code Error", "QR Data URL is null or empty.");
                    Toast.makeText(this, "QR Code data is missing.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            logErrorResponse(response);
        }
    }

    private void handleRentalApiResponse(Response<List<QrCodeRentalResponse.QrItem>> response, Button submitButton, ProgressBar progressBar) {
        // Ẩn ProgressBar và hiển thị lại submitButton
        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        if (response.isSuccessful() && response.body() != null) {
            List<QrCodeRentalResponse.QrItem> qrItems = response.body();
            if (!qrItems.isEmpty()) {
                String qrDataUrl = qrItems.get(0).getQrDataUrl();
                Log.d("API Response", "QR Data URL: " + qrDataUrl);

                if (qrDataUrl != null && !qrDataUrl.isEmpty()) {
                    // Remove Base64 prefix if present
                    if (qrDataUrl.startsWith("data:image/png;base64,")) {
                        qrDataUrl = qrDataUrl.substring("data:image/png;base64,".length());
                    }

                    Intent intent = new Intent(Activity_Book_Trainer.this, QrCodeDisplayActivity.class);
                    intent.putExtra("qr_base64", qrDataUrl); // Pass Base64 string
                    startActivity(intent);
                } else {
                    Log.e("QR Code Error", "QR Data URL is null or empty.");
                    Toast.makeText(this, "QR Code data is missing.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            logErrorResponse(response);
        }
    }

    private void handleApiFailure(Throwable t, Button submitButton, ProgressBar progressBar) {
        // Ẩn ProgressBar và hiển thị lại submitButton
        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Failed to connect to server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e("RegisterPackageAPI", "Failure: ", t);
    }

    private void logErrorResponse(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorResponse = response.errorBody().string();
                Log.e("API Error", "Error Response: " + errorResponse);

                // Phân tích lỗi chi tiết dựa trên response
                if (response.code() == 400) {
                    // Lỗi Bad Request
                    if (errorResponse.contains("Number of sessions is not valid")) {
                        Toast.makeText(this, "Number of sessions is not valid to register this membership.", Toast.LENGTH_LONG).show();
                    } else if (errorResponse.contains("Trainer is fully booked")) {
                        Toast.makeText(this, "Trainer is fully booked for one or more slot dates.", Toast.LENGTH_LONG).show();
                    } else if (errorResponse.contains("One or more users already have active Trainer Rental Registrations.")) {
                        Toast.makeText(this, "One or more users already have active Trainer Rental Registrations.", Toast.LENGTH_LONG).show();
                    }else if (errorResponse.contains("One or more users already have active Boxing Registrations.")) {
                        Toast.makeText(this, "One or more users already have active Boxing Registrations.", Toast.LENGTH_LONG).show();
                    }else if(errorResponse.contains("Do not enter duplicate emails")){
                        Toast.makeText(this, "Do not enter duplicate emails.", Toast.LENGTH_LONG).show();
                    } else if (errorResponse.contains("One or more users do not have the required 'customer' role.")) {
                        Toast.makeText(this, "One or more users do not have the required 'customer' role.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Bad Request: " + errorResponse, Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 500) {
                    // Lỗi Internal Server Error
                    Toast.makeText(this, "Internal Server Error: " + errorResponse, Toast.LENGTH_LONG).show();
                } else {
                    // Lỗi khác
                    Toast.makeText(this, "An error occurred: " + errorResponse, Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("API Error", "Unknown error occurred.");
                Toast.makeText(this, "Unknown error occurred.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("API Error", "Error reading error response: " + e.getMessage());
            Toast.makeText(this, "Error reading error response.", Toast.LENGTH_SHORT).show();
        }
    }
    private String getSelectedPackageType() {
        int checkedId = trainerTypeRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioGym) {
            return "TrainerRental"; // Mặc định "Gym" dùng API type TrainerRental
        } else if (checkedId == R.id.radioBoxing) {
            return "Boxing";
        } else {
            return "TrainerRental"; // Giá trị mặc định
        }
    }

}
