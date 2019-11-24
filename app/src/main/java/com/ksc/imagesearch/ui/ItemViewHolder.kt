package com.ksc.imagesearch.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksc.imagesearch.R
import com.ksc.imagesearch.data.ImageSearchResponse
import com.ksc.imagesearch.util.GlideRequests
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class ItemViewHolder(val view: View, private val glide: GlideRequests)
    : RecyclerView.ViewHolder(view)
{
    private var post : ImageSearchResponse.Document? = null
    init {
        view.setOnClickListener {
            post?.doc_url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(post: ImageSearchResponse.Document?) {
        this.post = post
        view.minimumHeight = view.image_View.width/(post?.width ?: 0)*(post?.height ?: 0)
        Log.e("width","${view.image_View.width}")
        glide.load(post?.image_url)
//            .override(post?.width ?: 500,post?.height ?: 500)
            .fallback(R.drawable.ic_launcher_foreground)
            .into(view.findViewById(R.id.image_View))
        // size 안정해주면 자동으로 되긴 하는데 나중에 업스크롤링할때 사이즈값 없어서 맨위로 순간이동 하게 됨
        // https://stackoverflow.com/questions/33589365/recyclerview-scrolls-to-the-top-itself
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
            return ItemViewHolder(view, glide)
        }
    }
}