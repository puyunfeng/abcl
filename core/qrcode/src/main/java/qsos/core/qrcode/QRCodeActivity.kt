package qsos.core.qrcode

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import kotlinx.android.synthetic.main.qrcode_activity_main.*
import qsos.lib.base.routepath.QRCodePath
import qsos.lib.base.utils.RxBus
import java.util.concurrent.TimeUnit

/**
 * @author : 华清松
 * @description : 二维码扫描界面
 */
@Route(group = QRCodePath.GROUP, path = QRCodePath.MAIN)
class QRCodeActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
        QRCodeReaderView.OnQRCodeReadListener, Toolbar.OnMenuItemClickListener {

    private var light = true
    private var findCode = false
    private var beepManager: BeepManager? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.qrcode_activity_main)

        setSupportActionBar(qrcode_tb)
        qrcode_tb.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""
        qrcode_tb.setOnMenuItemClickListener(this)

        beepManager = BeepManager(this)

        RxPermissions(this).request(Manifest.permission.CAMERA)
                .subscribe {
                    if (it) {
                        initQRCodeReaderView()
                    } else {
                        finish()
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        qrcode_read?.startCamera()

        beepManager?.updatePrefs()
    }

    override fun onPause() {
        super.onPause()
        qrcode_read?.stopCamera()

        beepManager?.close()
    }

    @SuppressLint("CheckResult")
    override fun onQRCodeRead(text: String, points: Array<PointF?>) {
        if (!findCode) {
            findCode = true
            qrcode_points?.setPoints(points)
            /*🔔🔔🔔*/
            beepManager?.playBeepSoundAndVibrate()
            /*发送二维码扫描结果*/
            RxBus.instance.send(QRCodeResultEntity(QRCodePath.QR_CODE_RESULT, text))
        } else {
            Observable.timer(1000, TimeUnit.MILLISECONDS).subscribe {
                finish()
            }
        }
    }

    private fun initQRCodeReaderView() {
        qrcode_read.setAutofocusInterval(500L)
        qrcode_read.setOnQRCodeReadListener(this)
        qrcode_read.setBackCamera()
        qrcode_read.setQRDecodingEnabled(true)
        qrcode_read.startCamera()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("灯光").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        qrcode_read.setTorchEnabled(light)
        light = !light
        return true
    }
}