package com.ayadiyulianto.dailycoding

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ayadiyulianto.dailycoding.data.AppPreferences
import com.ayadiyulianto.dailycoding.data.local.entity.Story
import com.ayadiyulianto.dailycoding.data.remote.response.StoriesResponse
import com.ayadiyulianto.dailycoding.data.remote.retrofit.ApiConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val LOG_TAG: String = StackRemoteViewsFactory::class.java.simpleName

    private var mWidgetItems = ArrayList<Story>()

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        Log.v(LOG_TAG, "onDataSetChanged()")
        val apiService = ApiConfig.getApiService()
        val pref = AppPreferences.getInstance(mContext.dataStore)
        val token : String
        runBlocking(Dispatchers.IO) {
            token = "Bearer " + pref.getAuthToken().first()
        }
        val client = apiService.getStories(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val stories = loginResponse?.listStory
                    val storyList = ArrayList<Story>()
                    stories?.forEach { item ->
                        val story = Story(
                            item.id,
                            item.name,
                            item.description,
                            item.photoUrl,
                            item.createdAt,
                            item.lat,
                            item.lon
                        )
                        storyList.add(story)
                    }
                    mWidgetItems = storyList
                    Log.v(LOG_TAG, "onDataSetChanged(): story.size() = " + mWidgetItems.size)
                }
            }
            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                //
            }
        })
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.item_widget)
        val story = mWidgetItems[position]
        Glide.with(mContext)
            .asBitmap()
            .load(story.photoUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    rv.setImageViewBitmap(R.id.imageView, resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })

        val extras = bundleOf(
            DaycodingStoryWidget.EXTRA_ITEM to story.id
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}