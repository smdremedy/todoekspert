package com.soldiersofmobile.todoekspert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;


public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.usernameEditText)
    EditText usernameEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.loginButton)
    Button loginButton;
    @InjectView(R.id.loginButton2)
    Button loginButton2;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());

    }

    @OnClick(R.id.loginButton)
    public void tryToLogin() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean hasError = false;

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.field_cannot_be_empty));
            hasError = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.field_cannot_be_empty));
            hasError = true;
        }
        if (!hasError) {
            login(username, password);
        }
    }

    private void login(final String username, final String password) {

        AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(20);
                        publishProgress(i);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "test".equals(username) && "test".equals(password);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressBar.setProgress(values[0]);
                String format = String.format("%s%%", +values[0]);
                loginButton2.setText(format);
                Timber.d(format);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loginButton.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                loginButton.setEnabled(true);
                if(result) {


                    finish();

                    Intent intent = new Intent(getApplicationContext(), TodoListActivity.class);
                    startActivity(intent);

                }

            }
        };
        asyncTask.execute(username, password);


    }

}
