package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.chat_item_location.view.*
import qsos.lib.base.data.chat.MsgEntity

/**
 * @author : 华清松
 * @description : 聊天列表项-位置
 */
@SuppressLint("SetTextI18n")
class LocationViewHolder(itemView: View) : AbsChatViewHolder(itemView) {

    override fun setChildView(data: MsgEntity, position: Int) {

        child2View?.chat_item_location_name_tv?.text = data.data.location?.name

        child2View?.setOnClickListener { v ->
            onClick(v)
        }
    }

}