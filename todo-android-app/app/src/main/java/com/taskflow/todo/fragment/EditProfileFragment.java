package com.taskflow.todo.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.taskflow.todo.R;
import com.taskflow.todo.data.db.TodoDatabase;
import com.taskflow.todo.data.entity.User;
import com.taskflow.todo.util.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class EditProfileFragment extends Fragment {

    private EditText etFirstName, etLastName, etDateOfBirth;
    private MaterialButton btnGenderMale, btnGenderFemale, btnSaveProfile;
    private MaterialCardView cardAvatarMale, cardAvatarFemale;

    private String selectedAvatar = "male";
    private String selectedGender = "";

    private TodoDatabase db;
    private SessionManager sessionManager;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        db = TodoDatabase.getDatabase(requireContext());
        sessionManager = new SessionManager(requireContext());

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        btnGenderMale = view.findViewById(R.id.btnGenderMale);
        btnGenderFemale = view.findViewById(R.id.btnGenderFemale);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        cardAvatarMale = view.findViewById(R.id.cardAvatarMale);
        cardAvatarFemale = view.findViewById(R.id.cardAvatarFemale);

        view.findViewById(R.id.btnBackEdit).setOnClickListener(v -> goBack());

        cardAvatarMale.setOnClickListener(v -> selectAvatar("male"));
        cardAvatarFemale.setOnClickListener(v -> selectAvatar("female"));

        btnGenderMale.setOnClickListener(v -> selectGender("Male"));
        btnGenderFemale.setOnClickListener(v -> selectGender("Female"));

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        loadExistingData();

        return view;
    }

    private void selectAvatar(String type) {
        selectedAvatar = type;
        int activeColor = ContextCompat.getColor(requireContext(), R.color.primaryColor);
        int inactiveColor = ContextCompat.getColor(requireContext(), R.color.inputBorder);
        cardAvatarMale.setStrokeColor("male".equals(type) ? activeColor : inactiveColor);
        cardAvatarFemale.setStrokeColor("female".equals(type) ? activeColor : inactiveColor);
    }

    private void selectGender(String gender) {
        selectedGender = gender;
        int activeColor = ContextCompat.getColor(requireContext(), R.color.primaryColor);
        int activeBg = ContextCompat.getColor(requireContext(), R.color.primaryLight);
        int inactiveBg = ContextCompat.getColor(requireContext(), R.color.backgroundColor);
        int inactiveText = ContextCompat.getColor(requireContext(), R.color.textColorMuted);

        if ("Male".equals(gender)) {
            btnGenderMale.setBackgroundColor(activeBg);
            btnGenderMale.setTextColor(activeColor);
            btnGenderFemale.setBackgroundColor(inactiveBg);
            btnGenderFemale.setTextColor(inactiveText);
        } else {
            btnGenderFemale.setBackgroundColor(activeBg);
            btnGenderFemale.setTextColor(activeColor);
            btnGenderMale.setBackgroundColor(inactiveBg);
            btnGenderMale.setTextColor(inactiveText);
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (datePicker, year, month, day) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
                    etDateOfBirth.setText(date);
                },
                cal.get(Calendar.YEAR) - 20,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void loadExistingData() {
        long userId = sessionManager.getLoggedInUserId();
        TodoDatabase.databaseWriteExecutor.execute(() -> {
            currentUser = db.userDao().getUserById(userId);
            if (currentUser != null && isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    if (currentUser.getFirstName() != null && !currentUser.getFirstName().isEmpty()) {
                        etFirstName.setText(currentUser.getFirstName());
                    }
                    if (currentUser.getLastName() != null && !currentUser.getLastName().isEmpty()) {
                        etLastName.setText(currentUser.getLastName());
                    }
                    if (currentUser.getDateOfBirth() != null && !currentUser.getDateOfBirth().isEmpty()) {
                        etDateOfBirth.setText(currentUser.getDateOfBirth());
                    }
                    if (currentUser.getGender() != null && !currentUser.getGender().isEmpty()) {
                        selectGender(currentUser.getGender());
                    }
                    if (currentUser.getAvatarType() != null) {
                        selectAvatar(currentUser.getAvatarType());
                    }
                });
            }
        });
    }

    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();

        if (firstName.isEmpty()) {
            Toast.makeText(requireContext(), "First name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(requireContext(), "Profile not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveProfile.setEnabled(false);

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setDateOfBirth(dob);
        currentUser.setGender(selectedGender);
        currentUser.setAvatarType(selectedAvatar);

        TodoDatabase.databaseWriteExecutor.execute(() -> {
            try {
                db.userDao().update(currentUser);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                        btnSaveProfile.setEnabled(true);
                        goBack();
                    });
                }
            } catch (Exception e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSaveProfile.setEnabled(true);
                    });
                }
            }
        });
    }

    private void goBack() {
        if (isAdded()) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
