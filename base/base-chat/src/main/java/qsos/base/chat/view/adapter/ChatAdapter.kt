package qsos.base.chat.view.adapter

import android.text.TextUtils
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityOptionsCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.chat_item_image.view.*
import qsos.base.chat.R
import qsos.base.chat.view.holder.*
import qsos.core.play.image.FileData
import qsos.core.play.image.FileListData
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.data.chat.MsgEntity
import qsos.lib.base.data.chat.MsgTypeEnum
import qsos.lib.base.routepath.FilePath
import qsos.lib.base.routepath.MapPath
import qsos.lib.base.routepath.PlayPath
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.file.FileUtils

/**
 * @author : 华清松
 * @description : 消息列表容器
 */
class ChatAdapter(data: ArrayList<MsgEntity>, onItemClickListener: OnRecyclerViewItemClickListener) : BaseAdapter<MsgEntity>(data) {

    private val mOnItemClickListener = onItemClickListener

    override fun getItemViewType(position: Int): Int {
        return data[position].typeEnum.ordinal
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<MsgEntity> {
        return when (getType(viewType)) {
            R.layout.chat_item_notice -> NoticeViewHolder(view)
            R.layout.chat_item_text -> TextViewHolder(view)
            R.layout.chat_item_image -> ImageViewHolder(view)
            R.layout.chat_item_audio -> AudioViewHolder(view)
            R.layout.chat_item_video -> VideoViewHolder(view)
            R.layout.chat_item_file -> FileViewHolder(view)
            R.layout.chat_item_instr -> InstrViewHolder(view)
            R.layout.chat_item_card -> CardViewHolder(view)
            R.layout.chat_item_location -> LocationViewHolder(view)
            else -> TextViewHolder(view)
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.chat_item_main
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.chat_item_left_status, R.id.chat_item_right_status -> {
                /**重新发送消息*/
                mOnItemClickListener.onItemClick(view, position, -1)
            }
            /*列表项内部点击*/
            R.id.chat_item_instr_accept_tv -> {
                // 签收指令
                ToastUtils.showToast(view.context, "签收指令")
            }
            R.id.chat_item_instr_refuse_tv -> {
                // 拒收指令
                ToastUtils.showToast(view.context, "拒收指令")
            }

            /*列表项点击*/
            R.id.chat_item_text -> {
                // 无详情
            }
            R.id.chat_item_notice -> {
                // 无详情
            }
            R.id.chat_item_audio -> {
                // 语音播放
                mOnItemClickListener.onItemClick(view, position, 0)
            }
            R.id.chat_item_video_play -> {
                // 视频播放
                val fileUrl = data[position].data.file?.url
                val saveName = FileUtils.getFileNameByUrl(fileUrl)
                val savePath = "${FileUtils.CHAT_PATH}/$saveName"
                ARouter.getInstance().build(PlayPath.VIDEO_PREVIEW)
                        .withString(PlayPath.VIDEO_URL, data[position].data.video?.url)
                        .withString(PlayPath.VIDEO_PATH, data[position].data.video?.path
                                ?: savePath)
                        .withString(PlayPath.VIDEO_NAME, data[position].data.video?.name
                                ?: saveName)
                        .navigation()
            }
            R.id.chat_item_image -> {
                // 图片预览
                val optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                        view.chat_item_image_iv,
                        view.chat_item_image_iv.width / 2,
                        view.chat_item_image_iv.height / 2,
                        0, 0)
                ARouter.getInstance().build(PlayPath.IMAGE_PREVIEW)
                        .withString(PlayPath.IMAGE_LIST, Gson().toJson(getFileListData(position, MsgTypeEnum.IMAGE)))
                        .withOptionsCompat(optionsCompat)
                        .navigation()
            }
            R.id.chat_item_file -> {
                // 文件预览
                val fileUrl = data[position].data.file?.url
                val saveName = FileUtils.getFileNameByUrl(fileUrl)
                val savePath = "${FileUtils.CHAT_PATH}/$saveName"
                ARouter.getInstance().build(FilePath.FILE_PREVIEW)
                        .withString(FilePath.FILE_URL, data[position].data.file?.url)
                        .withString(FilePath.FILE_PATH, data[position].data.file?.path
                                ?: savePath)
                        .withString(FilePath.FILE_NAME, data[position].data.file?.name
                                ?: saveName)
                        .navigation()
            }
            R.id.chat_item_instr -> {
                // TODO 指令详情
            }
            R.id.chat_item_card -> {
                // TODO 名片预览
            }
            R.id.chat_item_location -> {
                // 位置详情
                if (data[position].data.location!!.x == null || data[position].data.location!!.y == null) {
                    ToastUtils.showToast(view.context, "未知位置，无法打开")
                } else {
                    ARouter.getInstance().build(MapPath.MAIN)
                            .withDouble(MapPath.MAIN_KEY_LOCATION_X, data[position].data.location!!.x!!)
                            .withDouble(MapPath.MAIN_KEY_LOCATION_Y, data[position].data.location!!.y!!)
                            .navigation()
                }
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }

    @LayoutRes
    fun getType(ordinal: Int): Int = when (MsgTypeEnum.values()[ordinal]) {
        MsgTypeEnum.TEXT -> R.layout.chat_item_text
        MsgTypeEnum.IMAGE -> R.layout.chat_item_image
        MsgTypeEnum.AUDIO -> R.layout.chat_item_audio
        MsgTypeEnum.VIDEO -> R.layout.chat_item_video
        MsgTypeEnum.FILE -> R.layout.chat_item_file
        MsgTypeEnum.INSTR -> R.layout.chat_item_instr
        MsgTypeEnum.CARD -> R.layout.chat_item_card
        MsgTypeEnum.NOTICE -> R.layout.chat_item_notice
        MsgTypeEnum.LOCATION -> R.layout.chat_item_location
        else -> -1
    }

    /**获取列表数据内某一类型的所有数据*/
    private fun getFileListData(position: Int, typeEnum: MsgTypeEnum): FileListData {
        val imageList = arrayListOf<FileData>()
        var mPosition = 0
        data.forEach {
            if (typeEnum == it.typeEnum) {
                if (MsgTypeEnum.IMAGE == typeEnum) {
                    // 获取当前列表内所有图片，保存图片链接，不是本地就是URL，先本地
                    if (TextUtils.isEmpty(it.data.img?.path)) {
                        imageList.add(FileData(it.data.img?.name, it.data.img?.url))
                    } else {
                        imageList.add(FileData(it.data.img?.name, it.data.img?.path))
                    }
                    if (data[position].id == it.id) {
                        mPosition = imageList.size - 1
                    }
                }
            }
        }
        return FileListData(mPosition, imageList)
    }

}
