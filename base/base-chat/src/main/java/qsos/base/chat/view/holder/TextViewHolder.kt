package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import kotlinx.android.synthetic.main.chat_item_text.view.*
import qsos.lib.base.data.chat.MsgEntity
import qsos.base.chat.view.widget.emoji.EmojiParser

/**
 * @author : 华清松
 * @description : 聊天列表项-文本
 */
@SuppressLint("SetTextI18n")
class TextViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {

        child2View?.chat_item_text_iv?.text = EmojiParser.getInstance(itemView.context as Activity).setText(data.data.text)

        child2View?.setOnClickListener { v ->
            onClick(v)
        }
    }

}