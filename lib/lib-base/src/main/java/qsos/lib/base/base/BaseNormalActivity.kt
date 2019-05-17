package qsos.lib.base.base

/**
 * @author : 华清松
 * @description : 无需额外处理业务的 Activity
 */
abstract class BaseNormalActivity : BaseActivity() {
    override val reload = false
    override var statusBarColor: Int? = null
}
