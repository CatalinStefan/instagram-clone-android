package com.catalin.instagramclone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.catalin.instagramclone.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

    val posts = MutableLiveData<List<Post>>()
    val loggedIn = MutableLiveData<Boolean>()
    val message = MutableLiveData<String>()

    private var accessToken: String = ""
    private var currentUsername: String? = null
    private var currentUserId: Int? = null

    init {
        getAllPosts()
    }

    fun getAllPosts() {
        InstagramApiService.api
            .getAllPosts()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        val list = response.body()
                        posts.value = list ?: listOf()
                    } else {
                        message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    handleError(t)
                }

            })
    }

    fun onLogin(username: String, password: String) {
        InstagramApiService.api
            .login(username, password)
            .enqueue(object : Callback<UserLoginResponse> {
                override fun onResponse(
                    call: Call<UserLoginResponse>,
                    response: Response<UserLoginResponse>
                ) {
                    if (response.isSuccessful) {
                        accessToken = "${response.body()?.tokenType} ${response.body()?.accessToken}"
                        currentUsername = response.body()?.username
                        currentUserId = response.body()?.userId
                        loggedIn.value = true
                    } else {
                        message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    handleError(t)
                }

            })
    }

    fun onSignup(username: String, email: String, password: String) {
        val signupRequest = UserSignupRequest(username, email, password)
        InstagramApiService.api
            .signup(signupRequest)
            .enqueue(object : Callback<UserSignupResponse> {
                override fun onResponse(
                    call: Call<UserSignupResponse>,
                    response: Response<UserSignupResponse>
                ) {
                    if (response.isSuccessful) {
                        message.value = "User $username created. Logging in..."
                        onLogin(username, password)
                    } else {
                        message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<UserSignupResponse>, t: Throwable) {
                    handleError(t)
                }

            })
    }

    private fun handleError(t: Throwable) {
        message.value = t.localizedMessage
        t.printStackTrace()
    }

    fun onLogout() {
        message.value = "Logged out"
        accessToken = ""
        currentUsername = null
        currentUserId = null
        loggedIn.value = false
    }
}