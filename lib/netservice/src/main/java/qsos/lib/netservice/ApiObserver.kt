package qsos.lib.netservice

import qsos.lib.base.data.http.ApiException
import qsos.lib.netservice.interceptor.ApiExceptionService
import timber.log.Timber

/**
 * @author 华清松
 * @doc 类说明：请求回调处理
 */
open class ApiObserver<T> : ApiExceptionService<T>() {

    override fun onNext(data: T) {

        Timber.tag("网络请求：").i("onNext: 请求结果= ${data.toString()}")
    }

    override fun onError(ex: ApiException) {

        ex.printStackTrace()
        Timber.tag("网络请求：").e("onError: 请求异常 ${ex}")
    }

    override fun onComplete() {

        Timber.tag("网络请求：").e("onComplete: 处理完毕")
    }

    override fun onStart() {

        Timber.tag("网络请求：").e("onStart: 开始请求")
    }

}
