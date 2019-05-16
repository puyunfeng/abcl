package qsos.app.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.app_activity_main.*
import qsos.app.R
import qsos.core.file.BaseFileModuleActivity
import qsos.lib.base.routepath.AppPath
import qsos.lib.base.utils.activity.ActivityUtils
import qsos.lib.base.view.NavigationPagerAdapter

/**
 * @author : 华清松
 * @description : 主界面
 */
@Route(group = AppPath.GROUP, path = AppPath.MAIN)
class MainActivity : BaseFileModuleActivity() {

    private var viewList = arrayListOf<Fragment>()
    private var mTitles = arrayListOf<String>()

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        ActivityUtils.instance.addActivity(this)
        ActivityUtils.instance.finishAllButNotMe(this)
    }

    override fun initView() {
        super.initView()

        vp_main.adapter = NavigationPagerAdapter(supportFragmentManager, viewList, mTitles)
        vp_main.offscreenPageLimit = viewList.size

        tl_main.setupWithViewPager(vp_main)

        getData()
    }

    override fun getData() {
    }

    override val layoutId: Int = R.layout.app_activity_main
    override val reload: Boolean = true
}
