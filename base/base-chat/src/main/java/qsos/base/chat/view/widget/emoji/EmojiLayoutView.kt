package qsos.base.chat.view.widget.emoji

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chat_emoji_layout.view.*
import qsos.base.chat.R

/**
 * @author : 华清松
 * @description : 表情布局
 */
class EmojiLayoutView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener, View.OnTouchListener, TextView.OnEditorActionListener {
    private var mContext = context
    private var mEditText: EditText? = null

    init {
        addView(LayoutInflater.from(context).inflate(R.layout.chat_emoji_layout, null))

        /**表情列表*/
        val mAdapter = EmojiAdapter(EmojiParser.getInstance(mContext as Activity).mPageListData)
        chat_emoji_rv.layoutManager = GridLayoutManager(context, 5)
        chat_emoji_rv.adapter = mAdapter

    }

    fun setOnEmojiView(editText: EditText, mOnEmojiListener: OnEmojiListener) {
        this.mEditText = editText
        (chat_emoji_rv.adapter as EmojiAdapter).setOnEmojiListener(mOnEmojiListener)
    }

    override fun onClick(v: View) {
        // 切换表情按钮

        toggle(v, true)
    }

    /**
     * 为true隐藏键盘，显示表情面板
     * 为false弹出键盘，隐藏表情面板
     */
    private fun toggle(v: View?, flag: Boolean) {
        if (flag) {
            //显示表情面板，隐藏布局
            if (!isEmoji) {
                // FIXME 隐藏键盘
                isEmoji = true
            } else {
                //显示键盘
                isEmoji = false
            }
        } else {
            isKeyBrodEmoji = true
        }
    }

    /**
     * 设置 EditText 按下
     */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v.id == mEditText!!.id) {
            toggle(mEditText, false)
        }
        return false
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        // 监听键盘发送
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            val text = v.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(mContext, text + "不能为空", Toast.LENGTH_SHORT).show()
            } else {
                // FIXME 隐藏键盘
                mEditText!!.setText("")
            }
        }
        return false
    }

    interface OnEmojiListener {
        /**
         * @param type 操作方式 0-追加文本 1-直接替换文本
         * @param text 要操作的文本
         * @param send 是否操作后发送文本
         */
        fun setText(type: Int, text: CharSequence, send: Boolean)
    }

    companion object {
        // 监听底部表情布局和键盘的切换
        var isEmoji: Boolean = false
        var isKeyBrodEmoji: Boolean = false
    }
}
