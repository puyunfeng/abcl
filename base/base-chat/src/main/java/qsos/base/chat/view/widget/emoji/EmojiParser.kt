package qsos.base.chat.view.widget.emoji

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ImageSpan
import qsos.lib.base.utils.image.BitmapUtils
import qsos.base.chat.R
import java.io.IOException
import java.io.InputStream
import java.util.regex.Pattern

/**
 * @author : 华清松
 * @description : 表情处理类
 */
class EmojiParser private constructor(activity: Activity) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: EmojiParser? = null

        fun getInstance(mActivity: Activity) =
                instance ?: synchronized(this) {
                    instance ?: EmojiParser(mActivity).also { instance = it }
                }
    }

    private val mActivity = activity
    private val mPattern: Pattern
    /**表情尺寸*/
    private var iconSize: Int = 30
    /**表情图标集合*/
    var mPageListData = arrayListOf<EmojiEntity>()
    var mEmojiMap = hashMapOf<String, EmojiEntity>()

    /**将带有表情的文本转化为表情显示*/
    fun setText(text: String?): CharSequence {
        if (TextUtils.isEmpty(text)) return ""
        val builder = SpannableStringBuilder(text)
        val pattern = "\\[/.*?]"

        val r = Pattern.compile(pattern)
        val m = r.matcher(text)
        while (m.find()) {
            val iconName = text!!.substring(m.start(), m.end())
            val icon = getEmojiByName(iconName)
            if (icon != null) {
                builder.setSpan(ImageSpan(mActivity, icon), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return builder
    }

    /**通过名称获取表情对象*/
    private fun getEmojiByName(name: String): Bitmap? {
        return mEmojiMap[name]?.icon
    }

    /**从Assets中读取图片并重置大小*/
    private fun getImageFromAssets(fileName: String): Bitmap? {
        var image: Bitmap?
        val am = mActivity.resources.assets
        try {
            val `is` = am.open(fileName)
            image = decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        // 通过像素，获取表情布局的最大限制尺寸，然后等像素缩放
        image = BitmapUtils.zoomToFixShape(image!!, iconSize)
        return image
    }

    /**解码图片流*/
    private fun decodeStream(picStream: InputStream): Bitmap? {
        val bitmap = BitmapFactory.decodeStream(picStream)
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = 4
        return bitmap
    }

    /**根据文本替换成图片*/
    fun setEmojiSpansToEdit(emoji: EmojiEntity): CharSequence {
        val builder = SpannableStringBuilder(emoji.name)
        val matcher = mPattern.matcher(emoji.name)
        while (matcher.find()) {
            val icon = emoji.icon
            if (icon != null) {
                builder.setSpan(ImageSpan(mActivity, icon), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return builder
    }

    init {
        // 初始化英寸数
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.windowManager.defaultDisplay.getRealSize(point)
        }
        val displayMetrics = activity.resources.displayMetrics
        val density: Double = displayMetrics.density.toDouble()
        iconSize = when (density) {
            0.75 -> 30
            1.0 -> 32
            else -> {
                when {
                    displayMetrics.widthPixels < 640 -> 32
                    else -> 50
                }
            }
        }
        // 表情图标名称获取，通过名称获取表情 bitmap
        val smileyIconNames = mActivity.resources.getStringArray(R.array.chat_smiley_names)
        // 表情名称获取,通过名称对应表情 bitmap
        val smileTexts = mActivity.resources.getStringArray(R.array.chat_smiley_texts)
        // 初始化每页表情列表
        mPageListData.clear()
        for ((index, name) in smileTexts.withIndex()) {
            val emoji = EmojiEntity(name, getImageFromAssets("emoji/${smileyIconNames[index]}.png"))
            mPageListData.add(emoji)
            mEmojiMap[name] = emoji
        }
        // 构建正则表达式
        mPattern = buildPattern(mPageListData)
    }

    /**构建正则表达式，存储表情转化规则*/
    private fun buildPattern(mSmileTextListData: ArrayList<EmojiEntity>): Pattern {
        val patternString = StringBuilder(mSmileTextListData.size * 3)
        patternString.append('(')
        for (s in mSmileTextListData) {
            patternString.append(Pattern.quote(s.name.toString()))
            patternString.append('|')
        }
        patternString.replace(patternString.length - 1, patternString.length, ")")
        return Pattern.compile(patternString.toString())
    }

}
