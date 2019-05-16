package qsos.lib.netservice

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author 华清松
 * @doc 使用场景： JUNIT 进行测试时模拟网络请求返回
 */
object ObservableDecorator {

    fun <T> decorate(observable: Observable<T>): Observable<T> {
        return if (ApiEngine.isUnitTest) {
            // 当前线程同步执行
            observable.subscribeOn(Schedulers.trampoline()).observeOn(Schedulers.trampoline())
        } else {
            observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .delay(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
        }
    }
}
