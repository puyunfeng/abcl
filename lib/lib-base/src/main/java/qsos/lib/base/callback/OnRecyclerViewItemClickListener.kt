package qsos.lib.base.callback

import android.view.View

/**
 * @author : 华清松
 * @description : RecyclerView 子项点击监听
 */
interface OnRecyclerViewItemClickListener {
    /**
     * 子项点击监听
     * @param view 当前点击的视图
     * @param position 当前项位置
     * @param obj 附加值，比如可以根据此来值判断将要进行什么操作*/
    fun onItemClick(view: View, position: Int, obj: Any?)

}

/**
 * @author : 华清松
 * @description : RecyclerView 子项长按监听
 */
interface OnRecyclerViewItemLongClickListener {
    /**
     * 子项点击监听
     * @param view 当前点击的视图
     * @param position 当前项位置
     * @param obj 附加值，比如可以根据此来值来判断将要进行什么操作*/
    fun onItemLongClick(view: View, position: Int, obj: Any?)

}