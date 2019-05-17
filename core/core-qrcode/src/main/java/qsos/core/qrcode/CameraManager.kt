package qsos.core.qrcode

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import android.view.SurfaceHolder
import com.google.zxing.PlanarYUVLuminanceSource

import java.io.IOException

class CameraManager(context: Context) {

    private val configManager: CameraConfigurationManager
    private var openCamera: OpenCamera? = null
    private var autoFocusManager: AutoFocusManager? = null
    private var initialized: Boolean = false
    private var previewing: Boolean = false
    private var previewCallback: Camera.PreviewCallback? = null
    private var displayOrientation = 0

    @set:Synchronized
    var previewCameraId = OpenCameraInterface.NO_REQUESTED_CAMERA
    private var autofocusIntervalInMs = AutoFocusManager.DEFAULT_AUTO_FOCUS_INTERVAL_MS

    val previewSize: Point?
        get() = configManager.cameraResolution

    private val isOpen: Boolean
        @Synchronized get() = openCamera != null

    init {
        this.configManager = CameraConfigurationManager(context)
    }

    fun setPreviewCallback(previewCallback: Camera.PreviewCallback?) {
        this.previewCallback = previewCallback

        if (isOpen) {
            openCamera!!.camera.setPreviewCallback(previewCallback)
        }
    }

    fun setDisplayOrientation(degrees: Int) {
        this.displayOrientation = degrees

        if (isOpen) {
            openCamera!!.camera.setDisplayOrientation(degrees)
        }
    }

    fun setAutofocusInterval(autofocusIntervalInMs: Long) {
        this.autofocusIntervalInMs = autofocusIntervalInMs
        if (autoFocusManager != null) {
            autoFocusManager!!.setAutofocusInterval(autofocusIntervalInMs)
        }
    }

    fun forceAutoFocus() {
        if (autoFocusManager != null) {
            autoFocusManager!!.start()
        }
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames into.
     * @param height @throws IOException Indicates the camera driver failed to open.
     */
    @Synchronized
    @Throws(IOException::class)
    fun openDriver(holder: SurfaceHolder, width: Int, height: Int) {
        var theCamera = openCamera
        if (!isOpen) {
            theCamera = OpenCameraInterface.open(previewCameraId)
            if (theCamera?.camera == null) {
                throw IOException("Camera.open() failed to return object from driver")
            }
            openCamera = theCamera
        }
        theCamera!!.camera.setPreviewDisplay(holder)
        theCamera.camera.setPreviewCallback(previewCallback)
        theCamera.camera.setDisplayOrientation(displayOrientation)

        if (!initialized) {
            initialized = true
            configManager.initFromCameraParameters(theCamera, width, height)
        }

        val cameraObject = theCamera.camera
        var parameters: Camera.Parameters? = cameraObject.parameters
        val parametersFlattened = parameters?.flatten()
        try {
            configManager.setDesiredCameraParameters(theCamera, false)
        } catch (re: RuntimeException) {
            parameters = cameraObject.parameters
            parameters!!.unflatten(parametersFlattened)
            try {
                cameraObject.parameters = parameters
                configManager.setDesiredCameraParameters(theCamera, true)
            } catch (re2: RuntimeException) {
            }
        }

        cameraObject.setPreviewDisplay(holder)
    }

    @Synchronized
    fun setTorchEnabled(enabled: Boolean) {
        val theCamera = openCamera
        if (theCamera != null && enabled != configManager.getTorchState(theCamera.camera)) {
            val wasAutoFocusManager = autoFocusManager != null
            if (wasAutoFocusManager) {
                autoFocusManager!!.stop()
                autoFocusManager = null
            }
            configManager.setTorchEnabled(theCamera.camera, enabled)
            if (wasAutoFocusManager) {
                autoFocusManager = AutoFocusManager(theCamera.camera)
                autoFocusManager!!.start()
            }
        }
    }

    @Synchronized
    fun closeDriver() {
        if (isOpen) {
            openCamera!!.camera.release()
            openCamera = null
        }
    }

    @Synchronized
    fun startPreview() {
        val theCamera = openCamera
        if (theCamera != null && !previewing) {
            theCamera.camera.startPreview()
            previewing = true
            autoFocusManager = AutoFocusManager(theCamera.camera)
            autoFocusManager!!.setAutofocusInterval(autofocusIntervalInMs)
        }
    }

    @Synchronized
    fun stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager!!.stop()
            autoFocusManager = null
        }
        if (openCamera != null && previewing) {
            openCamera!!.camera.stopPreview()
            previewing = false
        }
    }

    fun buildLuminanceSource(data: ByteArray, width: Int, height: Int): PlanarYUVLuminanceSource {
        return PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
    }

    companion object {

        private val TAG = CameraManager::class.java.simpleName
    }
}
