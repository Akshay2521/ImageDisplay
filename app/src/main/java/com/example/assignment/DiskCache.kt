package com.example.assignment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*

class DiskCache(context: Context) {
    private val cacheDir: File = context.cacheDir

    fun writeBitmapToDisk(url: String, bitmap: Bitmap) {
        val fileName = getFileName(url)
        val file = File(cacheDir, fileName)
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readBitmapFromDisk(url: String): Bitmap? {
        val fileName = getFileName(url)
        val file = File(cacheDir, fileName)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    private fun getFileName(url: String): String {
        return url.hashCode().toString()
    }
}