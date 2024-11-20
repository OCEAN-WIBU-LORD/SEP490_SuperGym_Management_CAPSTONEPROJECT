package com.supergym.sep490_supergymmanagement.apiadapter.ApiService;

import com.supergym.sep490_supergymmanagement.models.MembershipPackage;
import com.supergym.sep490_supergymmanagement.models.Post;
import com.supergym.sep490_supergymmanagement.models.PostCategory;
import com.supergym.sep490_supergymmanagement.models.QrCodeRequest;
import com.supergym.sep490_supergymmanagement.models.QrCodeResponse;
import com.supergym.sep490_supergymmanagement.models.RegisterUserDto;
import com.supergym.sep490_supergymmanagement.models.Transaction;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/Auth/register")
    Call<Void> registerUser(@Body RegisterUserDto registerUserDto);

    // New API call for generating QR codes
    @POST("/api/GymRegistration/CreateRegistration")
    Call<QrCodeResponse> generateQrCodes(@Body QrCodeRequest request);

    @POST("api/facedata/registerFace")
    Call<Void> registerFace(@Body Map<String, Object> faceData);


    @GET("/api/membership")
    Call<List<MembershipPackage>> getMembershipPackages();
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
    @GET("api/posts/posts/{postId}")
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

    @DELETE("posts/{postId}.json")
    Call<Void> deletePost(@Path("postId") String postId);

    // 7. Cập nhật bài viết
    @PUT("api/posts/posts/{postId}")
    Call<Void> updatePost(@Path("postId") String postId, @Body Post updatedPost);

    // 8. Search posts by category
    @GET("api/posts/posts/category/{categoryId}")
    Call<List<Post>> getPostsByCategory(@Path("categoryId") String categoryId);

    @GET("api/Posts/posts/user/{userId}")
    Call<List<Post>> getPostsByUserId(@Path("userId") String userId);

    @GET("/api/PaymentHistory/{userId}")
    Call<List<Transaction>> getPaymentHistory(@Path("userId") String userId);

}
