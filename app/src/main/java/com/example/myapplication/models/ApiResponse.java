package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("user")
    private User user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData()         { return data; }
    public User getUser()      { return user; }
}