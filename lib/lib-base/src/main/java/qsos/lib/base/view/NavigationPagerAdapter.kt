package qsos.lib.base.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author : 华清松
 * @description : 片段 适配器
 */
class NavigationPagerAdapter(fm: FragmentManager, var list: List<Fragment>)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var titleList: ArrayList<String>? = null

    constructor(fm: FragmentManager, list: List<Fragment>, titleList: ArrayList<String>)
            : this(fm, list) {
        this.titleList = titleList
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (titleList == null) super.getPageTitle(position) else titleList!![position]
    }

}
