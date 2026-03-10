package com.taskflow.todo.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.taskflow.todo.data.entity.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(long userId);

    @Query("UPDATE users SET avatarType = :avatarType WHERE id = :userId")
    void updateAvatar(long userId, String avatarType);

    @Query("UPDATE users SET firstName = :firstName, lastName = :lastName, dateOfBirth = :dob, gender = :gender WHERE id = :userId")
    void updateProfile(long userId, String firstName, String lastName, String dob, String gender);
}
