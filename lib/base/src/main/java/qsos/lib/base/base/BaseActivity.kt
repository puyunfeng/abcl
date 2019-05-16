package qsos.lib.base.base

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.alibaba.android.arouter.launcher.ARouter
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_base.*
import org.reactivestreams.Subscription
import qsos.lib.base.R
import qsos.lib.base.utils.LogUtil
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.activity.ActivityUtils

/**
 * @author : 华清松
 * @description : Base Activity
 */
abstract class BaseActivity : AppCompatActivity(),
        BaseView,
        View.OnClickListener {

    /**统一处理观察行为，在finish时取消行为*/
    var mSubscription: Subscription? = null
    /**Rx方式进行动态权限请求*/
    var mRxPermissions: RxPermissions? = null

    final override var mContext: Context? = null
        protected set(value) {
            field = value
        }

    /**设置视图ID*/
    abstract val layoutId: Int?

    /**状态栏颜色，每个页面不主动修改则默认为主题色*/
    abstract var statusBarColor: Int?

    /**视图重载是否重新加载数据*/
    abstract val reload: Boolean

    /**默认视图ID*/
    override val defLayoutId: Int = R.layout.activity_default

    /**判断当前Activity是否在前台*/
    override var isActive: Boolean = false
        protected set(value) {
            field = value
        }

    /*注意调用顺序*/

    /**初始化数据*/
    abstract fun initData(savedInstanceState: Bundle?)

    /**初始化试图*/
    abstract fun initView()

    /**获取数据*/
    abstract fun getData()

    override fun startActivity(intent: Intent) {
        LogUtil.i("启动:$localClassName")
        super.startActivity(intent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate(bundle: Bundle?) {
        LogUtil.i("创建:$localClassName")
        super.onCreate(bundle)

        mContext = this

        // 全部竖屏显示
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        mRxPermissions = RxPermissions(this)

        ARouter.getInstance().inject(this)

        ActivityUtils.instance.addActivity(this)

        initData(bundle)

        if (layoutId == null) {
            setContentView(defLayoutId)
        } else {
            setContentView(layoutId!!)
            initView()
        }

        /**修改状态栏颜色，默认主题色*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = statusBarColor
                    ?: ContextCompat.getColor(this, R.color.colorPrimary)
        }

        // 统一处理没有网络或网络错误的点击效果，一般为重新获取数据 getData()q
        ll_base?.setOnClickListener(this)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onStart() {
        LogUtil.i("开启:$localClassName")
        super.onStart()
        isActive = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        LogUtil.i("当前:$localClassName")
        super.onResume()
        if (reload) {
            getData()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
        LogUtil.i("暂停:$localClassName")
        super.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun onStop() {
        LogUtil.i("停止:$localClassName")
        super.onStop()
        isActive = false
    }

    override fun finish() {
        LogUtil.i("结束:$localClassName")
        super.finish()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onDestroy() {
        LogUtil.i("销毁:$localClassName")
        super.onDestroy()
        if (mSubscription != null) {
            mSubscription!!.cancel()
        }

        ActivityUtils.instance.finishSingle(this)
    }

    override fun showToast(msg: String?) {
        ToastUtils.showToast(this, if (TextUtils.isEmpty(msg)) "没有信息" else msg!!)
    }

    override fun finishThis() {
        finish()
    }

    /**隐藏软键盘*/
    fun hideKeyboard() {
        val imm: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            if (this.currentFocus?.windowToken != null) {
                imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    /**基础交互页面实体*/
    /**网络错误页面*/
    private var baseNetErrorView: View? = null
    /**数据加载中界面*/
    private var baseDataLoadingView: View? = null
    /**请求数据为空界面*/
    private var baseDataNullView: View? = null
    /**服务器错误界面*/
    private var baseNet404View: View? = null

    /**配置基础交互页面：网络错误，加载中，没有数据，服务器错误*/
    override fun setBaseView(
            baseNetErrorView: View?,
            baseDataLoadingView: View?,
            baseDataNullView: View?,
            baseNet404View: View?) {
        this.baseNetErrorView = baseNetErrorView
        this.baseDataLoadingView = baseDataLoadingView
        this.baseDataNullView = baseDataNullView
        this.baseNet404View = baseNet404View
    }

    override fun changeBaseView(state: BaseView.STATE) {
        ll_base?.removeAllViews()
        ll_base?.isClickable = false
        val baseView: View? = when (state) {
            BaseView.STATE.NOT_NET -> {
                ll_base?.isClickable = true
                baseNetErrorView
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_net_error, ll_base, false)
            }
            BaseView.STATE.LOADING -> {
                ll_base?.isClickable = false
                baseDataLoadingView
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_loading, ll_base, false)
            }
            BaseView.STATE.RESULT_NULL -> {
                ll_base?.isClickable = true
                baseDataNullView
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_null, ll_base, false)
            }
            BaseView.STATE.NET_ERROR -> {
                ll_base?.isClickable = true
                baseNet404View
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_404, ll_base, false)
            }
            BaseView.STATE.NOT_FOUND -> {
                ll_base?.isClickable = true
                baseNet404View
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_404, ll_base, false)
            }
            BaseView.STATE.OK -> {
                null
            }
        }
        if (baseView == null) {
            ll_base?.visibility = View.GONE
        } else {
            ll_base?.visibility = View.VISIBLE
            ll_base?.addView(baseView)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_base -> {
                // 没有网络或网络错误点击是重新获取数据
                changeBaseView(BaseView.STATE.LOADING)
                getData()
            }
        }
    }

}
