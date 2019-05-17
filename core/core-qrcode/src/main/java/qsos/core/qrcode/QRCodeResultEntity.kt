package qsos.core.qrcode

import qsos.lib.base.utils.RxBusEvent

/**
 * @author : 华清松
 * @description : 二维码结果实体
 */
data class QRCodeResultEntity(val key: String, val value: String?) : RxBusEvent<String> {

    override fun name(): String {
        return key
    }

    override fun message(): String? {
        return value
    }
}