package qsos.lib.base.utils

import io.reactivex.Flowable
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor

/**
 * @author : 华清松
 * @description : Rxjava 消息总线工具类。替代 EventBus，用观察者吧。
 */
class RxBus private constructor() {

    private val mRxBus: FlowableProcessor<RxBusEvent<*>> = PublishProcessor.create<RxBusEvent<*>>().toSerialized()

    fun send(obj: RxBusEvent<*>?) {
        LogUtil.i("发送消息------>>>>>> $obj")
        if (obj != null) {
            mRxBus.onNext(obj)
        }
    }

    /**根据传递的 eventType 类型返回特定事件类型的被观察者 */
    fun <T> toFlowable(tClass: Class<T>): Flowable<T> {
        LogUtil.i("绑定消息接收------>>>>>> $tClass")
        return mRxBus.ofType(tClass)
    }

    fun getBus(): Flowable<RxBusEvent<*>> {
        return mRxBus
    }

    fun hasSubscribers(): Boolean {
        return mRxBus.hasSubscribers()
    }

    private object Holder {
        val BUS = RxBus()
    }

    companion object {

        val instance: RxBus
            get() = Holder.BUS
    }

}

interface RxBusEvent<T> {
    fun name(): String
    fun message(): T?
}
