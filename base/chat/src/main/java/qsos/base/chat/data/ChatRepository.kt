package qsos.base.chat.data

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import qsos.lib.base.data.chat.MsgEntity
import qsos.lib.base.data.chat.MsgFileUtils
import qsos.lib.base.data.chat.MsgTypeEnum
import qsos.lib.base.data.http.UDFileEntity
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.ObservableService
import qsos.lib.netservice.file.BaseRepository
import qsos.lib.netservice.file.FileRepository

/**
 * @author : 华清松
 * @description : 聊天数据获取
 */
@SuppressLint("CheckResult")
class ChatRepository : IChatModel, BaseRepository() {

    val fileRepo = FileRepository()

    override fun sendMsg(msgEntity: MsgEntity) {
        // todo 测试数据
        ObservableService.setObservable(ApiEngine.createService(ApiChat::class.java)
                .sendMsg(msgEntity))
                .subscribe(
                        {
                            dataChat.postValue(it)
                        },
                        {
                            msgEntity.status = -1
                            dataChat.postValue(msgEntity)
                        }
                )
    }

    override fun uploadFileOfMsg(msgEntity: MsgEntity) {
        val path = MsgFileUtils.getPathByMsg(msgEntity)
        val udFileEntity = UDFileEntity(null, null, null, -1)
        udFileEntity.adjoint = msgEntity

        if (TextUtils.isEmpty(path)) {
            fileRepo.dataDownloadFile.value = udFileEntity
        } else {
            udFileEntity.progress = 0
            udFileEntity.path = path
            fileRepo.dataDownloadFile.value = udFileEntity
            fileRepo.uploadFile(udFileEntity)
        }
    }

    override fun getList() {

        val msgList = arrayListOf<MsgEntity>()

        msgList.add(MsgEntity(MsgTypeEnum.TEXT, true))
        msgList.add(MsgEntity(MsgTypeEnum.IMAGE, true))
        msgList.add(MsgEntity(MsgTypeEnum.AUDIO, true))
        msgList.add(MsgEntity(MsgTypeEnum.VIDEO, true))
        msgList.add(MsgEntity(MsgTypeEnum.FILE, true))
        msgList.add(MsgEntity(MsgTypeEnum.INSTR, true))
        msgList.add(MsgEntity(MsgTypeEnum.CARD, true))
        msgList.add(MsgEntity(MsgTypeEnum.NOTICE, true))
        msgList.add(MsgEntity(MsgTypeEnum.LOCATION, true))

        msgList.add(MsgEntity(MsgTypeEnum.TEXT, false))
        msgList.add(MsgEntity(MsgTypeEnum.IMAGE, false))
        msgList.add(MsgEntity(MsgTypeEnum.AUDIO, false))
        msgList.add(MsgEntity(MsgTypeEnum.VIDEO, false))
        msgList.add(MsgEntity(MsgTypeEnum.FILE, false))
        msgList.add(MsgEntity(MsgTypeEnum.INSTR, false))
        msgList.add(MsgEntity(MsgTypeEnum.CARD, false))
        msgList.add(MsgEntity(MsgTypeEnum.NOTICE, false))
        msgList.add(MsgEntity(MsgTypeEnum.LOCATION, false))

        msgList.add(MsgEntity(MsgTypeEnum.INSTR, true))
        msgList.add(MsgEntity(MsgTypeEnum.CARD, true))
        msgList.add(MsgEntity(MsgTypeEnum.NOTICE, true))
        msgList.add(MsgEntity(MsgTypeEnum.LOCATION, true))

        msgList.add(MsgEntity(MsgTypeEnum.TEXT, false))
        msgList.add(MsgEntity(MsgTypeEnum.IMAGE, false))
        msgList.add(MsgEntity(MsgTypeEnum.AUDIO, false))
        msgList.add(MsgEntity(MsgTypeEnum.VIDEO, false))

        msgList.add(MsgEntity(MsgTypeEnum.INSTR, false))
        msgList.add(MsgEntity(MsgTypeEnum.CARD, false))
        msgList.add(MsgEntity(MsgTypeEnum.NOTICE, false))
        msgList.add(MsgEntity(MsgTypeEnum.LOCATION, false))

        msgList.add(MsgEntity(MsgTypeEnum.INSTR, true))
        msgList.add(MsgEntity(MsgTypeEnum.CARD, true))
        msgList.add(MsgEntity(MsgTypeEnum.NOTICE, true))
        msgList.add(MsgEntity(MsgTypeEnum.LOCATION, true))

        msgList.add(MsgEntity(MsgTypeEnum.TEXT, false))
        msgList.add(MsgEntity(MsgTypeEnum.IMAGE, false))
        msgList.add(MsgEntity(MsgTypeEnum.AUDIO, false))
        msgList.add(MsgEntity(MsgTypeEnum.VIDEO, false))

        dataChatList.postValue(msgList)

    }

    val dataChat = MutableLiveData<MsgEntity>()
    val dataChatList = MutableLiveData<List<MsgEntity>>()

}