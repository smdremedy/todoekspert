package com.soldiersofmobile.todoekspert;

import android.app.Application;
import android.preference.PreferenceManager;

import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.di.DaggerTodoComponent;
import com.soldiersofmobile.todoekspert.di.TodoComponent;
import com.soldiersofmobile.todoekspert.di.TodoModule;

import retrofit.RestAdapter;
import timber.log.Timber;

public class App extends Application {


    public static TodoComponent getTodoComponent() {
        return todoComponent;
    }

    private static TodoComponent todoComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        todoComponent = DaggerTodoComponent.builder()
                .todoModule(new TodoModule(this))
                .build();




    }

}
