package qsos.core.lib.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import qsos.lib.base.base.BaseActivity
import qsos.lib.base.base.BaseView
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 业务模块公共 Activity
 */
@SuppressLint("CheckResult")
abstract class BaseModuleActivity(override var statusBarColor: Int? = null)
    : BaseActivity() {

    private lateinit var baseModelIml: BaseModelIml

    override fun initData(savedInstanceState: Bundle?) {
        baseModelIml = BaseModelIml(BaseModuleRepository())
    }

    override fun initView() {
        baseModelIml.httpNetCode.observe(this, Observer {
            when (it) {
                HttpCode.UNAUTHORIZED -> {
                    changeBaseView(BaseView.STATE.NET_ERROR)
                }
                HttpCode.NETWORK_ERROR -> {
                    changeBaseView(BaseView.STATE.NOT_NET)
                }
                HttpCode.NOT_FOUND -> {
                    changeBaseView(BaseView.STATE.NOT_FOUND)
                }
                HttpCode.HTTP_ERROR -> {
                    changeBaseView(BaseView.STATE.NET_ERROR)
                }
                HttpCode.RESULT_NULL -> {
                    changeBaseView(BaseView.STATE.RESULT_NULL)
                }
                HttpCode.SUCCESS -> changeBaseView(BaseView.STATE.OK)
                HttpCode.LOADING -> changeBaseView(BaseView.STATE.LOADING)
                else -> {
                    changeBaseView(BaseView.STATE.NET_ERROR)
                }
            }
        })
    }

}
