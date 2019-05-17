package qsos.base.chat.view.holder

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.chat_item_all.view.*
import kotlinx.android.synthetic.main.chat_item_left.view.*
import kotlinx.android.synthetic.main.chat_item_main.view.*
import kotlinx.android.synthetic.main.chat_item_right.view.*
import qsos.base.chat.R
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.chat.MsgEntity
import qsos.lib.base.data.chat.MsgTypeEnum
import qsos.lib.base.utils.DateUtils
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 消息列表项基础设置
 */
abstract class AbsChatViewHolder(itemView: View) : BaseHolder<MsgEntity>(itemView) {

    private var childView: View? = null
    var child2View: View? = null

    /**设置聊天内容视图*/
    abstract fun setChildView(data: MsgEntity, position: Int)

    @SuppressLint("SetTextI18n")
    override fun setData(data: MsgEntity, position: Int) {
        itemView.chat_item_left?.visibility = View.GONE
        itemView.chat_item_right?.visibility = View.GONE
        itemView.chat_item_notice?.visibility = View.GONE

        if (data.typeEnum == MsgTypeEnum.NOTICE) {
            childView = itemView.chat_item_notice
            childView?.visibility = View.VISIBLE
        } else {
            if (data.byMe) {
                childView = itemView.chat_item_right
                childView?.visibility = View.VISIBLE

                childView?.chat_item_right_nickname?.text = data.nickname
                childView?.chat_item_right_time?.text = DateUtils.dateFormat2String(data.sendTime)
                ImageLoaderUtils.displayHeader(itemView.context, childView?.chat_item_right_head_iv, data.headUrl)
            } else {
                childView = itemView.chat_item_left
                childView?.visibility = View.VISIBLE

                childView?.chat_item_left_nickname?.text = data.nickname
                childView?.chat_item_left_time?.text = DateUtils.dateFormat2String(data.sendTime)
                ImageLoaderUtils.displayHeader(itemView.context, childView?.chat_item_left_head_iv, data.headUrl)
            }
        }

        childView?.chat_item_text?.visibility = View.GONE
        childView?.chat_item_image?.visibility = View.GONE
        childView?.chat_item_video?.visibility = View.GONE
        childView?.chat_item_audio?.visibility = View.GONE
        childView?.chat_item_file?.visibility = View.GONE
        childView?.chat_item_location?.visibility = View.GONE
        childView?.chat_item_instr?.visibility = View.GONE
        childView?.chat_item_card?.visibility = View.GONE

        when (data.typeEnum) {
            MsgTypeEnum.NOTICE -> child2View = childView

            MsgTypeEnum.TEXT -> child2View = childView?.chat_item_text
            MsgTypeEnum.IMAGE -> child2View = childView?.chat_item_image
            MsgTypeEnum.AUDIO -> {
                child2View = childView?.chat_item_audio
                child2View?.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rectangle_gray)
            }
            MsgTypeEnum.VIDEO -> child2View = childView?.chat_item_video
            MsgTypeEnum.FILE -> child2View = childView?.chat_item_file
            MsgTypeEnum.INSTR -> child2View = childView?.chat_item_instr
            MsgTypeEnum.CARD -> child2View = childView?.chat_item_card
            MsgTypeEnum.LOCATION -> child2View = childView?.chat_item_location
            else -> {
            }
        }

        setChildView(data, position)

        val chatItemStatus = if (data.byMe) childView?.chat_item_right_status else childView?.chat_item_left_status
        /**设置消息状态*/
        when (data.status) {
            -1 -> {
                chatItemStatus?.visibility = View.VISIBLE
                chatItemStatus?.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(itemView.context, R.drawable.chat_error), null)
            }
            0 -> {
                chatItemStatus?.visibility = View.GONE
            }
            1 -> {
                chatItemStatus?.visibility = View.VISIBLE
                chatItemStatus?.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(itemView.context, R.drawable.chat_error_grey), null)
            }
        }
        chatItemStatus?.setOnClickListener {
            chatItemStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(itemView.context, R.drawable.chat_error_grey), null)
            onClick(it)
        }

        child2View?.visibility = View.VISIBLE
    }
}