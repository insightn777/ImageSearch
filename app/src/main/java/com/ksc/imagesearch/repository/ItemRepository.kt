package com.ksc.imagesearch.repository

import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.ksc.imagesearch.data.ImageDatasourceFactory
import com.ksc.imagesearch.util.KakaoApi
import java.util.concurrent.Executor

class ItemRepository(
    private val service: KakaoApi,
    private val networkExecutor: Executor
) {
    fun search(query: String): Listing {
        val dataSourceFactory = ImageDatasourceFactory(service, query, networkExecutor)

        val livePagedList = LivePagedListBuilder(dataSourceFactory,10).build()

        val refreshState = Transformations.switchMap(dataSourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(dataSourceFactory.sourceLiveData) {
                it.networkState
            },
            refreshState = refreshState,
            refresh = {
                dataSourceFactory.sourceLiveData.value?.invalidate()
            },
            retry = {
                dataSourceFactory.sourceLiveData.value?.retryAllFailed()
            }
        )
    }
}
