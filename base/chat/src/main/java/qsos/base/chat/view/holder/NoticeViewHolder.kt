package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.chat_item_notice.view.*
import qsos.lib.base.data.chat.MsgEntity

/**
 * @author : 华清松
 * @description : 聊天列表项-提醒
 */
@SuppressLint("SetTextI18n")
class NoticeViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {

        child2View?.chat_item_notice_tv?.text = data.desc + " 类型 " + data.typeEnum.desc

        child2View?.setOnClickListener { v ->
            onClick(v)
        }
    }

}