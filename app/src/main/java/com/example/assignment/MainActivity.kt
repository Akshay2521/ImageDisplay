package com.example.assignment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.util.LruCache
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var adapter: ImageAdapter
    private lateinit var imageCache: LruCache<String, Bitmap>

    private val imageURLs = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.gridView)
        adapter = ImageAdapter(this, imageURLs)
        gridView.adapter = adapter

        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        imageCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }

        FetchImageUrlsTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class FetchImageUrlsTask : AsyncTask<Void, Void, List<String>>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<String> {
            val url =
                URL("https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100")
            val connection = url.openConnection() as HttpURLConnection
            try {
                val inputStream: InputStream = BufferedInputStream(connection.inputStream)
                val response = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                val urls = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val thumbnailObj = jsonArray.getJSONObject(i).getJSONObject("thumbnail")
                    val imageURL =
                        "${thumbnailObj.getString("domain")}/${thumbnailObj.getString("basePath")}/0/${
                            thumbnailObj.getString("key")
                        }"
                    urls.add(imageURL)
                }
                return urls
            } finally {
                connection.disconnect()
            }
        }

        override fun onPostExecute(result: List<String>?) {
            super.onPostExecute(result)
            if (result != null) {
                imageURLs.addAll(result)
                adapter.notifyDataSetChanged()
            }
        }
    }

//    fun loadBitmapFromCache(url: String): Bitmap? {
//        return imageCache.get(url)
//    }

//    fun addBitmapToCache(url: String, bitmap: Bitmap) {
//        if (loadBitmapFromCache(url) == null) {
//            imageCache.put(url, bitmap)
//        }
//    }
//
//    fun cancelAllImageLoadTasks() {
//        adapter.cancelAllImageLoadTasks()
//    }
//
//    fun getDiskCache(): DiskCache {
//        return DiskCache(this)
//    }

}

