package com.catalin.instagramclone.api

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("image_url_type") val imageUrlType: String,
    val caption: String,
    val timestamp: String,
    val user: PostUser,
    val comments: List<Comment>
)

data class PostUser(
    val username: String
)

data class Comment(
    val text: String,
    val username: String,
    val timestamp: String
)

data class UserLoginResponse(
    @SerializedName("user_id") val userId: Int,
    val username: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class UserSignupRequest(
    val username: String,
    val email: String,
    val password: String
)

data class UserSignupResponse(
    val username: String,
    val email: String
)

data class ImageUploadResponse(
    val filename: String
)

data class CreatePost(
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("image_url_type") val imageUrlType: String,
    val caption: String,
    @SerializedName("creator_id") val creatorId: Int
)

data class CreatePostResponse(
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("image_url_type") val imageUrlType: String,
    val caption: String,
    @SerializedName("user_id") val userId: Int,
    val timestamp: String,
    val id: Int
)

data class CreateComment(
    val username: String,
    val text: String,
    @SerializedName("post_id") val postId: Int
)

data class CreateCommentResponse(
    val id: Int,
    val username: String,
    val text: String,
    @SerializedName("post_id") val postId: Int,
    val timestamp: String
)