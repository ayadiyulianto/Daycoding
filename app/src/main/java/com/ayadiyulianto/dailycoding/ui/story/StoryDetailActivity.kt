package com.ayadiyulianto.dailycoding.ui.story

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ayadiyulianto.dailycoding.R
import com.ayadiyulianto.dailycoding.data.local.entity.Story
import com.ayadiyulianto.dailycoding.databinding.ActivityStoryDetailBinding
import com.ayadiyulianto.dailycoding.utils.DateFormatter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private var story : Story? = null
    private var storyID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        story = intent.getParcelableExtra(EXTRA_STORY)
        storyID = intent.getIntExtra(EXTRA_STORY_ID, -1)
        if (story == null) {
            if (storyID < 0) {
                finish()
            }
        }
        setupView()
    }

    private fun setupView() {
        binding.apply {
            tvUserName.text = story?.name
            tvDescription.text = story?.description
            tvDateUpload.text = DateFormatter.formatDate(story!!.createdAt)
            Glide.with(this@StoryDetailActivity)
                .load(story?.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
                )
                .into(imgPhoto)
            if (story!!.isFavorited) {
                imgFavorite.setImageDrawable(
                    ContextCompat.getDrawable(this@StoryDetailActivity, R.drawable.baseline_favorite_24)
                )
            } else {
                imgFavorite.setImageDrawable(
                    ContextCompat.getDrawable(this@StoryDetailActivity, R.drawable.baseline_favorite_border_24)
                )
            }
            imgFavorite.setOnClickListener {
                onFavoriteClick()
            }
        }
    }

    private fun onFavoriteClick() {
        Toast.makeText(this, getString(R.string.save_to_favorite), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}