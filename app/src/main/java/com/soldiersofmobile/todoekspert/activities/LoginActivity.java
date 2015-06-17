package com.soldiersofmobile.todoekspert.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.soldiersofmobile.todoekspert.App;
import com.soldiersofmobile.todoekspert.LoginManager;
import com.soldiersofmobile.todoekspert.R;
import com.soldiersofmobile.todoekspert.api.ApiError;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.UserResponse;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import timber.log.Timber;


public class LoginActivity extends AppCompatActivity implements LoginManager.LoginCallback {


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

    @Inject
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getTodoComponent().inject(this);

        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());

    }

    @Override
    protected void onResume() {
        super.onResume();
        loginManager.setLoginCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loginManager.setLoginCallback(null);
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

        loginManager.login(username, password);


    }

    @Override
    public void loginDone() {

        finish();

        Intent intent = new Intent(getApplicationContext(), TodoListActivity.class);
        startActivity(intent);


    }

    @Override
    public void loginError(final String error) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

            }
        });


    }
}
