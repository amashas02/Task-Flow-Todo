package com.taskflow.todo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.taskflow.todo.R;
import com.taskflow.todo.data.db.TodoDatabase;
import com.taskflow.todo.data.entity.User;
import com.taskflow.todo.util.CryptoUtil;
import com.taskflow.todo.util.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUsername, etRegPassword, etRegConfirmPassword;
    private TextView tvRegError;
    private Button btnRegister, btnLoginNav;

    private TodoDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = TodoDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        tvRegError = findViewById(R.id.tvRegError);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginNav = findViewById(R.id.btnLoginNav);

        btnRegister.setOnClickListener(v -> performRegister());
        btnLoginNav.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String username = etRegUsername.getText().toString().trim();
        String password = etRegPassword.getText().toString();
        String confirm = etRegConfirmPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill out all fields");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match");
            return;
        }

        tvRegError.setVisibility(View.GONE);
        btnRegister.setEnabled(false);

        TodoDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User existingUser = db.userDao().getUserByUsername(username);
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        showError("Username already exists");
                        btnRegister.setEnabled(true);
                    });
                    return;
                }

                String hashedPw = CryptoUtil.hashPassword(password);
                User newUser = new User(username, hashedPw);
                long id = db.userDao().insert(newUser);

                runOnUiThread(() -> {
                    sessionManager.saveLoggedInUser(id);
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showError("Registration failed: " + e.getMessage());
                    btnRegister.setEnabled(true);
                });
            }
        });
    }

    private void showError(String msg) {
        tvRegError.setText(msg);
        tvRegError.setVisibility(View.VISIBLE);
    }
}
