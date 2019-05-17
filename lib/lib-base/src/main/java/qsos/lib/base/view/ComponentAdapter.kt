package qsos.lib.base.view

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import qsos.lib.base.R
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.FormType
import qsos.lib.base.data.play.FileData
import qsos.lib.base.data.play.FileListData
import qsos.lib.base.routepath.*

/**
 * @author : 华清松
 * @description : 功能 DEMO 列表容器
 */
class ComponentAdapter(list: ArrayList<ComponentItemEntity>)
    : BaseAdapter<ComponentItemEntity>(list) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<ComponentItemEntity> {
        return ComponentHolder(view).setOnItemClickListener(this).setOnItemLongClickListener(this)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_component
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        when (data[position].type) {
            ComponentItemEntity.ComponentEnum.CORE_PLAY_VIDEO -> {
                ARouter.getInstance().build(PlayPath.VIDEO_PREVIEW)
                        .withString(PlayPath.VIDEO_URL, "http://resource.qsos.vip/test.mp4")
                        .withString(PlayPath.VIDEO_PATH, "")
                        .withString(PlayPath.VIDEO_NAME, "测试视频")
                        .navigation()
            }
            ComponentItemEntity.ComponentEnum.CORE_PLAY_IMAGE -> {
                val fileDataList = arrayListOf<FileData>()
                fileDataList.add(FileData("测试图片1", "http://pic27.nipic.com/20130129/9252150_105546607370_2.jpg"))
                fileDataList.add(FileData("测试图片2", "http://a4.att.hudong.com/13/80/01300000375382124246807617073.jpg"))
                fileDataList.add(FileData("测试图片3", "http://img2.3lian.com/2014/f6/79/d/50.jpg"))
                fileDataList.add(FileData("测试图片4", "http://www.pptbz.com/pptpic/UploadFiles_6909/201205/2012052412255329.jpg"))
                val fileListData = FileListData(0, fileDataList)
                ARouter.getInstance().build(PlayPath.IMAGE_PREVIEW)
                        .withString(PlayPath.IMAGE_LIST, Gson().toJson(fileListData))
                        .navigation()
            }
            ComponentItemEntity.ComponentEnum.CORE_QRCODE -> {
                ARouter.getInstance().build(QRCodePath.MAIN)
                        .navigation()
            }
            ComponentItemEntity.ComponentEnum.BASE_CHAT -> {
                ARouter.getInstance().build(ChatPath.CHAT)
                        .navigation()
            }
            ComponentItemEntity.ComponentEnum.CORE_FORM_PLAN -> {
                ARouter.getInstance().build(FormPath.MAIN)
                        .withString("form_type", FormType.ADD_PLAN_DAY.title)
                        .navigation()
            }
            ComponentItemEntity.ComponentEnum.CORE_FORM_NOTICE -> {
                ARouter.getInstance().build(FormPath.MAIN)
                        .withString("form_type", FormType.ADD_NOTICE.title)
                        .navigation()
            }
            ComponentItemEntity.ComponentEnum.CORE_NFC -> {
                ARouter.getInstance().build(NfcPath.MAIN)
                        .navigation()
            }
            ComponentItemEntity.ComponentEnum.CORE_MAP -> {
                ARouter.getInstance().build(MapPath.MAIN)
                        .withDouble(MapPath.MAIN_KEY_LOCATION_X, 32.0)
                        .withDouble(MapPath.MAIN_KEY_LOCATION_Y, 32.0)
                        .navigation()
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}

}