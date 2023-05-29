package com.ayadiyulianto.dailycoding.ui.story

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayadiyulianto.dailycoding.R
import com.ayadiyulianto.dailycoding.adapter.LoadingStateAdapter
import com.ayadiyulianto.dailycoding.adapter.StoryListAdapter
import com.ayadiyulianto.dailycoding.adapter.StoryPagingAdapter
import com.ayadiyulianto.dailycoding.data.Result
import com.ayadiyulianto.dailycoding.databinding.ActivityMainBinding
import com.ayadiyulianto.dailycoding.ui.ViewModelFactory
import com.ayadiyulianto.dailycoding.ui.auth.AuthViewModel
import com.ayadiyulianto.dailycoding.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyListAdapter: StoryListAdapter

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(this)
    }

    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        storyListAdapter = StoryListAdapter { story ->
            Toast.makeText(this, getString(R.string.save_to_favorite), Toast.LENGTH_SHORT).show()
        }

        authViewModel.userToken.observe(this) { token ->
            if (token.isEmpty()) gotoLogin()

            storyViewModel.setToken(token)
            Log.e("TOKEN", "Token $token")
            getData()
        }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyListAdapter
        }

        binding.fabNewStory.setOnClickListener {
            val intent = Intent(this, StoryAddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getData() {
        val adapter = StoryPagingAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        storyViewModel.storiesData().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun gotoLogin() {
        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.locations -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                authViewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}