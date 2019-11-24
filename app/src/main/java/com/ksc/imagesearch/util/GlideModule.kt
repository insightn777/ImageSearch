package com.ksc.imagesearch.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.module.LibraryGlideModule

@GlideModule
class GlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        // example : registry.append(Photo.class, InputStream.class, new FlickrModelLoader.Factory())
    }
}

@GlideModule
class OkHttpLibraryGlideModule: LibraryGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        // example : registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory())
    }
}