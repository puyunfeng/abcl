package qsos.base.chat.view.widget

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnKeyListener
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.chat_action_add.view.*
import kotlinx.android.synthetic.main.chat_action_bar.view.*
import kotlinx.android.synthetic.main.chat_action_emoji.view.*
import qsos.lib.base.utils.BaseUtils
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.record.AudioUtils
import qsos.base.chat.R
import qsos.base.chat.view.widget.emoji.EmojiLayoutView

/**
 * @author : 华清松
 * @description : 聊天操作组件
 */
class ChatActionBarView : LinearLayout {
    private var mSendTextMessageListener: OnSendTextMessageListener? = null
    private var mAudioStatusListener: OnAudioStatusListener? = null
    private var mFileListener: OnFileListener? = null

    /**键盘是否可见*/
    private var mEditVisible = true

    /*语音，录音按下*/
    private var actionDown = true
    /*语音，录音抬起*/
    private var actionUp = true
    /*语音，录音取消*/
    private var actionCancel = true
    /*语音，记录每次按下的时长，解除触摸重置*/
    private var downTime: Long = 0L
    /*语音，录音按下后移动的 y 轴坐标*/
    private var my = 0F

    /**当前输入类型*/
    private var mInputType: InputTypeEnum = InputTypeEnum.TEXT

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.chat_action_bar, this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChatActionBarView)

        chat_action_bar_et.hint = typedArray.getString(R.styleable.ChatActionBarView_chat_et_hint)

        typedArray.recycle()

        initView(context)
    }

    /**设置操作栏监听*/
    private fun initView(context: Context) {
        /*点击其它隐藏键盘*/
        chat_action_bar_input_btn.setOnClickListener {
            changeOtherInput(if (mEditVisible) InputTypeEnum.AUDIO else InputTypeEnum.TEXT)
        }
        chat_action_bar_emoji_btn.setOnClickListener {
            changeOtherInput(InputTypeEnum.EMOJI)
        }
        chat_action_bar_add.setOnClickListener {
            changeOtherInput(InputTypeEnum.FILE)
        }

        initEditView()
        initAudioView(context)
        initEmojiView()
        initAddView()
        initSendView()
    }

    /**设置输入监听*/
    private fun initEditView() {
        chat_action_bar_et.setOnTouchListener(OnTouchListener { v, _ ->
            v.isFocusable = true
            v.isFocusableInTouchMode = true

            changeOtherInput(InputTypeEnum.TEXT)

            return@OnTouchListener false
        })

        chat_action_bar_et.setOnKeyListener(OnKeyListener { v, keyCode, event ->
            val et = v as EditText
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                /*点击键盘的发送按钮*/
                et.isFocusable = false
                et.isFocusableInTouchMode = false

                BaseUtils.hideKeyboard(context as Activity)
                mSendTextMessageListener?.send(et.text.toString())

                et.setText("")

                return@OnKeyListener true
            }
            return@OnKeyListener false
        })
    }

    /**设置添加语音监听*/
    private fun initAudioView(context: Context) {
        chat_action_bar_audio_btn?.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    chat_action_bar_audio_btn.text = context.getText(R.string.chat_input_audio_send)

                    my = motionEvent.y
                    // 开始录音
                    downTime = System.currentTimeMillis()
                    actionDown = true
                    mAudioStatusListener?.send(AudioStatusEnum.START, AudioUtils.audioPath)

                    mHandler.sendEmptyMessage(1)
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    chat_action_bar_audio_btn.text = context.getText(R.string.chat_input_audio)

                    // 停止录音，判断录音时长，低于1秒判定为录音无效，不发送
                    if ((System.currentTimeMillis() - downTime) < 1000 || actionCancel) {
                        mAudioStatusListener?.send(AudioStatusEnum.CANCEL, AudioUtils.audioPath)
                    } else {
                        mAudioStatusListener?.send(AudioStatusEnum.FINISH, AudioUtils.audioPath)
                    }
                    downTime = 0L
                    actionUp = true
                    actionDown = false
                    actionCancel = false

                    mHandler.sendEmptyMessage(0)
                }
                MotionEvent.ACTION_MOVE -> {
                    if ((my - motionEvent.y) > 300) {
                        chat_action_bar_audio_btn.text = context.getText(R.string.chat_input_audio_cancel)
                        // 录音中向上移出按钮一定范围，同时解除触摸，判定取消录音
                        actionCancel = true
                        mAudioStatusListener?.send(AudioStatusEnum.CAN_CANCEL, AudioUtils.audioPath)
                    } else {
                        chat_action_bar_audio_btn.text = context.getText(R.string.chat_input_audio_send)
                        actionCancel = false
                        mAudioStatusListener?.send(AudioStatusEnum.DOING, AudioUtils.audioPath)
                    }
                }
            }

            true
        }
    }

    /**设置添加表情监听*/
    private fun initEmojiView() {
        chat_action_emoji_erl?.setOnEmojiView(chat_action_bar_et, object : EmojiLayoutView.OnEmojiListener {
            override fun setText(type: Int, text: CharSequence, send: Boolean) {
                if (type == 0) {
                    chat_action_bar_et.append(text)
                } else {
                    chat_action_bar_et.setText(text)
                }
                chat_action_bar_et.setSelection(chat_action_bar_et.text.length)
                if (send) mSendTextMessageListener?.send(text.toString())
            }

        })
    }

    /**设置添加附件监听*/
    private fun initAddView() {
        chat_action_add_take_photo?.setOnClickListener {
            // 进行拍照
            mFileListener?.getFile(FileTypeEnum.PHOTO)
            chat_action_bar_rl.visibility = View.GONE
        }
        chat_action_add_take_album?.setOnClickListener {
            // 相册选择
            mFileListener?.getFile(FileTypeEnum.ALBUM)
            chat_action_bar_rl.visibility = View.GONE
        }
        chat_action_add_take_video?.setOnClickListener {
            // 进行录像
            mFileListener?.getFile(FileTypeEnum.VIDEO)
            chat_action_bar_rl.visibility = View.GONE
        }
        chat_action_add_take_file?.setOnClickListener {
            // 文件选择
            mFileListener?.getFile(FileTypeEnum.FILE)
            chat_action_bar_rl.visibility = View.GONE
        }
        chat_action_add_location?.setOnClickListener {
            // 发送位置
            mFileListener?.getFile(FileTypeEnum.LOCATION)
            chat_action_bar_rl.visibility = View.GONE
        }
    }

    /**设置发送监听*/
    private fun initSendView() {
        chat_action_bar_send?.setOnClickListener {
            /*点击键盘的发送按钮*/
            chat_action_bar_et.isFocusable = false
            chat_action_bar_et.isFocusableInTouchMode = false
            chat_action_bar_rl.visibility = View.GONE
            BaseUtils.hideKeyboard(context as Activity)
            mSendTextMessageListener?.send(chat_action_bar_et.text.toString())

            chat_action_bar_et.setText("")
            chat_action_bar_rl.visibility = View.GONE
        }
    }

    /**发送文字信息*/
    interface OnSendTextMessageListener {
        /**
         * @param message 信息内容
         */
        fun send(message: String)
    }

    /**发送附件信息*/
    interface OnFileListener {
        /**
         * @param type 附件类型
         */
        fun getFile(type: FileTypeEnum)
    }

    /**发送语音信息*/
    interface OnAudioStatusListener {
        /**
         * @param status 语音录制状态
         * @param path 语音文件存放路径
         */
        fun send(status: AudioStatusEnum, path: String)
    }

    fun setOnAudioStatusListener(listener: OnAudioStatusListener) {
        this.mAudioStatusListener = listener
    }

    fun setOnSendTextMessageListener(listener: OnSendTextMessageListener) {
        this.mSendTextMessageListener = listener
    }

    fun setOnFileListener(listener: OnFileListener) {
        this.mFileListener = listener
    }

    /*改变其它输入面板*/
    private fun changeOtherInput(inputType: InputTypeEnum) {

        showFileView(inputType)

        when (inputType) {
            InputTypeEnum.TEXT -> {
                mEditVisible = true

                chat_action_bar_et.visibility = View.VISIBLE
                chat_action_bar_audio_btn.visibility = View.GONE

                chat_action_bar_rl.visibility = View.GONE

                chat_action_bar_input_btn.setImageResource(R.drawable.chat_input)
                chat_action_bar_add.setImageResource(R.drawable.chat_add)
            }
            InputTypeEnum.AUDIO -> {
                mEditVisible = false

                chat_action_bar_et.visibility = View.GONE
                chat_action_bar_audio_btn.visibility = View.VISIBLE

                chat_action_bar_rl.visibility = View.GONE

                chat_action_bar_input_btn.setImageResource(R.drawable.chat_audio)
                chat_action_bar_add.setImageResource(R.drawable.chat_add)

                BaseUtils.hideKeyboard(context as Activity)
            }
            InputTypeEnum.EMOJI -> {
                mEditVisible = true

                chat_action_bar_et.visibility = View.VISIBLE
                chat_action_bar_audio_btn.visibility = View.GONE

                when {
                    mInputType != inputType -> chat_action_bar_rl.visibility = View.VISIBLE
                    chat_action_bar_rl.visibility == View.VISIBLE -> chat_action_bar_rl.visibility = View.GONE
                    else -> chat_action_bar_rl.visibility = View.VISIBLE
                }

                chat_action_bar_input_btn.setImageResource(R.drawable.chat_input)

                BaseUtils.hideKeyboard(context as Activity)
            }
            InputTypeEnum.FILE -> {

                when {
                    mInputType != inputType -> chat_action_bar_rl.visibility = View.VISIBLE
                    chat_action_bar_rl.visibility == View.VISIBLE -> chat_action_bar_rl.visibility = View.GONE
                    else -> chat_action_bar_rl.visibility = View.VISIBLE
                }

                BaseUtils.hideKeyboard(context as Activity)
            }
        }

        mInputType = inputType
    }

    /**切换文件和表情面板*/
    private fun showFileView(type: InputTypeEnum) {
        when (type) {
            InputTypeEnum.EMOJI -> {
                chat_action_add.visibility = View.GONE
                chat_action_emoji.visibility = View.VISIBLE
            }
            InputTypeEnum.FILE -> {
                chat_action_add.visibility = View.VISIBLE
                chat_action_emoji.visibility = View.GONE
            }
            else -> {
            }
        }
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg != null) {
                when (msg.what) {
                    0 -> AudioUtils.stop()
                    1 -> RxPermissions(context as FragmentActivity).request(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_PHONE_STATE).subscribe {
                        if (it) {
                            // NOTICE 在这里配置录音格式，目前选择AMR，文件大小很小
                            AudioUtils.record(AudioUtils.FLAG_AMR, recordListener)
                        } else {
                            ToastUtils.showToast(context, "录音失败，没有权限")
                        }
                    }
                }
            }
        }
    }

    private val recordListener = object : AudioUtils.OnRecordListener {
        override fun record(recordStatus: AudioUtils.RecordStatus) {
            // todo 这里可以弹窗展示录制时间，状态，获取录制路径
        }
    }

    enum class InputTypeEnum(val key: String) {
        TEXT("文本"),
        EMOJI("表情"),
        AUDIO("语音"),
        FILE("附件:拍照、录像、文档"),
    }

    enum class FileTypeEnum(val key: String) {
        PHOTO("拍照"),
        ALBUM("相册"),
        VIDEO("视频"),
        FILE("文件"),
        LOCATION("位置"),
    }

    /**根据监听此状态判断语音录入状态，进行交互和发送判断，通过 this.CHAT_PATH 获取语音存放路径*/
    enum class AudioStatusEnum(val key: String) {
        /**录制*/
        START("开始录制"),
        /**录制中*/
        DOING("录制中"),
        /**可取消*/
        CAN_CANCEL("可取消"),
        /**取消*/
        CANCEL("取消"),
        /**发送*/
        FINISH("录制完成"),
    }
}