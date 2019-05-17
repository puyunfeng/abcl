package qsos.base.chat.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import qsos.lib.base.data.chat.ChatFileEntity
import qsos.lib.base.data.chat.MsgEntity
import qsos.lib.base.data.chat.MsgTypeEnum

/**
 * @author : 华清松
 * @description : 聊天数据实现
 */
class ChatModelIml(
        private val chatRepo: ChatRepository
) : ViewModel() {

    fun getList() {
        chatRepo.getList()
    }

    fun sendMsg(msgEntity: MsgEntity) {

        when (msgEntity.typeEnum) {
            MsgTypeEnum.TEXT -> {
                chatRepo.sendMsg(msgEntity)
            }
            MsgTypeEnum.LOCATION -> {
                chatRepo.sendMsg(msgEntity)
            }
            else -> {
                // NOTICE 需要上传文件的消息，还应上传消息，此处不再做业务功能
                chatRepo.uploadFileOfMsg(msgEntity)
            }
        }
    }

    var msgData: LiveData<MsgEntity> = Transformations.map(chatRepo.dataChat) { it }

    var msgListData: LiveData<List<MsgEntity>> = Transformations.map(chatRepo.dataChatList) { it }

    var dataUploadFileOfMsg: LiveData<ChatFileEntity> = Transformations.map(chatRepo.fileRepo.dataUploadFile) {
        ChatFileEntity(it.adjoint as MsgEntity, it)
    }

}