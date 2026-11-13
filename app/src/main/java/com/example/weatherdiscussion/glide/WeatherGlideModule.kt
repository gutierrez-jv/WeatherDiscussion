package com.example.weatherdiscussion.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.weatherdiscussion.WeatherApp
import java.io.InputStream

class WeatherGlideModule : AppGlideModule(){

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, "glide", 100L * 1024 * 1024))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = (context.applicationContext as WeatherApp).container.baseOKtHttp
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))

    }
    override fun isManifestParsingEnabled() = false


}