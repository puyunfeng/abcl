package qsos.lib.base.data.chat

import qsos.lib.base.data.http.UDFileEntity

/**
 * @author : 华清松
 * @description : 带有文件的消息的实体
 */
data class ChatFileEntity(
        var chatEntity: MsgEntity,
        var fileEntity: UDFileEntity
)