package com.catalin.instagramclone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.catalin.instagramclone.databinding.ActivityMainBinding
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity(), AuthCallback, PostCallback {

    private val vm: MainViewModel by viewModels()
    private val adapter = PostListAdapter(arrayListOf(), this)
    private lateinit var binding: ActivityMainBinding

    private val RESULT_LOAD_IMG = 1
    private var imageStream: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        setupUI()
        setupObservables()
    }

    fun setupUI() {
        binding.postsRV.adapter = adapter
        binding.postsRV.layoutManager = LinearLayoutManager(this)

        binding.loginButton.setOnClickListener {
            val dialog = LoginDialog(this)
            dialog.show(supportFragmentManager, "LoginDialog")
        }

        binding.signupButton.setOnClickListener {
            val dialog = SignupDialog(this)
            dialog.show(supportFragmentManager, "SignupDialog")
        }

        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") {dialog, which -> vm.onLogout()}
                .setNegativeButton("Cancel") {dialog, which -> dialog.dismiss()}
                .show()
        }

        binding.selectImageButton.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(this, photoPickerIntent, RESULT_LOAD_IMG, null )
        }

        binding.performUpload.setOnClickListener {
            if (imageStream == null) {
                showToast("Please select image")
                return@setOnClickListener
            } else {
                vm.onPostUpload(imageStream!!, binding.caption.text.toString())
                imageStream = null
                binding.caption.setText("")
            }

            closeKeyboard()
        }

        binding.refreshLayout.setOnRefreshListener {
            adapter.updatePosts(listOf())
            vm.getAllPosts()
            Handler().postDelayed({ binding.refreshLayout.isRefreshing = false }, 1000)
        }
    }

    private fun closeKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.performUpload.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMG) {
            try {
                data?.data?.let {
                    imageStream = contentResolver.openInputStream(it)
                    showToast("Image selected")
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                showToast("Something went wrong")
            }
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun setupObservables() {
        vm.message.observe(this, Observer { msg ->
            showToast(msg)
        })

        vm.posts.observe(this, Observer { posts ->
            adapter.updatePosts(posts)
        })

        vm.loggedIn.observe(this, Observer { loggedIn ->
            binding.loginLayout.visibility = if (loggedIn) View.GONE else View.VISIBLE
            binding.logoutLayout.visibility = if (loggedIn) View.VISIBLE else View.GONE
            binding.uploadUnavailableMessage.visibility = if (loggedIn) View.GONE else View.VISIBLE
            binding.uploadLayout.visibility = if (loggedIn) View.VISIBLE else View.GONE
            adapter.onAuth(loggedIn)
        })
    }

    override fun onLogin(username: String, password: String) {
        vm.onLogin(username, password)
    }

    override fun onSignup(username: String, email: String, password: String) {
        vm.onSignup(username, email, password)
    }

    override fun onDeletePost(postId: Int) {
        vm.onDeletePost(postId)
    }

    override fun onComment(text: String, postId: Int) {
        vm.postComment(text, postId)
        closeKeyboard()
    }
}