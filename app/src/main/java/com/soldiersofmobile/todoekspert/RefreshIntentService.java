package com.soldiersofmobile.todoekspert;

import android.app.IntentService;
import android.content.Intent;

import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.TodosResponse;
import com.soldiersofmobile.todoekspert.db.TodoDao;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class RefreshIntentService extends IntentService {

    public static final String ACTION = "com.soldiersofmobile.todoekspert.REFRESH";
    @Inject
    LoginManager loginManager;
    @Inject
    TodoDao todoDao;
    @Inject
    TodoApi todoApi;

    public RefreshIntentService() {
        super(RefreshIntentService.class.getSimpleName());
        App.getTodoComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Timber.d("Refresh started");

        TodosResponse todosResponse = todoApi.getTodos(loginManager.getToken());
        for (Todo todo : todosResponse.results) {
            todoDao.insertOrUpdate(todo);
        }


        Timber.d("Refresh done");

        Intent broadcastIntent = new Intent(ACTION);
        sendBroadcast(broadcastIntent);


    }
}
