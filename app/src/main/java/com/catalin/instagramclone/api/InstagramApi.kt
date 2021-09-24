package com.catalin.instagramclone.api

import retrofit2.Call
import retrofit2.http.*

interface InstagramApi {
    @GET("post/all")
    fun getAllPosts(): Call<List<Post>>

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("username") username: String,
              @Field("password") password: String
    ): Call<UserLoginResponse>

    @POST("user")
    fun signup(@Body user: UserSignupRequest): Call<UserSignupResponse>
}