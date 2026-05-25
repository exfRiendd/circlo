package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("user")
    private T user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData()         { return data; }
    public T getUser()         { return user; }
}
