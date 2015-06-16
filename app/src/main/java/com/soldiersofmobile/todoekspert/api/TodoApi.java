package com.soldiersofmobile.todoekspert.api;

import com.soldiersofmobile.todoekspert.Todo;
import com.soldiersofmobile.todoekspert.api.UserResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Query;

public interface TodoApi {

    @Headers({
            "X-Parse-Application-Id: EhZ7ps1nVRbYCz4d1IkLlOLUAMkuzaA6NGS89hDX",
            "X-Parse-REST-API-Key: 0cc1iqhHHEg3j8do8b6DNkjC0nmnNNMKVJ11blov",
            "X-Parse-Revocable-Session: 1"
    })
    @GET("/1/login")
    UserResponse login(@Query("username") String username,
                 @Query("password") String password);

    @Headers({
            "X-Parse-Application-Id: EhZ7ps1nVRbYCz4d1IkLlOLUAMkuzaA6NGS89hDX",
            "X-Parse-REST-API-Key: 0cc1iqhHHEg3j8do8b6DNkjC0nmnNNMKVJ11blov",
            "X-Parse-Revocable-Session: 1"
    })
    @GET("/1/classes/Todo")
    void getTodos(@Header("X-Parse-Session-Token") String token, Callback<TodosResponse> callback);
}
