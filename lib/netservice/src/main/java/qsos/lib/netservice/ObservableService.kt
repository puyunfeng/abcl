package qsos.lib.netservice

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.lib.base.data.BaseResult

/**
 * @author 华清松
 * @doc 类说明：统一请求处理方式
 */
object ObservableService {

    /**设置统一的请求处理*/
    fun <T> setObservable(observable: Observable<BaseResult<T>>): Observable<T> {
        return observable.map(ApiResponseFunc())
                .onErrorResumeNext(HttpErrorFunc<T>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> setObservableFlowable(observable: Flowable<T>): Flowable<T> {
        return observable.onErrorResumeNext(HttpErrorFunctionFlowable<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> setFlowableBaseResult(observable: Flowable<BaseResult<T>>): Flowable<T> {
        return observable.map(ApiResponseFunc())
                .onErrorResumeNext(HttpErrorFunctionFlowable<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> setObservableSingle(observable: Single<T>): Single<T> {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}