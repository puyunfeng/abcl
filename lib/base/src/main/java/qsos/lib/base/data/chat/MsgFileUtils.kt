package qsos.lib.base.data.chat

import android.text.TextUtils
import qsos.lib.base.BuildConfig
import java.io.File

object MsgFileUtils {

    /**
     * 从待发消息中获取需要上传的文件
     * @param msgEntity 待发送的消息
     */
    fun getFileByMsg(msgEntity: MsgEntity): File? {
        val path = when (msgEntity.typeEnum) {
            MsgTypeEnum.FILE -> msgEntity.data.file!!.path
            MsgTypeEnum.AUDIO -> msgEntity.data.audio!!.path
            MsgTypeEnum.VIDEO -> msgEntity.data.video!!.path
            MsgTypeEnum.IMAGE -> msgEntity.data.img!!.path
            else -> null
        }
        return if (TextUtils.isEmpty(path)) {
            null
        } else {
            try {
                File(path)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * 将上传成功的文件url设置到消息中
     */
    fun setUrlToMsg(url: String, msgEntity: MsgEntity): MsgEntity {
        when (msgEntity.typeEnum) {
            MsgTypeEnum.FILE -> msgEntity.data.file!!.url = BuildConfig.TEST_URL + url
            MsgTypeEnum.AUDIO -> msgEntity.data.audio!!.url = BuildConfig.TEST_URL + url
            MsgTypeEnum.VIDEO -> msgEntity.data.video!!.url = BuildConfig.TEST_URL + url
            MsgTypeEnum.IMAGE -> msgEntity.data.img!!.url = BuildConfig.TEST_URL + url
            else -> {
            }
        }
        return msgEntity
    }

    /**
     * 将下载成功的文件path设置到消息中
     */
    fun setPathToMsg(path: String, msgEntity: MsgEntity): MsgEntity {
        when (msgEntity.typeEnum) {
            MsgTypeEnum.FILE -> msgEntity.data.file!!.path = path
            MsgTypeEnum.AUDIO -> msgEntity.data.audio!!.path = path
            MsgTypeEnum.VIDEO -> msgEntity.data.video!!.path = path
            MsgTypeEnum.IMAGE -> msgEntity.data.img!!.path = path
            else -> {
            }
        }
        return msgEntity
    }

    /**
     * 获取消息中的文件url
     */
    fun getUrlByMsg(msgEntity: MsgEntity): String? {
        return when (msgEntity.typeEnum) {
            MsgTypeEnum.FILE -> msgEntity.data.file!!.url
            MsgTypeEnum.AUDIO -> msgEntity.data.audio!!.url
            MsgTypeEnum.VIDEO -> msgEntity.data.video!!.url
            MsgTypeEnum.IMAGE -> msgEntity.data.img!!.url
            else -> null
        }
    }

    /**
     * 获取消息中的文件path
     */
    fun getPathByMsg(msgEntity: MsgEntity): String? {
        return when (msgEntity.typeEnum) {
            MsgTypeEnum.FILE -> msgEntity.data.file!!.path
            MsgTypeEnum.AUDIO -> msgEntity.data.audio!!.path
            MsgTypeEnum.VIDEO -> msgEntity.data.video!!.path
            MsgTypeEnum.IMAGE -> msgEntity.data.img!!.path
            else -> null
        }
    }
}