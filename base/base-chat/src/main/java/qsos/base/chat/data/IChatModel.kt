package qsos.base.chat.data

import qsos.lib.base.data.chat.MsgEntity

/**
 * @author : 华清松
 * @description :聊天数据接口
 */
interface IChatModel {

    /**获取消息列表数据*/
    fun getList()

    /**发送消息*/
    fun sendMsg(msgEntity: MsgEntity)

    /**上传消息中的文件*/
    fun uploadFileOfMsg(msgEntity: MsgEntity)
}
