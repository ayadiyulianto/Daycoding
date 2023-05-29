package com.ayadiyulianto.dailycoding.ui.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ayadiyulianto.dailycoding.MainDispatcherRule
import com.ayadiyulianto.dailycoding.adapter.StoryListAdapter
import com.ayadiyulianto.dailycoding.data.StoryRepository
import com.ayadiyulianto.dailycoding.data.local.entity.Story
import com.ayadiyulianto.dailycoding.getOrAwaitValue
import com.ayadiyulianto.dailycoding.utils.DataDummy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoriesResponse()
        val stories = dummyStories.listStory.map { item ->
            Story(
                item.id,
                item.name,
                item.description,
                item.photoUrl,
                item.createdAt,
                item.lat,
                item.lon
            )
        }
    //        val data: PagingData<Story> = PagingData.from(dummyStories)
        val data: PagingData<Story> = StoryPagingSource.snapshot(stories)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStoriesData("")).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<Story> = storyViewModel.storiesData().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.listStory.size, differ.snapshot().size)
        val dummyStory = dummyStories.listStory[0].let { item ->
            Story(
                item.id,
                item.name,
                item.description,
                item.photoUrl,
                item.createdAt,
                item.lat,
                item.lon
            )
        }
        Assert.assertEquals(dummyStory, differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<Story>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getStoriesData("")).thenReturn(expectedQuote)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualQuote: PagingData<Story> = storyViewModel.storiesData().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}