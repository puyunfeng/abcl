package qsos.core.qrcode

import android.hardware.Camera

class OpenCamera(private val index: Int, val camera: Camera, val facing: CameraFacing, val orientation: Int) {

    override fun toString(): String {
        return "Camera #$index : $facing,$orientation"
    }

}

