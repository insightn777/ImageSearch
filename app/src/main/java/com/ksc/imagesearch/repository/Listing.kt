package com.ksc.imagesearch.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.ksc.imagesearch.data.ImageSearchResponse

data class Listing (
    val pagedList: LiveData<PagedList<ImageSearchResponse.Document>>,
    // represents the network request status to show to the user
    val networkState: LiveData<NetworkState>,
    // represents the refresh status to show to the user. Separate from networkState, this
    // value is importantly only when refresh is requested.
    val refreshState: LiveData<NetworkState>,
    // refreshes the whole data and fetches it from scratch.
    val refresh: () -> Unit,
    // retries any failed requests.
    val retry: () -> Unit
)
