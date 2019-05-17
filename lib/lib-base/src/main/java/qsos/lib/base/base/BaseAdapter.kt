package qsos.lib.base.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.callback.OnRecyclerViewItemLongClickListener
import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : BaseAdapter
 */
abstract class BaseAdapter<T : BaseEntity>(var data: ArrayList<T>)
    : RecyclerView.Adapter<BaseHolder<T>>(),
        OnRecyclerViewItemClickListener,
        OnRecyclerViewItemLongClickListener,
        View.OnClickListener,
        View.OnLongClickListener {

    private var mHolder: BaseHolder<T>? = null
    var mContext: Context? = null

    /**让子类实现用以提供 BaseHolder */
    abstract fun getHolder(view: View, viewType: Int): BaseHolder<T>

    /**提供用于 item 布局的 layoutId */
    abstract fun getLayoutId(viewType: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<T> {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(getLayoutId(viewType), parent, false)
        mHolder = getHolder(view, viewType)
        // 设置 Item 点击事件
        mHolder!!.setOnItemClickListener(this)
        mHolder!!.setOnItemLongClickListener(this)
        return mHolder as BaseHolder<T>
    }

    override fun onClick(v: View) {

    }

    override fun onLongClick(v: View): Boolean {

        return true
    }

    /**绑定数据*/
    override fun onBindViewHolder(holder: BaseHolder<T>, position: Int) {
        holder.setData(data[position], position)
    }

    /**返回数据个数*/
    override fun getItemCount(): Int {
        return data.size
    }

    /**获得某个 position 上的 item 的数据*/
    fun getItem(position: Int): T? {
        return data[position]
    }

    /**遍历所有 RecyclerView 中的 Holder,释放他们需要释放的资源*/
    fun releaseAllHolder(recyclerView: RecyclerView?) {
        if (recyclerView == null) {
            return
        }
        (recyclerView.childCount - 1 downTo 0).forEach { i ->
            val view = recyclerView.getChildAt(i)
            val viewHolder = recyclerView.getChildViewHolder(view)
            if (viewHolder != null && viewHolder is BaseHolder<*>) {
                viewHolder.onRelease()
            }
        }
    }
}
