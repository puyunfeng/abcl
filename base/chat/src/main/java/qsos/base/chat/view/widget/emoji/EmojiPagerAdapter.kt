package qsos.base.chat.view.widget.emoji

import android.os.Parcelable
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View

/**
 * @author : 华清松
 * @description : 表情页滑动适配器
 */
class EmojiPagerAdapter internal constructor(private var mListViews: List<View>) : PagerAdapter() {

    override fun destroyItem(arg0: View, arg1: Int, arg2: Any) {
        (arg0 as ViewPager).removeView(mListViews[arg1])
    }

    override fun finishUpdate(arg0: View) {
    }

    override fun getCount(): Int {
        return mListViews.size
    }

    override fun instantiateItem(arg0: View, arg1: Int): Any {
        (arg0 as ViewPager).addView(mListViews[arg1], 0)
        return mListViews[arg1]
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    override fun restoreState(arg0: Parcelable?, arg1: ClassLoader?) {
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun startUpdate(arg0: View) {
    }
}
