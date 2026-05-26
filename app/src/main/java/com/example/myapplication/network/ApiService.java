package com.example.myapplication.network;

import com.example.myapplication.models.ApiResponse;
import com.example.myapplication.models.Barang;
import com.example.myapplication.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // Auth
    @POST("login.php")
    Call<ApiResponse<User>> login(@Body Map<String, String> body);

    @POST("register.php")
    Call<ApiResponse<User>> register(@Body Map<String, String> body);

    // Barang
    @GET("get_barang.php")
    Call<ApiResponse<List<Barang>>> getBarang();

    @GET("get_barang.php")
    Call<ApiResponse<List<Barang>>> getBarangByKategori(@Query("kategori") String kategori);

    @POST("add_barang.php")
    Call<ApiResponse<Barang>> addBarang(@Body Map<String, Object> body);

    @GET("get_profile.php")
    Call<ApiResponse<User>> getProfile(@Query("user_id") int userId);

    @POST("update_profile.php")
    Call<ApiResponse<User>> updateProfile(@Body Map<String, Object> body);
}
