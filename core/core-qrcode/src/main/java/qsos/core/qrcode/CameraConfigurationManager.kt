package qsos.core.qrcode

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import java.util.*

internal class CameraConfigurationManager(private val context: Context) {

    var screenResolution: Point? = null
        private set
    var cameraResolution: Point? = null
        private set
    private var bestPreviewSize: Point? = null
    private var previewSizeOnScreen: Point? = null
    private var cwRotationFromDisplayToCamera: Int = 0
    private var cwNeededRotation: Int = 0

    fun initFromCameraParameters(camera: OpenCamera, width: Int, height: Int) {
        val parameters = camera.camera.parameters
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay

        val displayRotation = display.rotation
        val cwRotationFromNaturalToDisplay: Int
        when (displayRotation) {
            Surface.ROTATION_0 -> cwRotationFromNaturalToDisplay = 0
            Surface.ROTATION_90 -> cwRotationFromNaturalToDisplay = 90
            Surface.ROTATION_180 -> cwRotationFromNaturalToDisplay = 180
            Surface.ROTATION_270 -> cwRotationFromNaturalToDisplay = 270
            else ->
                // Have seen this return incorrect values like -90
                if (displayRotation % 90 == 0) {
                    cwRotationFromNaturalToDisplay = (360 + displayRotation) % 360
                } else {
                    throw IllegalArgumentException("Bad rotation: $displayRotation")
                }
        }

        var cwRotationFromNaturalToCamera = camera.orientation

        // Still not 100% sure about this. But acts like we need to flip this:
        if (camera.facing === CameraFacing.FRONT) {
            cwRotationFromNaturalToCamera = (360 - cwRotationFromNaturalToCamera) % 360
        }

        cwRotationFromDisplayToCamera = (360 + cwRotationFromNaturalToCamera - cwRotationFromNaturalToDisplay) % 360
        cwNeededRotation = if (camera.facing === CameraFacing.FRONT) {
            (360 - cwRotationFromDisplayToCamera) % 360
        } else {
            cwRotationFromDisplayToCamera
        }

        screenResolution = Point(width, height)
        cameraResolution = findBestPreviewSizeValue(parameters, screenResolution!!)
        bestPreviewSize = findBestPreviewSizeValue(parameters, screenResolution!!)
        val isScreenPortrait = screenResolution!!.x < screenResolution!!.y
        val isPreviewSizePortrait = bestPreviewSize!!.x < bestPreviewSize!!.y

        if (isScreenPortrait == isPreviewSizePortrait) {
            previewSizeOnScreen = bestPreviewSize
        } else {
            previewSizeOnScreen = Point(bestPreviewSize!!.y, bestPreviewSize!!.x)
        }
    }

    fun setDesiredCameraParameters(camera: OpenCamera, safeMode: Boolean) {

        val theCamera = camera.camera
        val parameters = theCamera.parameters ?: return

        var focusMode: String? = null
        if (!safeMode) {
            val supportedFocusModes = parameters.supportedFocusModes
            focusMode = findSettableValue("focus mode",
                    supportedFocusModes,
                    Camera.Parameters.FOCUS_MODE_AUTO)
        }
        if (focusMode != null) {
            parameters.focusMode = focusMode
        }

        parameters.setPreviewSize(bestPreviewSize!!.x, bestPreviewSize!!.y)

        theCamera.parameters = parameters

        theCamera.setDisplayOrientation(cwRotationFromDisplayToCamera)

        val afterParameters = theCamera.parameters
        val afterSize = afterParameters.previewSize
        if (afterSize != null && (bestPreviewSize!!.x != afterSize.width || bestPreviewSize!!.y != afterSize.height)) {
            bestPreviewSize!!.x = afterSize.width
            bestPreviewSize!!.y = afterSize.height
        }
    }

    // All references to Torch are removed from here, methods, variables...

    private fun findBestPreviewSizeValue(parameters: Camera.Parameters, screenResolution: Point): Point {

        val rawSupportedSizes = parameters.supportedPreviewSizes
        if (rawSupportedSizes == null) {
            val defaultSize = parameters.previewSize
            return Point(defaultSize.width, defaultSize.height)
        }

        // Sort by size, descending
        val supportedPreviewSizes = ArrayList(rawSupportedSizes)
        Collections.sort<Camera.Size>(supportedPreviewSizes, Comparator<Camera.Size> { a, b ->
            val aPixels = a.height * a.width
            val bPixels = b.height * b.width
            if (bPixels < aPixels) {
                return@Comparator -1
            }
            if (bPixels > aPixels) {
                1
            } else 0
        })

        if (Log.isLoggable(TAG, Log.INFO)) {
            val previewSizesString = StringBuilder()
            for (supportedPreviewSize in supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width)
                        .append('x')
                        .append(supportedPreviewSize.height)
                        .append(' ')
            }
        }

        var bestSize: Point? = null
        val screenAspectRatio = screenResolution.x.toFloat() / screenResolution.y.toFloat()

        var diff = java.lang.Float.POSITIVE_INFINITY
        for (supportedPreviewSize in supportedPreviewSizes) {
            val realWidth = supportedPreviewSize.width
            val realHeight = supportedPreviewSize.height
            val pixels = realWidth * realHeight
            if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
                continue
            }

            // This code is modified since We're using portrait mode
            val isCandidateLandscape = realWidth > realHeight
            val maybeFlippedWidth = if (isCandidateLandscape) realHeight else realWidth
            val maybeFlippedHeight = if (isCandidateLandscape) realWidth else realHeight

            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                val exactPoint = Point(realWidth, realHeight)
                return exactPoint
            }
            val aspectRatio = maybeFlippedWidth.toFloat() / maybeFlippedHeight.toFloat()
            val newDiff = Math.abs(aspectRatio - screenAspectRatio)
            if (newDiff < diff) {
                bestSize = Point(realWidth, realHeight)
                diff = newDiff
            }
        }

        if (bestSize == null) {
            val defaultSize = parameters.previewSize
            bestSize = Point(defaultSize.width, defaultSize.height)
        }

        return bestSize
    }

    fun getTorchState(camera: Camera?): Boolean {
        if (camera != null) {
            val parameters = camera.parameters
            if (parameters != null) {
                val flashMode = camera.parameters.flashMode
                return flashMode != null && (Camera.Parameters.FLASH_MODE_ON == flashMode || Camera.Parameters.FLASH_MODE_TORCH == flashMode)
            }
        }
        return false
    }

    fun setTorchEnabled(camera: Camera, enabled: Boolean) {
        val parameters = camera.parameters
        setTorchEnabled(parameters, enabled, false)
        camera.parameters = parameters
    }

    fun setTorchEnabled(parameters: Camera.Parameters, enabled: Boolean, safeMode: Boolean) {
        setTorchEnabled(parameters, enabled)

        if (!safeMode) {
            setBestExposure(parameters, enabled)
        }
    }

    companion object {

        private val TAG = "CameraConfiguration"

        private val MIN_PREVIEW_PIXELS = 470 * 320
        private val MAX_PREVIEW_PIXELS = 1280 * 720
        private val MAX_EXPOSURE_COMPENSATION = 1.5f
        private val MIN_EXPOSURE_COMPENSATION = 0.0f

        private fun findSettableValue(name: String,
                                      supportedValues: Collection<String>?,
                                      vararg desiredValues: String): String? {
            if (supportedValues != null) {
                for (desiredValue in desiredValues) {
                    if (supportedValues.contains(desiredValue)) {
                        return desiredValue
                    }
                }
            }
            return null
        }

        fun setTorchEnabled(parameters: Camera.Parameters,
                            enabled: Boolean) {
            val supportedFlashModes = parameters.supportedFlashModes
            val flashMode: String?
            if (enabled) {
                flashMode = findSettableValue("flash mode",
                        supportedFlashModes,
                        Camera.Parameters.FLASH_MODE_TORCH,
                        Camera.Parameters.FLASH_MODE_ON)
            } else {
                flashMode = findSettableValue("flash mode",
                        supportedFlashModes,
                        Camera.Parameters.FLASH_MODE_OFF)
            }
            if (flashMode != null) {
                if (flashMode == parameters.flashMode) {
                } else {
                    parameters.flashMode = flashMode
                }
            }
        }

        fun setBestExposure(parameters: Camera.Parameters,
                            lightOn: Boolean) {

            val minExposure = parameters.minExposureCompensation
            val maxExposure = parameters.maxExposureCompensation
            val step = parameters.exposureCompensationStep
            if ((minExposure != 0 || maxExposure != 0) && step > 0.0f) {
                // Set low when light is on
                val targetCompensation = if (lightOn) MIN_EXPOSURE_COMPENSATION else MAX_EXPOSURE_COMPENSATION
                var compensationSteps = Math.round(targetCompensation / step)
                val actualCompensation = step * compensationSteps
                // Clamp value:
                compensationSteps = Math.max(Math.min(compensationSteps, maxExposure), minExposure)
                if (parameters.exposureCompensation == compensationSteps) {
                } else {
                    parameters.exposureCompensation = compensationSteps
                }
            } else {
            }
        }
    }
}
