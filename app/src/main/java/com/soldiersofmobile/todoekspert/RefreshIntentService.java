package com.soldiersofmobile.todoekspert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.soldiersofmobile.todoekspert.activities.TodoListActivity;
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
    private static final int NOTIFICATION_ID = 1;
    @Inject
    LoginManager loginManager;
    @Inject
    TodoDao todoDao;
    @Inject
    TodoApi todoApi;
    private NotificationManager mNotificationManager;

    public RefreshIntentService() {
        super(RefreshIntentService.class.getSimpleName());
        App.getTodoComponent().inject(this);

    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Timber.d("Refresh started");

        TodosResponse todosResponse = todoApi.getTodos(loginManager.getToken());
        for (Todo todo : todosResponse.results) {
            //todoDao.insertOrUpdate(todo);
            ContentValues values = new ContentValues();
            values.put(TodoDao.C_ID, todo.objectId);
            values.put(TodoDao.C_CONTENT, todo.getContent());
            values.put(TodoDao.C_DONE, todo.isDone());
//            values.put(TodoDao.C_CREATED_AT, todo.createdAt.getTime());
//            values.put(TodoDao.C_UPDATED_AT, todo.updatedAt.getTime());
            values.put(TodoDao.C_USER_ID, todo.user.objectId);

            getContentResolver().insert(TodoProvider.CONTENT_URI, values);
        }


        Timber.d("Refresh done");

        Intent broadcastIntent = new Intent(ACTION);
        sendBroadcast(broadcastIntent);
        sendTimelineNotification(10);


    }

    private void sendTimelineNotification(int timelineUpdateCount) {

        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        mNotificationManager.cancel(NOTIFICATION_ID);
        String notificationSummary = this.getString(
                R.string.notification_message, timelineUpdateCount);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        builder.setAutoCancel(true);
        builder.setContentTitle(getText(R.string.notification_title));
        builder.setContentText(notificationSummary);

        builder.setSmallIcon(R.drawable.ic_launcher);

        Intent backIntent = new Intent(this, TodoListActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, backIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}
