package com.taskflow.todo.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String avatarType;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = "";
        this.lastName = "";
        this.dateOfBirth = "";
        this.gender = "";
        this.avatarType = "male";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAvatarType() { return avatarType; }
    public void setAvatarType(String avatarType) { this.avatarType = avatarType; }

    public String getDisplayName() {
        if (firstName != null && !firstName.isEmpty()) {
            String display = firstName;
            if (lastName != null && !lastName.isEmpty()) {
                display += " " + lastName;
            }
            return display;
        }
        return username;
    }
}
