package com.taskflow.todo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.taskflow.todo.R;
import com.taskflow.todo.fragment.AddTaskFragment;
import com.taskflow.todo.fragment.HomeFragment;
import com.taskflow.todo.fragment.ProfileFragment;
import com.taskflow.todo.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Fragment homeFragment;
    private Fragment addTaskFragment;
    private Fragment profileFragment;
    private Fragment activeFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.createNotificationChannel(this);
        requestNotificationPermission();

        fm = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        addTaskFragment = new AddTaskFragment();
        profileFragment = new ProfileFragment();
        activeFragment = homeFragment;

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fm.beginTransaction().add(R.id.nav_host_fragment, profileFragment, "3").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, addTaskFragment, "2").hide(addTaskFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            popEditProfileIfNeeded();
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;
            } else if (itemId == R.id.navigation_add_task) {
                fm.beginTransaction().hide(activeFragment).show(addTaskFragment).commit();
                activeFragment = addTaskFragment;
                return true;
            } else if (itemId == R.id.navigation_profile) {
                fm.beginTransaction().hide(activeFragment).show(profileFragment).commit();
                activeFragment = profileFragment;
                return true;
            }
            return false;
        });
    }

    private void popEditProfileIfNeeded() {
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate("edit_profile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void navigateToHome() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}
