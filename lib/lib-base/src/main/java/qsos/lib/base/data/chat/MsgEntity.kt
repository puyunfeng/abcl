package qsos.lib.base.data.chat

import qsos.lib.base.data.BaseEntity
import qsos.lib.base.data.app.FileEntity
import java.util.*

/**
 * @author : 华清松
 * @description : 消息的实体
 */
data class MsgEntity(
        /**消息类型*/
        var typeEnum: MsgTypeEnum,
        /**是否为当前用户发送的消息*/
        var byMe: Boolean
) : BaseEntity {
    /**消息ID*/
    var id: String = UUID.randomUUID().toString()
    /**消息描述*/
    var desc: String? = "描述内容"
    /**是否已读*/
    var read: Boolean = false
    /**消息发送人昵称*/
    var nickname: String = "昵称"
    /**消息发送人头像链接*/
    var headUrl: String? = "http://img.mp.sohu.com/upload/20170710/18931bc763024d7cbd105e1b46804446.png"
    /**消息发送时间*/
    var sendTime: Date? = Date()
    /**消息发送与接收状态
     * -1:错误，可重试
     * 0:正常
     * 1:发送/接收中*/
    var status: Int = 0
    /**消息内容*/
    var data: ContentData = ContentData(null, null, null, null, null, null, null, null, null, null)

    init {
        this.data.text = "测试文本"
        this.data.img = MsgImageEntity("测试图片", "", "http://img.mp.sohu.com/upload/20170710/18931bc763024d7cbd105e1b46804446.png")
        this.data.file = MsgFileEntity("测试文件", null, "http://resource.qsos.vip/test.mp4")
        this.data.audio = MsgAudioEntity("测试语音", "", "http://resource.qsos.vip/test.wav")
        this.data.video = MsgVideoEntity("测试视频", "", "http://resource.qsos.vip/test.mp4", "http://img.mp.itc.cn/upload/20160820/d2e412453e5b43948c5c835a6ecc13f2_th.gif")
        this.data.instr = MsgInstrEntity("测试指令", "")
        this.data.card = MsgCardEntity("", "")
        this.data.task = MsgTaskEntity("我的任务")
        this.data.notice = MsgNoticeEntity("你好")
        this.data.location = MsgLocationEntity(32.3, 32.3)
    }

    /**消息内容*/
    data class ContentData constructor(
            var text: String?,
            var img: MsgImageEntity?,
            var file: MsgFileEntity?,
            var audio: MsgAudioEntity?,
            var video: MsgVideoEntity?,
            var instr: MsgInstrEntity?,
            var card: MsgCardEntity?,
            var task: MsgTaskEntity?,
            var notice: MsgNoticeEntity?,
            var location: MsgLocationEntity?
    )

    /**消息-图片实体*/
    class MsgImageEntity(mName: String?, path: String?, mUrl: String?) : FileEntity() {
        init {
            this.name = mName
            this.path = path
            this.url = mUrl
        }
    }

    /**消息-文件实体*/
    class MsgFileEntity(name: String?, path: String?, url: String?) : FileEntity() {
        init {
            this.id = UUID.randomUUID().toString()
            this.name = name
            this.path = path
            this.url = url
        }
    }

    /**消息-语音实体*/
    class MsgAudioEntity(mName: String?, path: String?, mUrl: String?) : FileEntity() {
        init {
            this.name = mName
            this.path = path
            this.url = mUrl
        }
    }

    /**消息-视频实体*/
    class MsgVideoEntity(mName: String?, path: String?, mUrl: String?, mCoverUrl: String?) : FileEntity() {
        /**封面链接*/
        var coverUrl: String? = null

        init {
            this.name = mName
            this.path = path
            this.url = mUrl
            this.coverUrl = mCoverUrl
        }
    }

    /**消息-指令实体*/
    class MsgInstrEntity(mName: String?, mUrl: String?) {
        var id: Int? = null
        var name: String? = mName
        var url: String? = mUrl
    }

    /**消息-名片实体*/
    class MsgCardEntity(mName: String?, mUrl: String?) {
        var id: Int? = null
        var name: String? = mName
        var url: String? = mUrl
    }

    /**消息-任务实体*/
    class MsgTaskEntity(mName: String?) {
        var id: Int? = null
        var name: String? = mName
    }

    /**消息-提醒实体*/
    class MsgNoticeEntity(mName: String?) {
        var id: Int? = null
        var name: String? = mName
    }

    /**消息-位置实体*/
    class MsgLocationEntity(mx: Double?, my: Double?) {
        var id: Int? = null
        var name: String? = "未知位置"
        var x: Double? = mx
        var y: Double? = my
    }
}
