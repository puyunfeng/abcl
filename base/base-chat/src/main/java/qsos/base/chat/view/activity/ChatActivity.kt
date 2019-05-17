package qsos.base.chat.view.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import qsos.lib.base.routepath.ChatPath
import qsos.core.file.BaseFileModuleActivity
import qsos.base.chat.R
import qsos.base.chat.view.fragment.ChatFragment

/**
 * @author : 华清松
 * @description : 聊天活动
 */
@Route(group = ChatPath.GROUP, path = ChatPath.CHAT)
class ChatActivity : BaseFileModuleActivity() {

    private var mChatFragment = ChatFragment()

    override val layoutId = R.layout.chat_activity_main
    override val reload = true

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        transaction.replace(R.id.chat_main_fl, mChatFragment)
        transaction.commit()
    }

    override fun initView() {
        super.initView()

    }

    override fun getData() {

    }

}