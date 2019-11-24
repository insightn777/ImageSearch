package com.ksc.imagesearch.util

import com.ksc.imagesearch.data.ImageSearchResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KakaoApi {

    @Headers("Authorization: KakaoAK " + RESTAPIKEY)
    @GET("v2/search/image")
    fun searchImages(
        @Query("query") query: String,      // 필수
        @Query("sort") sort: String? = null,// accuracy (정확도순) or recency (최신순)
        @Query("page") page: Int,           // 결과 페이지 번호 1-50
        @Query("size") size: Int = 10       // 한 페이지에 보여질 문서의 개수 1-80
    ): Call<ImageSearchResponse>

    companion object {
        private const val BASE_URL = "https://dapi.kakao.com/"
        // https://developers.kakao.com/docs/restapi/search#%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B2%80%EC%83%89

        fun create(): KakaoApi {
            val client = OkHttpClient.Builder().build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KakaoApi::class.java)
        }
    }
}