package qsos.lib.base.view

import qsos.lib.base.data.BaseEntity

/**
 * @author : 华清松
 * @description : 功能实体
 */
class ComponentItemEntity : BaseEntity {
    constructor()
    constructor(type: ComponentEnum) {
        this.type = type
    }

    var type: ComponentEnum? = null

    enum class ComponentEnum(key: String) {
        BASE_CHAT("聊天业务"),
        CORE_QRCODE("二维码功能"),
        CORE_FORM_PLAN("计划表单"),
        CORE_FORM_NOTICE("公告表单"),
        CORE_PLAY_VIDEO("视频播放"),
        CORE_NFC("NFC功能"),
        CORE_MAP("公安地图"),
        CORE_PLAY_IMAGE("图片画廊");

        private val keyName = key

        fun getKeyName(): String {
            return keyName
        }
    }

    companion object {
        fun getList(): ArrayList<ComponentItemEntity> {
            val mListData = arrayListOf<ComponentItemEntity>()
            ComponentEnum.values().forEach {
                mListData.add(ComponentItemEntity(it))
            }
            return mListData
        }
    }

}