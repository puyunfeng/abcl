package qsos.base.chat.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.chat_frg_main.*
import qsos.base.chat.R
import qsos.base.chat.data.ChatModelIml
import qsos.base.chat.data.ChatRepository
import qsos.base.chat.view.adapter.ChatAdapter
import qsos.base.chat.view.widget.ChatActionBarView
import qsos.core.file.BaseFileModuleActivity
import qsos.core.file.BaseFileModuleFragment
import qsos.core.file.IFileHelper
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.data.app.FileEntity
import qsos.lib.base.data.chat.MsgEntity
import qsos.lib.base.data.chat.MsgFileUtils
import qsos.lib.base.data.chat.MsgTypeEnum
import qsos.lib.base.routepath.FilePath
import qsos.lib.base.routepath.MapPath
import qsos.lib.base.utils.LogUtil
import qsos.lib.base.utils.ViewModelUtils
import qsos.lib.base.utils.file.ChatMediaPlayer
import qsos.lib.base.utils.file.FileUtils
import java.io.File

/**
 * @author : 华清松
 * @description : 聊天页面
 */
class ChatFragment(override val layoutId: Int = R.layout.chat_frg_main) : BaseFileModuleFragment() {

    override val reload = false

    private lateinit var mChatModel: ChatModelIml
    private lateinit var mAdapter: ChatAdapter

    private var messageListData = arrayListOf<MsgEntity>()
    private var refresh = true

    private var mActivity: BaseFileModuleActivity? = null

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        mChatModel = ViewModelUtils.getModel(this, ChatModelIml(ChatRepository()))

        try {
            mActivity = context as BaseFileModuleActivity
        } catch (e: Exception) {
            mActivity = null
        }
    }

    override fun initView(view: View) {
        mAdapter = ChatAdapter(messageListData, object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View, position: Int, obj: Any?) {
                when (obj) {
                    -1 -> {
                        // 重新发送消息
                        mChatModel.sendMsg(mAdapter.data[position])
                    }
                    0 -> {
                        // todo 判断是语音，进行下列操作
                        // 点击后判断语音文件是否存在，不存在则去下载然后自动播放，存在则直接播放
                        val path = MsgFileUtils.getPathByMsg(mAdapter.data[position])
                        val url = MsgFileUtils.getUrlByMsg(mAdapter.data[position])

                        if (TextUtils.isEmpty(path)) {
                            // 通过网络播放录音
                            ChatMediaPlayer.init(context!!).listener(object : ChatMediaPlayer.PlayerListener {
                                override fun onPlayerStop() {
                                    showToast("播放完")
                                }
                            }).play(ChatMediaPlayer.PlayBuild(context!!, ChatMediaPlayer.PlayType.URL, url!!))
                        } else {
                            // 通过本地播放语音
                            ChatMediaPlayer.init(context!!).listener(object : ChatMediaPlayer.PlayerListener {
                                override fun onPlayerStop() {
                                    showToast("播放完")
                                }
                            }).play(ChatMediaPlayer.PlayBuild(context!!, ChatMediaPlayer.PlayType.PATH, path!!))

                        }
                    }
                }
            }

        })

        val linearLayoutManager = LinearLayoutManager(this.mContext)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        chat_rv.layoutManager = linearLayoutManager
        chat_rv.adapter = mAdapter

        chat_srl?.setOnRefreshListener {
            refresh = false
            getData()
        }?.setOnLoadMoreListener {
            refresh = true
            getData()
        }?.autoLoadMore()

        /**NOTICE 发送消息监听，操作输入前请求权限*/

        /**发送文字，可含表情*/
        chat_action_bar?.setOnSendTextMessageListener(object : ChatActionBarView.OnSendTextMessageListener {
            override fun send(message: String) {
                if (TextUtils.isEmpty(message.trim())) {
                    return
                }
                val msg = MsgEntity(MsgTypeEnum.TEXT, true)
                msg.data.text = message
                messageListData.add(0, msg)
                mAdapter.notifyItemInserted(0)
                chat_rv.scrollToPosition(0)

                mChatModel.sendMsg(msg)
            }
        })

        /**发送语音*/
        chat_action_bar?.setOnAudioStatusListener(object : ChatActionBarView.OnAudioStatusListener {
            @SuppressLint("CheckResult")
            override fun send(status: ChatActionBarView.AudioStatusEnum, path: String) {
                if (ChatActionBarView.AudioStatusEnum.FINISH == status) {
                    // 发送语音信息
                    val file: File? = FileUtils.getFile(path)
                    if (file == null) {
                        showToast("录音失败，请重试")
                        return
                    } else {
                        val msg = MsgEntity(MsgTypeEnum.AUDIO, true)
                        msg.data.audio = MsgEntity.MsgAudioEntity(file.name, path, null)
                        messageListData.add(0, msg)
                        mAdapter.notifyItemInserted(0)
                        chat_rv.scrollToPosition(0)

                        mChatModel.sendMsg(msg)
                    }
                }
            }
        })

        /**发送文件类消息*/
        mActivity?.setOnSendFileListener(object : IFileHelper.OnSendFileListener<FileEntity.FileTypeEnum> {
            override fun send(file: File, type: FileEntity.FileTypeEnum) {
                // 上传文件
                val msg = MsgEntity(MsgTypeEnum.UN_KNOW, true)
                when (type) {
                    FileEntity.FileTypeEnum.IMAGE -> {
                        msg.typeEnum = MsgTypeEnum.IMAGE
                        msg.data.img = MsgEntity.MsgImageEntity(file.name, file.path, null)
                    }
                    FileEntity.FileTypeEnum.VIDEO -> {
                        msg.typeEnum = MsgTypeEnum.VIDEO
                        msg.data.video = MsgEntity.MsgVideoEntity(file.name, file.path, null, null)
                    }
                    else -> {
                        msg.typeEnum = MsgTypeEnum.FILE
                        msg.data.file = MsgEntity.MsgFileEntity(file.name, file.path, null)
                    }
                }
                messageListData.add(0, msg)
                mAdapter.notifyItemInserted(0)
                chat_rv.scrollToPosition(0)

                mChatModel.sendMsg(msg)
            }
        })

        /**发送定位监听*/
        mActivity?.setOnSendLocationListener(object : IFileHelper.OnSendLocationListener {
            override fun send(x: Double, y: Double) {
                val msg = MsgEntity(MsgTypeEnum.LOCATION, true)
                msg.data.location = MsgEntity.MsgLocationEntity(x, y)
                messageListData.add(0, msg)
                mAdapter.notifyItemInserted(0)
                chat_rv.scrollToPosition(0)

                mChatModel.sendMsg(msg)
            }
        })

        /**NOTICE 其它事务操作监听*/

        /**拍照/图片/视频/文件 获取*/
        chat_action_bar?.setOnFileListener(object : ChatActionBarView.OnFileListener {
            override fun getFile(type: ChatActionBarView.FileTypeEnum) {

                when (type) {
                    ChatActionBarView.FileTypeEnum.PHOTO -> {
                        mActivity?.takeCamera(Consumer { result ->
                            mActivity?.uploadImageBeforeZip(FileUtils.getFile(context!!, result.uri), null)
                        })
                    }
                    ChatActionBarView.FileTypeEnum.ALBUM -> {
                        mActivity?.takeGallery(Consumer { result ->
                            mActivity?.uploadImageBeforeZip(FileUtils.getFile(context!!, result.uri), null)
                        })
                    }
                    ChatActionBarView.FileTypeEnum.FILE -> {
                        mActivity?.takeFile(FileUtils.WORD_MIME_TYPES, FileUtils.TAKE_FILE_CODE)
                    }
                    ChatActionBarView.FileTypeEnum.VIDEO -> {
                        mRxPermissions?.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA)
                                ?.subscribe {
                                    if (it) {
                                        ARouter.getInstance().build(FilePath.TAKE_VIDEO).navigation(context as BaseFileModuleActivity, FileUtils.TAKE_VIDEO_CODE)
                                    } else {
                                        showToast("授权失败，无法拍摄视频")
                                    }
                                }
                    }
                    ChatActionBarView.FileTypeEnum.LOCATION -> {
                        ARouter.getInstance().build(MapPath.MAIN).navigation(context as BaseFileModuleActivity, FileUtils.TAKE_LOCATION_CODE)
                    }
                }
            }
        })

        /**NOTICE 绑定数据*/

        /**消息列表数据更新*/
        mChatModel.msgListData.observe(this, Observer<List<MsgEntity>> {
            chat_srl?.finishRefresh()
            chat_srl?.finishLoadMore()

            if (refresh) {
                messageListData.clear()
            }

            messageListData.addAll(it ?: arrayListOf())

            mAdapter.notifyDataSetChanged()
        })

        /**消息中的文件上传成功后刷新对应的消息列表项*/
        mChatModel.dataUploadFileOfMsg.observe(this, Observer { chat ->
            var status = 0
            when {
                chat.fileEntity.progress in -1..99 -> {
                    if (chat.fileEntity.progress == -1) {
                        status = -1
                        showToast("上传失败")
                    }
                }
                else -> {
                    status = 1
                    LogUtil.i("上传中 ${chat.fileEntity.progress} %")
                }
            }
            mAdapter.data.forEach {
                if (it.id == chat.chatEntity.id) {
                    it.status = status
                    it.data = chat.chatEntity.data
                }
            }
            mAdapter.notifyDataSetChanged()
        })

        /**消息发送回调*/
        mChatModel.msgData.observe(this, Observer { chat ->
            mAdapter.data.forEach {
                if (it.id == chat.id) {
                    it.status = chat.status
                }
            }
            mAdapter.notifyDataSetChanged()
        })
        /**获取消息列表*/
        getData()
    }

    override fun getData() {
        mChatModel.getList()
    }

    override fun onPause() {
        // NOTICE 进入后台一定要停止语音播放
        ChatMediaPlayer.stop()
        super.onPause()
    }

    override fun onDestroy() {
        // NOTICE 进入后台一定要释放语音播放
        ChatMediaPlayer.destroy()
        super.onDestroy()
    }
}