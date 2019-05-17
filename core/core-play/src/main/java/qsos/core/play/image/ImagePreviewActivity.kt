package qsos.core.play.image

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.play_activity_image.*
import qsos.core.play.R
import qsos.lib.base.base.BaseNormalActivity
import qsos.lib.base.routepath.PlayPath
import qsos.lib.base.utils.image.BitmapUtils

/**
 * @author : 华清松
 * @description : 图片预览界面
 */
@SuppressLint("CheckResult", "SetTextI18n")
@Route(group = PlayPath.GROUP, path = PlayPath.IMAGE_PREVIEW)
class ImagePreviewActivity(override val layoutId: Int? = R.layout.play_activity_image)
    : BaseNormalActivity(), ScalableCardHelper.OnPageChangeListener {

    @Autowired(name = PlayPath.IMAGE_LIST)
    @JvmField
    var imageListDataString: String? = null

    private var imageListData: FileListData? = null

    private var mAdapter: ImageAdapter? = null
    private var errorDrawable: Drawable? = null

    override fun initData(savedInstanceState: Bundle?) {
        imageListData = Gson().fromJson(imageListDataString, FileListData::class.java)

        errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_primary)
    }

    override fun initView() {
        val fadeIn = AlphaAnimation(0.5f, 1f)
        fadeIn.duration = 500
        val fadeOut = AlphaAnimation(0.5f, 0f)
        fadeOut.duration = 500
        play_image_is.inAnimation = fadeIn
        play_image_is.outAnimation = fadeOut
        play_image_is.setFactory {
            val imageView = ImageView(this)
            imageView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            imageView
        }

        play_image_rv.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)

        mAdapter = ImageAdapter(imageListData!!.imageList)
        play_image_rv.adapter = mAdapter

        val cardHelper = ScalableCardHelper(this)
        cardHelper.attachToRecyclerView(play_image_rv)

        play_image_rv.scrollToPosition(imageListData!!.position)

    }

    override fun getData() {}

    private var mBlurBitmap = arrayOf<Bitmap?>()

    /**翻页监听*/
    override fun onPageSelected(position: Int) {
        val size = imageListData!!.imageList.size

        play_image_position?.text = " ${position + 1} / $size "
        play_image_name?.text = imageListData!!.imageList[position].name

        if (mBlurBitmap.isEmpty()) {
            mBlurBitmap = arrayOfNulls(imageListData!!.imageList.size)
        }

        var bitmap = mBlurBitmap[position]
        if (bitmap == null) {
            Observable.create<Bitmap> {
                bitmap = Glide.with(applicationContext).asBitmap()
                        .load(imageListData!!.imageList[position].url)
                        .error(R.drawable.bg_primary)
                        .submit()
                        .get()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    bitmap = BitmapUtils.blurBitmap(this, bitmap!!, 25f)
                }
                it.onNext(bitmap!!)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                mBlurBitmap[position] = bitmap
                                play_image_is.setImageDrawable(BitmapDrawable(this.resources, it))
                            },
                            {
                                mBlurBitmap[position] = null
                                play_image_is.setImageDrawable(errorDrawable)
                            })
        } else {
            play_image_is.setImageDrawable(BitmapDrawable(this.resources, bitmap))
        }
    }

    override fun finish() {
        mAdapter?.releaseAllHolder(play_image_rv)
        super.finish()
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
    }
}