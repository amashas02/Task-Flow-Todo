package com.taskflow.todo.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.taskflow.todo.R;
import com.taskflow.todo.adapter.TaskAdapter;
import com.taskflow.todo.data.db.TodoDatabase;
import com.taskflow.todo.data.entity.Todo;
import com.taskflow.todo.util.NotificationHelper;
import com.taskflow.todo.util.SessionManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvTasks;
    private LinearLayout emptyState;
    private TaskAdapter adapter;
    private TextView tvCompletedTasks, tvTotalTasks, tvPercentage, tvRemainingTasks, tvTotalTasksLabel;
    private ProgressBar progressBar;
    private TabLayout tabLayout;

    private TodoDatabase db;
    private SessionManager sessionManager;

    private LiveData<List<Todo>> currentFilteredData;
    private LiveData<List<Todo>> allTasksData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = TodoDatabase.getDatabase(requireContext());
        sessionManager = new SessionManager(requireContext());

        rvTasks = view.findViewById(R.id.rvTasks);
        emptyState = view.findViewById(R.id.emptyState);
        tvCompletedTasks = view.findViewById(R.id.tvCompletedTasks);
        tvTotalTasks = view.findViewById(R.id.tvTotalTasks);
        tvPercentage = view.findViewById(R.id.tvPercentage);
        tvRemainingTasks = view.findViewById(R.id.tvRemainingTasks);
        tvTotalTasksLabel = view.findViewById(R.id.tvTotalTasksLabel);
        progressBar = view.findViewById(R.id.progressBar);
        tabLayout = view.findViewById(R.id.tabLayout);

        setupRecyclerView();
        setupTabs();
        loadAllTasks();
        applyFilter(0);

        return view;
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Todo todo) {
                showEditDialog(todo);
            }

            @Override
            public void onCheckClick(Todo todo) {
                boolean newStatus = !todo.isCompleted();
                TodoDatabase.databaseWriteExecutor.execute(() ->
                        db.todoDao().updateCompletionStatus(todo.getId(), newStatus));
                if (newStatus) {
                    NotificationHelper.showTaskCompletedNotification(requireContext(), todo.getTitle());
                }
            }

            @Override
            public void onEditClick(Todo todo) {
                showEditDialog(todo);
            }

            @Override
            public void onDeleteClick(Todo todo) {
                showDeleteConfirmation(todo);
            }
        });
        rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTasks.setAdapter(adapter);
    }

    private void showEditDialog(Todo todo) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_task, null);
        EditText etTitle = dialogView.findViewById(R.id.etEditTitle);
        EditText etDesc = dialogView.findViewById(R.id.etEditDesc);
        EditText etTags = dialogView.findViewById(R.id.etEditTags);

        etTitle.setText(todo.getTitle());
        etDesc.setText(todo.getDescription());
        etTags.setText(todo.getTags());

        final long todoId = todo.getId();

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTitle = etTitle.getText().toString().trim();
                    String newDesc = etDesc.getText().toString().trim();
                    String newTags = etTags.getText().toString().trim();

                    if (newTitle.isEmpty()) {
                        Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    TodoDatabase.databaseWriteExecutor.execute(() ->
                            db.todoDao().updateTask(todoId, newTitle, newDesc, newTags));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(Todo todo) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete \"" + todo.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) ->
                        TodoDatabase.databaseWriteExecutor.execute(() -> db.todoDao().delete(todo)))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                applyFilter(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadAllTasks() {
        long userId = sessionManager.getLoggedInUserId();
        allTasksData = db.todoDao().getTodosForUser(userId);
        allTasksData.observe(getViewLifecycleOwner(), this::updateProgress);
    }

    private void applyFilter(int tab) {
        long userId = sessionManager.getLoggedInUserId();

        if (currentFilteredData != null) {
            currentFilteredData.removeObservers(getViewLifecycleOwner());
        }

        switch (tab) {
            case 1:
                currentFilteredData = db.todoDao().getActiveTodosForUser(userId);
                break;
            case 2:
                currentFilteredData = db.todoDao().getCompletedTodosForUser(userId);
                break;
            default:
                currentFilteredData = db.todoDao().getTodosForUser(userId);
                break;
        }

        currentFilteredData.observe(getViewLifecycleOwner(), todos -> {
            adapter.setTasks(todos);
            emptyState.setVisibility(todos.isEmpty() ? View.VISIBLE : View.GONE);
            rvTasks.setVisibility(todos.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void updateProgress(List<Todo> todos) {
        int total = todos.size();
        int completed = 0;
        for (Todo t : todos) {
            if (t.isCompleted()) completed++;
        }

        tvCompletedTasks.setText(String.valueOf(completed));
        tvTotalTasks.setText(" / " + total);
        tvTotalTasksLabel.setText(total + " tasks");

        if (total > 0) {
            int percentage = (int) (((double) completed / total) * 100);
            tvPercentage.setText(percentage + "%");
            progressBar.setProgress(percentage);
            tvRemainingTasks.setText((total - completed) + " tasks remaining");
        } else {
            tvPercentage.setText("0%");
            progressBar.setProgress(0);
            tvRemainingTasks.setText("No tasks yet");
        }
    }
}
