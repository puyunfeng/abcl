package qsos.core.file

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Bundle
import android.text.TextUtils
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.tencent.smtt.sdk.TbsReaderView
import kotlinx.android.synthetic.main.file_activity_preview_file.*
import qsos.lib.base.base.BaseNormalActivity
import qsos.lib.base.routepath.FilePath
import qsos.lib.base.utils.activity.ActivityUtils

/**
 * @author : 华清松
 * @description : 文件预览界面，使用TBS
 */
@Route(group = FilePath.GROUP, path = FilePath.TBS_WORD_PREVIEW)
open class TBSFilePreviewActivity :
        BaseNormalActivity(),
        TbsReaderView.ReaderCallback {

    @Autowired
    @JvmField
    var fileUrl = ""

    override val layoutId = R.layout.file_activity_preview_file

    private var mTbsReaderView: TbsReaderView? = null

    override fun initData(savedInstanceState: Bundle?) {
        ActivityUtils.instance.addActivity(this)

        if (TextUtils.isEmpty(fileUrl)) {
            showToast("打开失败")
            return
        }

    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun initView() {
        window.setFormat(PixelFormat.TRANSLUCENT)
        setSupportActionBar(tb_preview_file)
        tb_preview_file.setNavigationOnClickListener { _ ->
            finishThis()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""
        tv_preview_file?.text = "文件预览"

        mTbsReaderView = TbsReaderView(mContext, this)
        rl_preview_file.addView(mTbsReaderView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))

        mRxPermissions?.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
                ?.subscribe {
                    if (it) {
                        getData()
                    } else {
                        showToast("授权失败，无法预览文件")
                        finishThis()
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTbsReaderView != null) {
            mTbsReaderView?.onStop()
        }
    }

    override fun onCallBackAction(p0: Int?, p1: Any?, p2: Any?) {

    }

    override fun getData() {
    }

}
