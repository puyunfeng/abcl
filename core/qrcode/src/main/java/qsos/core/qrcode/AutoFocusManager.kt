package qsos.core.qrcode

import android.annotation.SuppressLint
import android.hardware.Camera
import android.os.AsyncTask
import java.util.*
import java.util.concurrent.RejectedExecutionException

internal class AutoFocusManager(private val camera: Camera) : Camera.AutoFocusCallback {
    private var autofocusIntervalMs = DEFAULT_AUTO_FOCUS_INTERVAL_MS

    private var stopped: Boolean = false
    private var focusing: Boolean = false
    private val useAutoFocus: Boolean
    private var outstandingTask: AsyncTask<*, *, *>? = null

    init {
        val currentFocusMode = camera.parameters.focusMode
        useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode)
        start()
    }

    @Synchronized
    override fun onAutoFocus(success: Boolean, theCamera: Camera) {
        focusing = false
        autoFocusAgainLater()
    }

    fun setAutofocusInterval(autofocusIntervalMs: Long) {
        if (autofocusIntervalMs <= 0) {
            throw IllegalArgumentException("AutoFocusInterval must be greater than 0.")
        }
        this.autofocusIntervalMs = autofocusIntervalMs
    }

    @SuppressLint("NewApi")
    @Synchronized
    private fun autoFocusAgainLater() {
        if (!stopped && outstandingTask == null) {
            val newTask = AutoFocusTask()
            try {
                newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                outstandingTask = newTask
            } catch (ree: RejectedExecutionException) {
            }
        }
    }

    @Synchronized
    fun start() {
        if (useAutoFocus) {
            outstandingTask = null
            if (!stopped && !focusing) {
                try {
                    camera.autoFocus(this)
                    focusing = true
                } catch (re: RuntimeException) {
                    autoFocusAgainLater()
                }

            }
        }
    }

    @Synchronized
    private fun cancelOutstandingTask() {
        if (outstandingTask != null) {
            if (outstandingTask!!.status != AsyncTask.Status.FINISHED) {
                outstandingTask!!.cancel(true)
            }
            outstandingTask = null
        }
    }

    @Synchronized
    fun stop() {
        stopped = true
        if (useAutoFocus) {
            cancelOutstandingTask()
            // Doesn't hurt to call this even if not focusing
            try {
                camera.cancelAutoFocus()
            } catch (re: RuntimeException) {
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AutoFocusTask : AsyncTask<Any, Any, Any>() {
        override fun doInBackground(vararg voids: Any): Any? {
            try {
                Thread.sleep(autofocusIntervalMs)
            } catch (e: InterruptedException) {
                // continue
            }

            start()
            return null
        }
    }

    companion object {

        const val DEFAULT_AUTO_FOCUS_INTERVAL_MS = 200L

        val FOCUS_MODES_CALLING_AF: MutableCollection<String>

        init {
            FOCUS_MODES_CALLING_AF = ArrayList(2)
            FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO)
            FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO)
        }
    }
}
