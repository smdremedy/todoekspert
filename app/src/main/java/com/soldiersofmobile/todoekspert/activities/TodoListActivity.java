package com.soldiersofmobile.todoekspert.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.soldiersofmobile.todoekspert.App;
import com.soldiersofmobile.todoekspert.LoginManager;
import com.soldiersofmobile.todoekspert.R;
import com.soldiersofmobile.todoekspert.Todo;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.TodosResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class TodoListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 123;

    @Inject
    LoginManager loginManager;
    @Inject
    TodoApi todoApi;

    @InjectView(R.id.todosList)
    ListView todosList;
    private TodoAdapter adapter;


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
        Timber.plant(new Timber.DebugTree());


        adapter = new TodoAdapter(LayoutInflater.from(getApplicationContext()));
        todosList.setAdapter(adapter);


    }



    static class TodoAdapter extends BaseAdapter {


        private final LayoutInflater layoutInflater;

        public TodoAdapter(LayoutInflater layoutInflater) {

            this.layoutInflater = layoutInflater;
        }

        private ArrayList<Todo> arrayList = new ArrayList<>();

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Todo getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Timber.d("Pos:" + i + " view:" + view);

            if (getItemViewType(i) == 0) {
                return getView0(i, view, viewGroup);
            } else {
                return getView1(i, view, viewGroup);
            }

        }

        private View getView0(int i, View view, ViewGroup viewGroup) {
            View inflatedView = view;
            if (inflatedView == null) {
                inflatedView = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
            }

            ViewHolder viewHolder2 = (ViewHolder) inflatedView.getTag();
            if (viewHolder2 == null) {
                viewHolder2 = new ViewHolder(inflatedView);
                inflatedView.setTag(viewHolder2);
            }

            Todo todo = getItem(i);
            viewHolder2.itemContentTextView.setText(Boolean.toString(todo.isDone()));
            viewHolder2.itemDoneCheckBox.setChecked(todo.isDone());
            viewHolder2.itemDoneCheckBox.setText(todo.getContent());
            return inflatedView;
        }

        private View getView1(int i, View view, ViewGroup viewGroup) {
            View inflatedView = view;
            if (inflatedView == null) {
                inflatedView = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
            }

            ViewHolder viewHolder2 = (ViewHolder) inflatedView.getTag();
            if (viewHolder2 == null) {
                viewHolder2 = new ViewHolder(inflatedView);
                inflatedView.setTag(viewHolder2);
            }

            final Todo todo = getItem(i);
            viewHolder2.itemContentTextView.setText(Boolean.toString(todo.isDone()));
            viewHolder2.itemDoneCheckBox.setChecked(todo.isDone());
            viewHolder2.itemDoneCheckBox.setText(todo.getContent());

            inflatedView.setBackgroundResource(R.drawable.button_selector);
            return inflatedView;
        }

        public void addAll(List<Todo> results) {
            arrayList.addAll(results);
            notifyDataSetChanged();
        }

        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'list_item.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
        static class ViewHolder {
            @InjectView(R.id.itemDoneCheckBox)
            CheckBox itemDoneCheckBox;
            @InjectView(R.id.itemContentTextView)
            TextView itemContentTextView;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
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

            todoApi.getTodos(loginManager.getToken(), new Callback<TodosResponse>() {
                @Override
                public void success(TodosResponse todosResponse, Response response) {
                    adapter.addAll(todosResponse.results);

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });


        }
        if (id == R.id.action_new) {
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
}
