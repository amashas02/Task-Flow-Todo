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

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private Button btnLogin;
    private Button btnRegisterNav;

    private TodoDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        setContentView(R.layout.activity_login);

        db = TodoDatabase.getDatabase(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterNav = findViewById(R.id.btnRegisterNav);

        btnLogin.setOnClickListener(v -> performLogin());

        btnRegisterNav.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill out all fields");
            return;
        }

        tvError.setVisibility(View.GONE);
        btnLogin.setEnabled(false);

        TodoDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User user = db.userDao().getUserByUsername(username);
                String hashedPw = CryptoUtil.hashPassword(password);
                
                runOnUiThread(() -> {
                    if (user != null && user.getPasswordHash().equals(hashedPw)) {
                        sessionManager.saveLoggedInUser(user.getId());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        showError("Invalid username or password");
                        btnLogin.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showError("Login error: " + e.getMessage());
                    btnLogin.setEnabled(true);
                });
            }
        });
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
