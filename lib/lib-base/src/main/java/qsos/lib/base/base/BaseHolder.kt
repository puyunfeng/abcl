package qsos.lib.base.base

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.callback.OnRecyclerViewItemLongClickListener
import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @desc : 子项布局 Holder
 */
@SuppressLint("SetTextI18n")
abstract class BaseHolder<T : BaseEntity>(itemView: View)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
    private val t: T? = null
    /**子项点击监听*/
    var mOnItemClickListener: OnRecyclerViewItemClickListener? = null

    /**设置子项点击监听*/
    fun setOnItemClickListener(listener: OnRecyclerViewItemClickListener): BaseHolder<T> {
        this.mOnItemClickListener = listener
        return this
    }

    /**子项长按监听*/
    var mOnItemLongClickListener: OnRecyclerViewItemLongClickListener? = null

    /**设置子项长按监听*/
    fun setOnItemLongClickListener(listener: OnRecyclerViewItemLongClickListener): BaseHolder<T> {
        this.mOnItemLongClickListener = listener
        return this
    }

    init {
        // 点击监听
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    /**设置数据 */
    abstract fun setData(data: T, position: Int)

    /** 释放资源 */
    open fun onRelease() {
        this.mOnItemClickListener = null
        this.mOnItemLongClickListener = null
    }

    override fun onLongClick(view: View): Boolean {
        mOnItemLongClickListener?.onItemLongClick(view, adapterPosition, 0)
        return true
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(view, adapterPosition, 0)
    }

}
