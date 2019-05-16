package qsos.core.form.view.hodler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.form_item_file.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.view.adapter.FormFileAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.FormItem

/**
 * @author : 华清松
 * @description : 文件表单项
 */
class ItemFormFileHolder(itemView: View) : BaseHolder<FormItem>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun setData(data: FormItem, position: Int) {
        itemView.item_form_key.text = data.form_item_key

        if (data.form_item_value?.values != null) {
            val files = data.form_item_value?.values!!
            itemView.rv_item_form_files.layoutManager = GridLayoutManager(
                    itemView.context, 4
            )

            itemView.rv_item_form_files.adapter = FormFileAdapter(files)

            itemView.tv_item_form_files_size.text = "${files.size}\t件"
        }

        // 监听
        itemView.tv_item_form_files_size.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, position, 0)
        }
    }

}