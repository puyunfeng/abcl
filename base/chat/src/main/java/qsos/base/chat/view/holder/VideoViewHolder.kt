package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.chat_item_video.view.*
import qsos.lib.base.data.chat.MsgEntity
import qsos.lib.base.utils.file.FileUtils
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 聊天列表项-视频
 */
@SuppressLint("SetTextI18n")
class VideoViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {
        if (TextUtils.isEmpty(data.data.video?.path)) {
            ImageLoaderUtils.displayGif(itemView.context, child2View?.chat_item_video_cover, data.data.video?.coverUrl)
        } else {
            val bitmap = FileUtils.getVideoThumb(data.data.video?.path!!)
            ImageLoaderUtils.display(itemView.context, child2View?.chat_item_video_cover, bitmap)
        }

        child2View?.chat_item_video_play?.setOnClickListener {
            onClick(it)
        }
    }

}