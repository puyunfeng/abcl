package qsos.lib.base.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_base.*
import qsos.lib.base.R
import qsos.lib.base.utils.LogUtil

/**
 * @author : 华清松
 * @description : Base Fragment
 */
@SuppressLint("SetTextI18n")
abstract class BaseFragment : Fragment(), BaseView, View.OnClickListener {
    /**Rx方式进行动态权限请求*/
    var mRxPermissions: RxPermissions? = null

    override var isActive: Boolean = false
        protected set(value) {
            field = value
        }

    override var mContext: Context? = null
        protected set(value) {
            field = value
        }

    private var mainView: View? = null

    /**设置视图ID*/
    abstract val layoutId: Int?

    /**视图重载是否重新加载数据*/
    abstract val reload: Boolean

    override val defLayoutId: Int
        get() = R.layout.fragment_default

    private var isFirst: Boolean = false

    /*注意调用顺序*/

    /**初始化数据*/
    abstract fun initData(savedInstanceState: Bundle?)

    /**初始化视图*/
    abstract fun initView(view: View)

    /**获取数据*/
    abstract fun getData()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        initData(bundle)
        mRxPermissions = RxPermissions(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        LogUtil.i("创建:${javaClass.name}")

        mContext = context

        mainView = if (layoutId == null) {
            inflater.inflate(defLayoutId, container, false)
        } else {
            inflater.inflate(layoutId!!, container, false)
        }

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // kotlin 务必在此进行 initView 操作，否则将出现空指针异常
        initView(view)

        ll_base?.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        // 页面重现，重新加载数据
        if (reload) {
            getData()
        }
    }

    override fun finishThis() {
        (mContext as Activity).finish()
    }

    override fun showToast(msg: String?) {
        Toast.makeText(mContext, if (TextUtils.isEmpty(msg)) "没有信息" else msg, Toast.LENGTH_SHORT).show()
    }

    fun setFirst() {
        isFirst = true
    }

    /**隐藏输入键盘*/
    fun hideKeyboard() {
        val imm: InputMethodManager = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            if (activity?.currentFocus?.windowToken != null) {
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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
        val baseView: View? = when (state) {
            BaseView.STATE.NOT_NET -> {
                baseNetErrorView
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_net_error, ll_base, false)
            }
            BaseView.STATE.LOADING -> {
                baseDataLoadingView
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_loading, ll_base, false)
            }
            BaseView.STATE.RESULT_NULL -> {
                baseDataNullView
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_null, ll_base, false)
            }
            BaseView.STATE.NET_ERROR -> {
                baseNet404View
                        ?: LayoutInflater.from(mContext).inflate(R.layout.activity_base_404, ll_base, false)
            }
            BaseView.STATE.NOT_FOUND -> {
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
