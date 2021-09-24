package com.catalin.instagramclone

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.catalin.instagramclone.databinding.DialogSignupBinding

class SignupDialog(val callback: AuthCallback): DialogFragment() {

    lateinit var binding: DialogSignupBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogSignupBinding.inflate(it.layoutInflater)

            binding.signupButton.setOnClickListener {
                binding.errorField.visibility = View.GONE
                val username = binding.usernameET.text.toString()
                val email = binding.emailET.text.toString()
                val password = binding.passwordET.text.toString()
                var msg = ""
                if (username.isEmpty()) msg += "Username cannot be empty\n"
                if (email.isEmpty()) msg += "Email cannot be empty\n"
                if (password.isEmpty()) msg += "Password cannot be empty\n"

                if (msg.isEmpty()) {
                    callback.onSignup(username, email, password)
                    this@SignupDialog.dismiss()
                } else {
                    msg = msg.substring(0, msg.length - 1)
                    binding.errorField.text = msg
                    binding.errorField.visibility = View.VISIBLE
                }
            }

            return AlertDialog.Builder(it)
                .setView(binding.root)
                .create()
        } ?: return super.onCreateDialog(savedInstanceState)
    }
}