package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.chat_item_image.view.*
import qsos.lib.base.data.chat.MsgEntity
import qsos.lib.base.utils.file.FileUtils
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 聊天列表项-图片
 */
@SuppressLint("SetTextI18n")
class ImageViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {

        if (TextUtils.isEmpty(data.data.img?.url)) {
            if (!TextUtils.isEmpty(data.data.img?.path)) {
                val image = FileUtils.getFile(data.data.img?.path!!)
                ImageLoaderUtils.display(itemView.context, child2View?.chat_item_image_iv, image)
            }
        } else {
            ImageLoaderUtils.display(itemView.context, child2View?.chat_item_image_iv, data.data.img?.url)
        }

        child2View?.setOnClickListener { v ->
            onClick(v)
        }
    }

}