package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.chat_item_audio.view.*
import qsos.lib.base.data.chat.MsgEntity

/**
 * @author : 华清松
 * @description : 聊天列表项-语音
 */
@SuppressLint("SetTextI18n")
class AudioViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    private var audioView: ImageView? = null

    override fun setChildView(data: MsgEntity, position: Int) {

        if (data.byMe) {
            child2View?.chat_item_audio_left_iv?.visibility = View.GONE
            child2View?.chat_item_audio_right_iv?.visibility = View.VISIBLE
            audioView = child2View?.chat_item_audio_right_iv
        } else {
            child2View?.chat_item_audio_left_iv?.visibility = View.VISIBLE
            child2View?.chat_item_audio_right_iv?.visibility = View.GONE
            audioView = child2View?.chat_item_audio_left_iv
        }

        child2View?.setOnClickListener { v ->
            onClick(v)
        }
    }
}