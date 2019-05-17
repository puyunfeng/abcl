package qsos.base.chat.data

import io.reactivex.Observable
import qsos.lib.base.data.BaseResult
import qsos.lib.base.data.chat.MsgEntity
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author : 华清松
 * @description : 聊天接口
 */
interface ApiChat {

    /**发送聊天消息*/
    @POST("/chat/send")
    fun sendMsg(
            @Body connectId: MsgEntity
    ): Observable<BaseResult<MsgEntity>>

}