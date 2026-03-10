package com.taskflow.todo.adapter;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taskflow.todo.R;
import com.taskflow.todo.data.entity.Todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Todo> tasks = new ArrayList<>();
    private final OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Todo todo);
        void onCheckClick(Todo todo);
        void onEditClick(Todo todo);
        void onDeleteClick(Todo todo);
    }

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Todo> newTasks) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return tasks.size(); }

            @Override
            public int getNewListSize() { return newTasks.size(); }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return tasks.get(oldPos).getId() == newTasks.get(newPos).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                Todo oldTask = tasks.get(oldPos);
                Todo newTask = newTasks.get(newPos);
                return oldTask.isCompleted() == newTask.isCompleted()
                        && Objects.equals(oldTask.getTitle(), newTask.getTitle())
                        && Objects.equals(oldTask.getDescription(), newTask.getDescription())
                        && Objects.equals(oldTask.getTags(), newTask.getTags());
            }
        });
        this.tasks = newTasks;
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle, tvDesc, tvTags;
        private final ImageView ivCheck, ivEdit, ivDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvTags = itemView.findViewById(R.id.tvTaskTags);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);

            ivCheck.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCheckClick(tasks.get(position));
                }
            });

            ivEdit.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(tasks.get(position));
                }
            });

            ivDelete.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(tasks.get(position));
                }
            });
        }

        public void bind(Todo task) {
            tvTitle.setText(task.getTitle());

            if (task.isCompleted()) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.textColorMuted));
                ivCheck.setImageResource(R.drawable.ic_check);
                ImageViewCompat.setImageTintList(ivCheck,
                        ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.primaryColor)));
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.textColorDark));
                ivCheck.setImageResource(R.drawable.ic_check_circle_outline);
                ImageViewCompat.setImageTintList(ivCheck,
                        ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.textColorMuted)));
            }

            if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
                tvDesc.setText(task.getDescription());
                tvDesc.setVisibility(View.VISIBLE);
            } else {
                tvDesc.setVisibility(View.GONE);
            }

            if (task.getTags() != null && !task.getTags().trim().isEmpty()) {
                tvTags.setText(task.getTags());
                tvTags.setVisibility(View.VISIBLE);
            } else {
                tvTags.setVisibility(View.GONE);
            }
        }
    }
}
