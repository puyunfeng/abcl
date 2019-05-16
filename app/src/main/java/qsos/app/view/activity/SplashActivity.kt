package qsos.app.view.activity

import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import qsos.app.R
import qsos.core.lib.view.BaseModuleActivity
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.routepath.AppPath
import qsos.lib.base.routepath.BasePath
import qsos.lib.base.utils.activity.ActivityUtils

/**
 * @author : 华清松
 * @description : 闪屏界面
 */
@Route(group = AppPath.GROUP, path = AppPath.SPLASH)
class SplashActivity : BaseModuleActivity() {

    override val layoutId = R.layout.app_activity_splash
    override val reload = false

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        ActivityUtils.instance.addActivity(this)
    }

    override fun initView() {
        super.initView()

        ActivityUtils.instance.finishAllButNotMe(this)
        statusBarColor = ContextCompat.getColor(this, R.color.white)

        mHandler.sendEmptyMessageDelayed(0, 800)
    }

    override fun getData() {

    }

    private val mHandler = Handler {
        while (BaseApplication.buildFinish) {
            // FIXME AppPath.MAIN
            ARouter.getInstance().build(BasePath.MAIN).navigation()
            finishThis()
            break
        }
        return@Handler true
    }
}