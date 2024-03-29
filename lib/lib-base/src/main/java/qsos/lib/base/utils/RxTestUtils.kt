package qsos.lib.base.utils

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

/**
 * @author : 华清松
 * @description :  Rxjava IO 异步线程转换同步,便于单元测试
 */
object RxTestUtils {
    fun asyncToSync() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }
}
