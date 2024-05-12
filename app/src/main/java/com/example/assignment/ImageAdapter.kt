package com.example.assignment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


class ImageAdapter(private val context: Context, private val imageUrls: List<String>) :
    BaseAdapter() {

    private val targets = mutableListOf<Target>()

    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun getItem(position: Int): Any {
        return imageUrls[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var imageView = convertView as? ImageView
        if (imageView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Picasso.get().cancelRequest(imageView)

        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                imageView.setImageBitmap(bitmap)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }
        targets.add(target)
        imageView.tag = target
        Picasso.get().load(imageUrls[position]).into(target)

        return imageView
    }

    fun cancelAllImageLoadTasks() {
        for (target in targets) {
            Picasso.get().cancelRequest(target)
        }
        targets.clear()
    }
}