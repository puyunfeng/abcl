package qsos.base.chat.view.widget.emoji

import android.app.Activity
import android.view.View
import android.widget.EditText
import qsos.base.chat.R
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import java.util.*

/**
 * @author : 华清松
 * @description : 表情列表容器
 */
class EmojiAdapter(emojiList: ArrayList<EmojiEntity>) : BaseAdapter<EmojiEntity>(emojiList) {

    private var mEmojiListener: EmojiLayoutView.OnEmojiListener? = null

    fun setOnEmojiListener(emojiListener: EmojiLayoutView.OnEmojiListener?) {
        this.mEmojiListener = emojiListener
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<EmojiEntity> {
        return EmojiItemHolder(view)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.chat_emoji_cell
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        clickEmoji(view.context as Activity, position)
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }

    /**表情点击*/
    private fun clickEmoji(activity: Activity, position: Int) {
        val convertContent = EmojiParser.getInstance(activity).setEmojiSpansToEdit(EmojiParser.getInstance(activity).mPageListData[position])
        mEmojiListener?.setText(0, convertContent, false)
    }

    /**删除表情*/
    private fun deleteString(activity: Activity, editText: EditText?) {
        var getString = editText?.text.toString()
        if (getString.isNotEmpty()) {
            var lastString = ""
            if (getString.length > 1) {

                lastString = getString.substring(getString.length - 2, getString.length)
            } else if (getString.length == 1) {
                lastString = getString.substring(getString.length - 1, getString.length)
            }
            var tag = true
            while (tag && getString.isNotEmpty()) {
                if (lastString == "] ") {
                    if (getString.isEmpty()) {
                        tag = false
                    } else {
                        try {
                            Thread.sleep(20)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        getString = getString.substring(0, getString.length - 1)
                        if (getString.isNotEmpty() && getString.substring(getString.length - 1, getString.length) == "[") {
                            tag = false
                            getString = getString.substring(0, getString.length - 1)
                        }
                    }
                } else {
                    tag = false
                    getString = getString.substring(0, getString.length - 1)
                }
            }
            val convertContent = EmojiParser.getInstance(activity).setEmojiSpansToEdit(EmojiEntity(getString, null))
            mEmojiListener?.setText(1, convertContent.toString(), false)
        }
    }

}