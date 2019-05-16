package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.chat_item_card.view.*
import qsos.lib.base.data.chat.MsgEntity

/**
 * @author : 华清松
 * @description : 聊天列表项-名片
 */
@SuppressLint("SetTextI18n")
class CardViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {

        child2View?.chat_item_card_name_tv?.text = data.desc

        child2View?.setOnClickListener { v ->
            onClick(v)
        }
    }

}