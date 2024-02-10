package hello.kwfriends.image

import android.content.Context
import coil.ImageLoader
import coil.memory.MemoryCache

object ImageLoaderFactory {
    private var imageLoader: ImageLoader? = null

    fun getInstance(context: Context): ImageLoader {
        if (imageLoader == null) {
            imageLoader = ImageLoader.Builder(context)
                .memoryCache {
                    MemoryCache.Builder(context).maxSizePercent(0.25).build()
                }
                .crossfade(true)
                .build()
        }
        return imageLoader!!
    }
}