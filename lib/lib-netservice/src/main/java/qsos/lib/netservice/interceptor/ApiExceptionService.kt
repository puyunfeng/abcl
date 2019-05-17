package qsos.lib.netservice.interceptor

import io.reactivex.observers.DisposableObserver
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode

/**
 * @author 华清松
 * @doc 类说明：请求错误处理类
 */
abstract class ApiExceptionService<T> : DisposableObserver<T>() {

    protected abstract fun onError(ex: ApiException)

    override fun onError(e: Throwable) {
        if (e is ApiException) {
            onError(e)
        } else {
            onError(ApiException(e, HttpCode.UNKNOWN))
        }
    }
}
