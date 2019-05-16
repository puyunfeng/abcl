package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.view.View
import qsos.lib.base.data.chat.MsgEntity

/**
 * @author : 华清松
 * @description : 聊天列表项-文件
 */
@SuppressLint("SetTextI18n")
class FileViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {

        child2View?.setOnClickListener { v ->
            onClick(v)
        }

    }

}