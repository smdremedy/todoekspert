package com.soldiersofmobile.todoekspert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AddTodoActivity extends ActionBarActivity {


    public static final String TODO_KEY = "todo";
    @InjectView(R.id.taskEditText)
    EditText taskEditText;
    @InjectView(R.id.doneCheckBox)
    CheckBox doneCheckBox;
    @InjectView(R.id.saveButton)
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.saveButton)
    public void addTask() {
        tryToSave();


    }

    private void tryToSave() {
        String task = taskEditText.getText().toString();
        if(TextUtils.isEmpty(task)) {
            taskEditText.setError(getString(R.string.field_cannot_be_empty));
        } else {
            Todo todo = new Todo();
            todo.setTask(task);
            todo.setDone(doneCheckBox.isChecked());

            Intent data = new Intent();
            data.putExtra(TODO_KEY, todo);
            setResult(RESULT_OK, data);
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
