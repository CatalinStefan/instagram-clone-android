package com.catalin.instagramclone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.catalin.instagramclone.api.InstagramApiService.BASE_URL
import com.catalin.instagramclone.api.Post
import com.catalin.instagramclone.databinding.ItemPostBinding

class PostListAdapter(private var posts: List<Post>, val postCallback: PostCallback): RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private var loggedIn: Boolean = false

    fun onAuth(loggedIn: Boolean) {
        this.loggedIn = loggedIn
        notifyDataSetChanged()
    }

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, postCallback)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position], loggedIn)
    }

    override fun getItemCount() = posts.size

    class PostViewHolder(val binding: ItemPostBinding, val postCallback: PostCallback): RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, loggedIn: Boolean) {
            binding.postUsername.text = post.user.username
            binding.captionUsername.text = post.user.username + ":"
            binding.captionText.text = post.caption

            val imgContext = binding.postImage.context
            val circularProgressDrawable = CircularProgressDrawable(imgContext)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            val imageUrl = if (post.imageUrlType == "absolute") post.imageUrl else BASE_URL + post.imageUrl

            Glide.with(imgContext)
                .load(imageUrl)
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .into(binding.postImage)

            binding.commentLayout.removeAllViews()
            for (comment in post.comments) {
                val commentView = TextView(binding.commentLayout.context)
                commentView.text = "${comment.username}: ${comment.text}"
                binding.commentLayout.addView(commentView)
            }

            binding.deleteButton.visibility = if (loggedIn) View.VISIBLE else View.INVISIBLE
            binding.deleteButton.setOnClickListener { postCallback.onDeletePost(post.id) }

            binding.newCommentLayout.visibility = if (loggedIn) View.VISIBLE else View.GONE
            binding.commentPost.setOnClickListener {
                val text = binding.commentText.text.toString()
                if (!text.isNullOrEmpty()) {
                    postCallback.onComment(text, post.id)
                }
                binding.commentText.setText("")
            }
        }
    }

}