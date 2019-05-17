package qsos.lib.base.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.load.engine.DiskCacheStrategy
import qsos.lib.base.R
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * @author : 华清松
 * @description : 图片加载工具类 使用glide框架封装
 */
object ImageLoaderUtils {

    private var LOADING: Int = R.drawable.bg_primary
    private var ERROR: Int = R.drawable.bg_primary
    private var HEAD: Int = R.drawable.vector_drawable_head

    fun display(context: Context, imageView: ImageView, url: String, loadingRec: Int?, errorRec: Int?) {
        var loading = loadingRec
        var error = errorRec
        if (loading == null) {
            loading = LOADING
        }
        if (error == null) {
            error = ERROR
        }
        GlideApp.with(context)
                .load(url)
                .placeholder(loading)
                .error(error)
                .into(imageView)
    }

    fun display(context: Context, imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        if (url == null) {
            displayError(context, imageView)
            return
        }
        GlideApp.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ERROR)
                .into(imageView)
    }

    fun display(context: Context, imageView: ImageView?, url: String, loadingRec: Int?) {
        var loading = loadingRec
        if (imageView == null) {
            return
        }
        if (loading == null) {
            loading = LOADING
        }
        GlideApp.with(context).load(url)
                .placeholder(loading)
                .error(ERROR)
                .into(imageView)
    }

    /**加载Bitmap图片有占位图片*/
    fun display(context: Context, imageView: ImageView?, bitmap: Bitmap) {
        if (imageView == null) {
            return
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bytes = baos.toByteArray()
        GlideApp.with(context)
                .load(bytes)
                .error(ERROR)
                .into(imageView)
    }

    /**加载GIF图片*/
    fun displayGif(context: Context, imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        if (url == null) {
            displayError(context, imageView)
            return
        }
        GlideApp.with(context)
                .asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ERROR)
                .into(imageView)
    }

    /**加载Bitmap图片*/
    fun displayNoLoading(context: Context, imageView: ImageView, bitmap: Bitmap) {
        val bao = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao)
        val bytes = bao.toByteArray()
        GlideApp.with(context)
                .load(bytes)
                .into(imageView)
    }

    /**加载本地图片*/
    fun display(context: Context, imageView: ImageView?, file: File?) {
        if (imageView == null) {
            return
        }
        GlideApp.with(context).load(file)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ERROR)
                .into(imageView)
    }

    /**加载本地图片并设置尺寸*/
    fun displayReSize(context: Context, imageView: ImageView, url: File, x: Int, y: Int) {
        GlideApp.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ERROR)
                .override(x, y)
                .into(imageView)
    }

    /**加载APP资源图片*/
    fun display(context: Context, imageView: ImageView?, @DrawableRes url: Int) {
        if (imageView == null) {
            return
        }
        GlideApp.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ERROR)
                .into(imageView)
    }

    /**加载APP资源图片*/
    fun display(context: Context, imageView: ImageView?, drawable: Drawable?) {
        if (imageView == null || drawable == null) {
            return
        }
        GlideApp.with(context).load(drawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ERROR)
                .into(imageView)
    }

    /**加载圆形图片*/
    fun displayHeader(context: Context, imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        if (url == null) {
            GlideApp.with(context)
                    .load(HEAD)
                    .transform(GlideCircleTransform())
                    .into(imageView)
        } else {
            GlideApp.with(context)
                    .load(url)
                    .error(HEAD)
                    .transform(GlideCircleTransform())
                    .into(imageView)
        }
    }

    /**加载圆形图片*/
    fun displayHeader(context: Context, imageView: ImageView?, @DrawableRes url: Int?) {
        if (imageView == null || url == null) {
            return
        }
        GlideApp.with(context)
                .load(url)
                .error(ERROR)
                .transform(GlideCircleTransform())
                .into(imageView)
    }

    /**加载圆角图片*/
    fun displayRoundImg(context: Context, url: String?, imageView: ImageView?, round: Float?) {
        if (imageView == null || url == null) {
            return
        }
        GlideApp.with(context)
                .load(url)
                .error(ERROR)
                .transform(GlideRoundTransform(round ?: 8f))
                .into(imageView)
    }

    /**加载错误图片*/
    private fun displayError(context: Context, imageView: ImageView) {
        GlideApp.with(context)
                .load(ERROR)
                .error(ERROR)
                .into(imageView)
    }
}
