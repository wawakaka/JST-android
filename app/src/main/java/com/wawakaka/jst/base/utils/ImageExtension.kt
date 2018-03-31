package com.wawakaka.jst.base.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wawakaka.jst.base.model.LoadImageFailed
import com.wawakaka.jst.base.model.LoadImageSuccess

/**
 * Created by babang on 3/25/2018.
 */
fun ImageView.setImage(url: String, context: Context) {
    Glide
        .with(context)
        .load(url)
        .into(this)
}

fun ImageView.setImage(url: String, context: Context, callback: RequestListener<Drawable>) {
    Glide
        .with(context)
        .load(url)
        .listener(callback)
        .into(this)
}

fun ImageView.setImageWithListener(url: String, context: Context, TAG: String) {
    Glide
        .with(context)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                LogUtils.debug(TAG, "e " + e.toString())
                LogUtils.debug(TAG, "model " + model.toString())
                LogUtils.debug(TAG, "target " + target.toString())
                LogUtils.debug(TAG, "isFirstResource " + isFirstResource.toString())
                RxBus.post(LoadImageFailed(TAG))
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                LogUtils.debug(TAG, "resource " + resource.toString())
                LogUtils.debug(TAG, "model " + model.toString())
                LogUtils.debug(TAG, "target " + target.toString())
                LogUtils.debug(TAG, "dataSource " + dataSource.toString())
                LogUtils.debug(TAG, "isFirstResource " + isFirstResource.toString())
                RxBus.post(LoadImageSuccess(TAG))
                return false
            }
        })
        .into(this)
}