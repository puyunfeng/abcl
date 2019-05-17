package qsos.lib.base.base

import android.content.Context
import android.view.View
import qsos.lib.base.data.http.ApiException

/**
 * @author : 华清松
 * @description : View 接口
 */
interface BaseView {
    /**默认界面ID*/
    val defLayoutId: Int

    /**获取上下文*/
    val mContext: Context?

    /** UI 是否存活*/
    val isActive: Boolean

    /**显示消息*/
    fun showToast(msg: String?)

    /**结束当前页面*/
    fun finishThis()

    /**改变基础交互界面，用于处理常规界面交互，如网络请求失败、错误、登录失效等*/
    fun changeBaseView(state: STATE)

    /**配置基础交互页面：网络错误，加载中，没有数据，服务器错误*/
    fun setBaseView(baseNetErrorView: View?, baseDataLoadingView: View?, baseDataNullView: View?, baseNet404View: View?)

    enum class STATE(key: String) {
        NOT_NET("没有网络"),
        NET_ERROR("服务器错误"),
        NOT_FOUND("接口错误"),
        LOADING("加载中"),
        RESULT_NULL("没有数据"),
        OK("正常");

        val state = key
    }

}