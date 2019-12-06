package com.ksc.imagesearch.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.ksc.imagesearch.util.KakaoApi
import java.util.concurrent.Executor

class ImageDatasourceFactory(
    private val kakaoApi: KakaoApi,
    private val query: String,
    private val retryExecutor: Executor ) : DataSource.Factory<Int,ImageSearchResponse.Document>()
{
    val sourceLiveData = MutableLiveData<ImageDataSource>()
    override fun create(): DataSource<Int, ImageSearchResponse.Document> {
        val source = ImageDataSource(kakaoApi, query, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}