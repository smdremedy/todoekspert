package com.soldiersofmobile.todoekspert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.soldiersofmobile.todoekspert.activities.LoginActivity;
import com.soldiersofmobile.todoekspert.activities.TodoListActivity;
import com.soldiersofmobile.todoekspert.api.ApiError;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.UserResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * Created by madejs on 16.06.15.
 */
public class LoginManager {

    public static final String USER_ID_PREFS_KEY = "userId";
    public static final String TOKEN_PREFS_KEY = "token";

    private final SharedPreferences sharedPreferences;
    private TodoApi todoApi;
    private String userId;

    public String getToken() {
        return token;
    }

    private String token;

    public void login(String username, String password) {
        asyncTask.execute(username, password);
    }

    public interface LoginCallback {
        void loginDone();
        void loginError(String error);
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    private LoginCallback loginCallback;

    AsyncTask<String, Integer, UserResponse> asyncTask = new AsyncTask<String, Integer, UserResponse>() {
        @Override
        protected UserResponse doInBackground(String... strings) {


            try {

                UserResponse result = todoApi.login(strings[0], strings[1]);
                Timber.d("Result:" + result.sessionToken);
                return result;
            } catch (final RetrofitError retrofitError) {
                final ApiError error = (ApiError) retrofitError.getBodyAs(ApiError.class);
                if(loginCallback != null) {
                    loginCallback.loginError(error.error);
                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), error.error, Toast.LENGTH_LONG).show();
//
//                    }
//                });

                return null;

            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(UserResponse result) {
            super.onPostExecute(result);
            if (result != null) {

                save(result.objectId, result.sessionToken);
                if(loginCallback != null) {
                    loginCallback.loginDone();
                }



            }

        }
    };


    public LoginManager(SharedPreferences sharedPreferences, TodoApi todoApi) {
        this.sharedPreferences = sharedPreferences;
        this.todoApi = todoApi;
        token = sharedPreferences.getString(TOKEN_PREFS_KEY, "");
        userId = sharedPreferences.getString(USER_ID_PREFS_KEY, "");

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean needsLogin() {
        return TextUtils.isEmpty(token);
    }

    public void logout() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        edit.apply();
    }

    public void save(String objectId, String sessionToken) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(USER_ID_PREFS_KEY, objectId);
        edit.putString(TOKEN_PREFS_KEY, sessionToken);


        edit.apply();
    }
}
