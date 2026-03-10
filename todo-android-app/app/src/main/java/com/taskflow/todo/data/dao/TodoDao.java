package com.taskflow.todo.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.taskflow.todo.data.entity.Todo;

import java.util.List;

@Dao
public interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Todo todo);

    @Update
    void update(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Todo>> getTodosForUser(long userId);

    @Query("SELECT * FROM todos WHERE userId = :userId AND isCompleted = 0 ORDER BY createdAt DESC")
    LiveData<List<Todo>> getActiveTodosForUser(long userId);

    @Query("SELECT * FROM todos WHERE userId = :userId AND isCompleted = 1 ORDER BY createdAt DESC")
    LiveData<List<Todo>> getCompletedTodosForUser(long userId);

    @Query("UPDATE todos SET isCompleted = :isCompleted WHERE id = :todoId")
    void updateCompletionStatus(long todoId, boolean isCompleted);

    @Query("UPDATE todos SET title = :title, description = :desc, tags = :tags WHERE id = :todoId")
    void updateTask(long todoId, String title, String desc, String tags);
}