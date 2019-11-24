package com.ksc.imagesearch.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.ksc.imagesearch.repository.NetworkState
import com.ksc.imagesearch.util.KakaoApi
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

class ImageDataSource(
        private val kakaoApi: KakaoApi,
        private val query: String,
        private val retryExecutor: Executor
) : PageKeyedDataSource<String,ImageSearchResponse.Document>()
{
    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null
    private var page = 1

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    var isEnd = false

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, ImageSearchResponse.Document>
    ) {
        page = 1
        val request = kakaoApi.searchImages(
            query = query,
            page = page++
        )
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()
            val items = response.body()?.documents ?: emptyList()
            isEnd = response.body()?.meta?.is_end ?: false
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(items,"${page}","${page+1}")
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, ImageSearchResponse.Document>
    ) {
        if ( isEnd ) return
        networkState.postValue(NetworkState.LOADING)
        kakaoApi.searchImages(
            query,
            page = page++
        ).enqueue(
            object : retrofit2.Callback<ImageSearchResponse> {
                override fun onFailure(call: Call<ImageSearchResponse>, t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.error(t.message ?: "unknown error"))
                }

                override fun onResponse(
                    call: Call<ImageSearchResponse>,
                    response: Response<ImageSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val items = response.body()?.documents ?: emptyList()
                        retry = null
                        callback.onResult(items, "$page")
                        networkState.postValue(NetworkState.LOADED)
                        isEnd = response.body()?.meta?.is_end ?: false
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error("error code: ${response.code()}"))
                    }
                }
            }
        )
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, ImageSearchResponse.Document>
    ) {
        // ignored, since we only ever append to our initial load
    }
}