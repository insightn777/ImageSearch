package com.ksc.imagesearch.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.ksc.imagesearch.repository.ItemRepository

class ItemViewModel(private val repository: ItemRepository) : ViewModel()
{
    private val query = MutableLiveData<String>()
    private val repoResult = map(query) {
        repository.search(it)
    }

    val posts = Transformations.switchMap(repoResult) { it.pagedList }
    val networkState = Transformations.switchMap(repoResult) { it.networkState }
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun searchImage(query: String): Boolean {
        if (this.query.value == query) {
            return false
        }
        this.query.value = query
        return true
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }
}