package qsos.base.chat.view.widget.emoji

import android.app.Activity
import android.view.View
import kotlinx.android.synthetic.main.chat_emoji_cell.view.*
import qsos.lib.base.base.BaseHolder

/**
 * @author : 华清松
 * @description : 表情列表子项布局
 */
class EmojiItemHolder(itemView: View) : BaseHolder<EmojiEntity>(itemView) {

    override fun setData(data: EmojiEntity, position: Int) {
        itemView.chat_emoji_tv.text = EmojiParser.getInstance(itemView.context as Activity).setEmojiSpansToEdit(data)
    }

}