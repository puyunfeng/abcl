package qsos.lib.netservice

import io.reactivex.Observable
import io.reactivex.functions.Function
import qsos.lib.base.data.http.ApiException

/**
 * @author 华清松
 * @doc 类说明：网络请求异常处理
 */
class HttpErrorFunc<T> : Function<Throwable, Observable<T>> {

    @Throws(ApiException::class)
    override fun apply(throwable: Throwable): Observable<T> {
        return Observable.error(ApiExceptionServiceImpl.handleException(throwable))
    }

}