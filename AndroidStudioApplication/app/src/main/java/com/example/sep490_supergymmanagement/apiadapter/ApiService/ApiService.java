package com.example.sep490_supergymmanagement.apiadapter.ApiService;

import com.example.sep490_supergymmanagement.models.Post;
import com.example.sep490_supergymmanagement.models.PostCategory;
import com.example.sep490_supergymmanagement.models.RegisterUserDto;
import com.example.sep490_supergymmanagement.models.QrCodeRequest;
import com.example.sep490_supergymmanagement.models.QrCodeResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Part;

public interface ApiService {

    // Đăng ký người dùng mới
    @POST("/api/Auth/register")
    Call<Void> registerUser(@Body RegisterUserDto registerUserDto);

    // Tạo mã QR
    @POST("/api/qr/generate")
    Call<QrCodeResponse> generateQrCodes(@Body QrCodeRequest request);

    // 1. Lấy danh sách tất cả danh mục
    @GET("api/posts/categories")
    Call<List<PostCategory>> getCategories();

    // 2. Lấy danh sách tất cả bài viết
    @GET("api/posts/posts")
    Call<List<Post>> getAllPosts();

    // 3. Lấy 5 bài viết mới nhất
    @GET("api/posts/posts/latest")
    Call<List<Post>> getLatestPosts();

    // 4. Lấy chi tiết một bài viết
    @GET("api/posts/{postId}")
    Call<Post> getPostById(@Path("postId") String postId);

    // 5. Đăng bài viết mới với ảnh
    @Multipart
    @POST("api/posts")
    Call<Post> createPostWithImage(
            @Part MultipartBody.Part image,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("categoryId") RequestBody categoryId
    );

    // 6. Xóa bài viết
    @DELETE("api/posts/{postId}")
    Call<Void> deletePost(@Path("postId") String postId);

    // 7. Cập nhật bài viết
    @PUT("api/posts/{postId}")
    Call<Void> updatePost(@Path("postId") String postId, @Body Post updatedPost);
}
