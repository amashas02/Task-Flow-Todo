package com.taskflow.todo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "taskflow_session";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLoggedInUser(long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public long getLoggedInUserId() {
        return prefs.getLong(KEY_USER_ID, -1L);
    }

    public boolean isLoggedIn() {
        return getLoggedInUserId() != -1L;
    }

    public void logout() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }
}
