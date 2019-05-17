package qsos.core.lib.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import qsos.lib.base.base.BaseFragment
import qsos.lib.base.base.BaseView
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 业务模块公共 Fragment
 */
@SuppressLint("CheckResult")
abstract class BaseModuleFragment : BaseFragment() {

    private lateinit var baseModelIml: BaseModelIml

    override fun initData(savedInstanceState: Bundle?) {
        baseModelIml = BaseModelIml(BaseModuleRepository())
    }

    override fun initView(view: View) {
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