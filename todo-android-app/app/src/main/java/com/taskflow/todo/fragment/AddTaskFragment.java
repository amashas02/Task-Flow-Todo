package com.taskflow.todo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.taskflow.todo.R;
import com.taskflow.todo.activity.MainActivity;
import com.taskflow.todo.data.db.TodoDatabase;
import com.taskflow.todo.data.entity.Todo;
import com.taskflow.todo.util.NotificationHelper;
import com.taskflow.todo.util.SessionManager;

public class AddTaskFragment extends Fragment {

    private EditText etTitle, etDesc, etTags;
    private Button btnSave;

    private TodoDatabase db;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        db = TodoDatabase.getDatabase(requireContext());
        sessionManager = new SessionManager(requireContext());

        etTitle = view.findViewById(R.id.etTaskTitle);
        etDesc = view.findViewById(R.id.etTaskDesc);
        etTags = view.findViewById(R.id.etTaskTags);
        btnSave = view.findViewById(R.id.btnSaveTask);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        btnSave.setOnClickListener(v -> saveTask());

        return view;
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String tags = etTags.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = sessionManager.getLoggedInUserId();
        Todo newTodo = new Todo(userId, title, desc, tags);

        btnSave.setEnabled(false);

        TodoDatabase.databaseWriteExecutor.execute(() -> {
            try {
                db.todoDao().insert(newTodo);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show();
                    NotificationHelper.showTaskAddedNotification(requireContext(), title);
                    etTitle.setText("");
                    etDesc.setText("");
                    etTags.setText("");
                    btnSave.setEnabled(true);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateToHome();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error saving task", Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                });
            }
        });
    }
}
