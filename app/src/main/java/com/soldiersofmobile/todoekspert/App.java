package com.soldiersofmobile.todoekspert;

import android.app.Application;
import android.preference.PreferenceManager;

import com.soldiersofmobile.todoekspert.api.TodoApi;

import retrofit.RestAdapter;

public class App extends Application {

    public LoginManager getLoginManager() {
        return loginManager;
    }

    private LoginManager loginManager;
    private TodoApi todoApi;

    @Override
    public void onCreate() {
        super.onCreate();

        todoApi = new RestAdapter.Builder()
                .setEndpoint("https://api.parse.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build().create(TodoApi.class);
        loginManager = new LoginManager(PreferenceManager.getDefaultSharedPreferences(this), todoApi);

    }

    public TodoApi getTodoApi() {
        return todoApi;
    }
}
