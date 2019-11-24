package com.ksc.imagesearch.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ksc.imagesearch.R
import com.ksc.imagesearch.data.ImageSearchResponse
import com.ksc.imagesearch.repository.NetworkState
import com.ksc.imagesearch.util.GlideRequests

class ItemsAdapter(
    private val glide: GlideRequests,
    private val retryCallback: () -> Unit
) : PagedListAdapter<ImageSearchResponse.Document, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.recyclerview_item -> ItemViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (getItemViewType(position)) {
            R.layout.recyclerview_item -> (holder as ItemViewHolder).bind(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(networkState)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.recyclerview_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {   // networkstate 가 변했다면
            if (hadExtraRow) {              // 이전에 loading?
                notifyItemRemoved(super.getItemCount())     // loaded 니까 끝에 NetworkItemView 제거
            } else {
                notifyItemInserted(super.getItemCount())    // loading 이니 끝에 NetworkItemView 추가
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)    // 마지막칸 아이템 바꿔줌
        }
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<ImageSearchResponse.Document>() {
            override fun areContentsTheSame(oldItem: ImageSearchResponse.Document, newItem: ImageSearchResponse.Document): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ImageSearchResponse.Document, newItem: ImageSearchResponse.Document): Boolean =
                oldItem.image_url == newItem.image_url
        }
    }
}