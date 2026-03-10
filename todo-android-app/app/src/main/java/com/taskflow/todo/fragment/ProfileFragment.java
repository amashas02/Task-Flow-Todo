package com.taskflow.todo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.taskflow.todo.R;
import com.taskflow.todo.activity.LoginActivity;
import com.taskflow.todo.data.db.TodoDatabase;
import com.taskflow.todo.data.entity.Todo;
import com.taskflow.todo.data.entity.User;
import com.taskflow.todo.util.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileUsername;
    private TextView tvStatCreated, tvStatActive, tvStatCompleted;
    private TextView tvProfileFirstName, tvProfileLastName, tvProfileDob, tvProfileGender;
    private TextView tvNoDetails;
    private LinearLayout rowFirstName, rowLastName, rowDob, rowGender;
    private ImageView ivProfileAvatar;

    private TodoDatabase db;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = TodoDatabase.getDatabase(requireContext());
        sessionManager = new SessionManager(requireContext());

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvStatCreated = view.findViewById(R.id.tvStatCreated);
        tvStatActive = view.findViewById(R.id.tvStatActive);
        tvStatCompleted = view.findViewById(R.id.tvStatCompleted);
        tvProfileFirstName = view.findViewById(R.id.tvProfileFirstName);
        tvProfileLastName = view.findViewById(R.id.tvProfileLastName);
        tvProfileDob = view.findViewById(R.id.tvProfileDob);
        tvProfileGender = view.findViewById(R.id.tvProfileGender);
        tvNoDetails = view.findViewById(R.id.tvNoDetails);
        rowFirstName = view.findViewById(R.id.rowFirstName);
        rowLastName = view.findViewById(R.id.rowLastName);
        rowDob = view.findViewById(R.id.rowDob);
        rowGender = view.findViewById(R.id.rowGender);

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> performLogout());
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> openEditProfile());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        long userId = sessionManager.getLoggedInUserId();

        TodoDatabase.databaseWriteExecutor.execute(() -> {
            User user = db.userDao().getUserById(userId);
            if (user != null && isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    tvProfileName.setText(user.getDisplayName());
                    tvProfileUsername.setText("@" + user.getUsername());

                    if ("female".equals(user.getAvatarType())) {
                        ivProfileAvatar.setImageResource(R.drawable.ic_avatar_female);
                    } else {
                        ivProfileAvatar.setImageResource(R.drawable.ic_avatar_male);
                    }

                    boolean hasAnyDetail = false;

                    if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                        tvProfileFirstName.setText(user.getFirstName());
                        rowFirstName.setVisibility(View.VISIBLE);
                        hasAnyDetail = true;
                    } else {
                        rowFirstName.setVisibility(View.GONE);
                    }

                    if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                        tvProfileLastName.setText(user.getLastName());
                        rowLastName.setVisibility(View.VISIBLE);
                        hasAnyDetail = true;
                    } else {
                        rowLastName.setVisibility(View.GONE);
                    }

                    if (user.getDateOfBirth() != null && !user.getDateOfBirth().isEmpty()) {
                        tvProfileDob.setText(user.getDateOfBirth());
                        rowDob.setVisibility(View.VISIBLE);
                        hasAnyDetail = true;
                    } else {
                        rowDob.setVisibility(View.GONE);
                    }

                    if (user.getGender() != null && !user.getGender().isEmpty()) {
                        tvProfileGender.setText(user.getGender());
                        rowGender.setVisibility(View.VISIBLE);
                        hasAnyDetail = true;
                    } else {
                        rowGender.setVisibility(View.GONE);
                    }

                    tvNoDetails.setVisibility(hasAnyDetail ? View.GONE : View.VISIBLE);
                });
            }
        });

        db.todoDao().getTodosForUser(userId).observe(getViewLifecycleOwner(), todos -> {
            int total = todos.size();
            int completed = 0;
            for (Todo t : todos) {
                if (t.isCompleted()) completed++;
            }
            int active = total - completed;
            tvStatCreated.setText(String.valueOf(total));
            tvStatActive.setText(String.valueOf(active));
            tvStatCompleted.setText(String.valueOf(completed));
        });
    }

    private void openEditProfile() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.nav_host_fragment, new EditProfileFragment())
                .addToBackStack("edit_profile")
                .commit();
    }

    private void performLogout() {
        sessionManager.logout();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
