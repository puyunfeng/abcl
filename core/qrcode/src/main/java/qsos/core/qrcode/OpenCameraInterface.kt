package qsos.core.qrcode

import android.hardware.Camera

object OpenCameraInterface {

    const val NO_REQUESTED_CAMERA = -1

    fun open(cameraId: Int): OpenCamera? {

        val numCameras = Camera.getNumberOfCameras()
        if (numCameras == 0) {
            return null
        }

        val explicitRequest = cameraId >= 0

        var selectedCameraInfo: Camera.CameraInfo? = null
        var index: Int
        if (explicitRequest) {
            index = cameraId
            selectedCameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(index, selectedCameraInfo)
        } else {
            index = 0
            while (index < numCameras) {
                val cameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(index, cameraInfo)
                val reportedFacing = CameraFacing.values()[cameraInfo.facing]
                if (reportedFacing === CameraFacing.BACK) {
                    selectedCameraInfo = cameraInfo
                    break
                }
                index++
            }
        }

        val camera: Camera?
        if (index < numCameras) {
            camera = Camera.open(index)
        } else {
            if (explicitRequest) {
                camera = null
            } else {
                camera = Camera.open(0)
                selectedCameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(0, selectedCameraInfo)
            }
        }

        return if (camera == null) {
            null
        } else OpenCamera(index,
                camera,
                CameraFacing.values()[selectedCameraInfo!!.facing],
                selectedCameraInfo.orientation)
    }

}
