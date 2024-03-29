package qsos.lib.base.utils

import qsos.lib.base.BuildConfig
import timber.log.Timber

/**
 * @author 华清松
 * @doc 类说明：日志打印工具
 */
object LogUtil {

    var isOpen = true
    var TAG = BuildConfig.APPLICATION_ID

    init {
        Timber.tag(TAG)
    }

    fun e(msg: String) {
        if (isOpen) {
            Timber.e(msg)
        }
    }

    fun w(msg: String) {
        if (isOpen) {
            Timber.w(msg)
        }
    }

    fun i(msg: String) {
        if (isOpen) {
            Timber.i(msg)
        }
    }

    fun d(msg: String) {
        if (isOpen) {
            Timber.d(msg)
        }
    }

    fun v(msg: String) {
        if (isOpen) {
            Timber.v(msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (isOpen) {
            Timber.tag(tag).e(msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (isOpen) {
            Timber.tag(tag).w(msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (isOpen) {
            Timber.tag(tag).i(msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (isOpen) {
            Timber.tag(tag).d(msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (isOpen) {
            Timber.tag(tag).v(msg)
        }
    }
}
