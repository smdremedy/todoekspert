package com.soldiersofmobile.todoekspert.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.soldiersofmobile.todoekspert.App;
import com.soldiersofmobile.todoekspert.LoginManager;
import com.soldiersofmobile.todoekspert.R;
import com.soldiersofmobile.todoekspert.Todo;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.TodosResponse;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;



public class TodoListActivity extends ActionBarActivity {

    public static final int REQUEST_CODE = 123;

    private LoginManager loginManager;
    private TodoApi todoApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App application = (App) getApplication();
        loginManager = application.getLoginManager();
        todoApi = application.getTodoApi();

        if(loginManager.needsLogin()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        setContentView(R.layout.activity_todo_list);
        Timber.plant(new Timber.DebugTree());
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
        if(id == R.id.action_refresh) {

            todoApi.getTodos(loginManager.getToken(), new Callback<TodosResponse>() {
                @Override
                public void success(TodosResponse todosResponse, Response response) {
                    for (Todo todo : todosResponse.results) {


                        Timber.d("Todos:" + todo);
                    }

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });


        }
        if(id == R.id.action_new) {
            startAddTodo();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAddTodo() {
        Intent intent = new Intent(getApplicationContext(), AddTodoActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {
            Timber.d("Result:" + resultCode + " data: " + data);
            if(resultCode == RESULT_OK) {
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
}
