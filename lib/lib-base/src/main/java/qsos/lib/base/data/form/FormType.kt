package qsos.lib.base.data.form

/**
 * @author : 华清松
 * @description : 表单业务类型
 */
enum class FormType(key: String) {

    ADD_NOTICE("添加公告"),
    ADD_PLAN_DAY("添加计划");

    val title = key

    companion object {

        fun getEnum(key: String?): FormType? {
            var mType: FormType? = null
            values().forEach { type ->
                if (type.title == key) {
                    mType = type
                }
            }
            return mType
        }
    }
}