package qsos.lib.base.view

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.item_component.view.*
import qsos.lib.base.base.BaseHolder

/**
 * @author : 华清松
 * @description : 功能视图
 */
class ComponentHolder(itemView: View) : BaseHolder<ComponentItemEntity>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun setData(data: ComponentItemEntity, position: Int) {

        itemView.item_component_tv?.text = data.type?.getKeyName()

        itemView.setOnClickListener { v ->
            mOnItemClickListener?.onItemClick(v, position, data)
        }

        itemView.setOnLongClickListener { v ->
            mOnItemLongClickListener?.onItemLongClick(v, position, data)
            return@setOnLongClickListener false
        }
    }
}