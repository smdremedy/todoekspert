package com.soldiersofmobile.todoekspert.di;

import com.soldiersofmobile.todoekspert.LoginManager;
import com.soldiersofmobile.todoekspert.activities.LoginActivity;
import com.soldiersofmobile.todoekspert.activities.TodoListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        TodoModule.class
})
public interface TodoComponent {

    void inject(LoginActivity loginActivity);

    LoginManager getLoginManager();

    void inject(TodoListActivity todoListActivity);
}
