package com.soldiersofmobile.todoekspert.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.soldiersofmobile.todoekspert.api.TodoApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module
public class TodoModule {

    private final Context context;

    public TodoModule(Context context) {

        this.context = context;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Singleton
    @Provides
    public TodoApi provideTodoApi() {
        return new RestAdapter.Builder()
                .setEndpoint("https://api.parse.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build().create(TodoApi.class);
    }
}
