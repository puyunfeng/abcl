package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.chat_item_instr.view.*
import qsos.lib.base.data.chat.MsgEntity

/**
 * @author : 华清松
 * @description : 聊天列表项-指令
 */
@SuppressLint("SetTextI18n")
class InstrViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {

        child2View?.chat_item_instr_accept_tv?.setOnClickListener {
            onClick(it)
        }
        child2View?.chat_item_instr_refuse_tv?.setOnClickListener {
            onClick(it)
        }
        child2View?.setOnClickListener { v ->
            onClick(v)
        }
    }

}