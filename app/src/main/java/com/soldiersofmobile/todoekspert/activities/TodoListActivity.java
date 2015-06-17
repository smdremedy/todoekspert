package com.soldiersofmobile.todoekspert.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.soldiersofmobile.todoekspert.App;
import com.soldiersofmobile.todoekspert.LoginManager;
import com.soldiersofmobile.todoekspert.R;
import com.soldiersofmobile.todoekspert.RefreshIntentService;
import com.soldiersofmobile.todoekspert.Todo;
import com.soldiersofmobile.todoekspert.TodoProvider;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.TodosResponse;
import com.soldiersofmobile.todoekspert.db.TodoDao;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class TodoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int REQUEST_CODE = 123;

    @Inject
    LoginManager loginManager;
    @Inject
    TodoApi todoApi;

    @Inject
    TodoDao todoDao;

    @InjectView(R.id.todosList)
    ListView todosList;
    //private TodoAdapter adapter;
    private SimpleCursorAdapter adapter;
    private String[] from = new String[]{TodoDao.C_CONTENT, TodoDao.C_DONE};
    private int[] to = new int[]{R.id.itemDoneCheckBox, R.id.itemDoneCheckBox};

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            refresh();

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(RefreshIntentService.ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getTodoComponent().inject(this);

        if (loginManager.needsLogin()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        setContentView(R.layout.activity_todo_list);
        ButterKnife.inject(this);




        //adapter = new TodoAdapter(LayoutInflater.from(getApplicationContext()));

        adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_item,
                null, from, to, 0);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                int columnIndex = cursor.getColumnIndex(TodoDao.C_DONE);
                if(columnIndex == i) {
                    int doneInt = cursor.getInt(columnIndex);
                    CheckBox checkBox = (CheckBox) view;
                    checkBox.setChecked(doneInt == 1);
                    return true;

                }
                return false;
            }
        });
        todosList.setAdapter(adapter);


//        getContentResolver().registerContentObserver(TodoProvider.CONTENT_URI,
//                true, new ContentObserver(null) {
//                    @Override
//                    public void onChange(boolean selfChange) {
//
//                        super.onChange(selfChange);
//                        Timber.d("Refresh from observer");
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                refresh();
//                            }
//                        });
//
//                    }
//                });
        getSupportLoaderManager().initLoader(1, null, this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        if (id == R.id.action_refresh) {


            Intent intent = new Intent(getApplicationContext(), RefreshIntentService.class);

            startService(intent);


        }
        if (id == R.id.action_new) {
            startAddTodo();
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {

        Cursor cursor = todoDao.query(loginManager.getUserId(), true);
        adapter.changeCursor(cursor);
    }

    private void startAddTodo() {
        Intent intent = new Intent(getApplicationContext(), AddTodoActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Timber.d("Result:" + resultCode + " data: " + data);
            if (resultCode == RESULT_OK) {
                Todo todo = (Todo) data.getParcelableExtra(AddTodoActivity.TODO_KEY);

                Timber.d("OK:" + todo.getContent() + " is done :" + todo.isDone());


            }

        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.are_you_sure));
        builder.setMessage(getString(R.string.do_you_want_to_logout));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loginManager.logout();

                finish();
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
                TodoProvider.CONTENT_URI,
                null, null, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("Reloaded cursor:" + data);
        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("Reset cursor");
        adapter.swapCursor(null);

    }
}
