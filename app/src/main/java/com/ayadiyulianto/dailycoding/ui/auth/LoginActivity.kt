package com.ayadiyulianto.dailycoding.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.ayadiyulianto.dailycoding.R
import com.ayadiyulianto.dailycoding.data.Result
import com.ayadiyulianto.dailycoding.databinding.ActivityLoginBinding
import com.ayadiyulianto.dailycoding.ui.ViewModelFactory
import com.ayadiyulianto.dailycoding.ui.story.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        hideSystemUI()

        loggingIn()

        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegister.setOnClickListener { register() }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun loggingIn() {
        authViewModel.isLoggedIn().observe(this) { isLoggedIn ->
            if (isLoggedIn) gotoMain()
        }
    }

    private fun login() {
        binding.progressBar.visibility = View.VISIBLE
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        authViewModel.login(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val user = result.data
                        authViewModel.saveUser(user)
                        Toast.makeText(
                            this,
                            getString(R.string.login_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        // Go to Main
                        gotoMain()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            getString(R.string.error_message, result.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun register() {
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, binding.imgLogo, "logo"
            )

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent, optionsCompat.toBundle())
    }

    private fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}