package qsos.lib.base.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi

/**
 * @author : 华清松
 * @description : Bitmap 工具类
 */
class BitmapUtils {
    companion object {

        /**图片任意形状的放大缩小*/
        fun zoomToFixShape(bitmap: Bitmap, iconSize: Int): Bitmap {
            var size = iconSize.toFloat()
            val tempBitmap: Bitmap
            val bitH = bitmap.height.toFloat()
            val bitW = bitmap.width.toFloat()
            val mMatrix = Matrix()
            if (size < 1f) {
                size = 20f
            }
            val minBit = (if (bitH <= bitW) bitH else bitW)
            val scold = if (minBit < size) size / minBit else minBit / size
            mMatrix.reset()
            mMatrix.postScale(scold, scold)
            tempBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitW.toInt(), bitH.toInt(), mMatrix, true)

            return tempBitmap
        }

        /**图片模糊处理*/
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun blurBitmap(context: Context,bitmap: Bitmap, radius: Float): Bitmap {
            // Create renderscript
            val rs = RenderScript.create(context)
            // Create allocation from Bitmap
            val allocation = Allocation.createFromBitmap(rs, bitmap)
            val t = allocation.type
            // Create allocation with the same type
            val blurredAllocation = Allocation.createTyped(rs, t)
            // Create script
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            // Set blur radius (maximum 25.0)
            blurScript.setRadius(radius)
            // Set input for script
            blurScript.setInput(allocation)
            // Call script for output allocation
            blurScript.forEach(blurredAllocation)
            // Copy script result into bitmap
            blurredAllocation.copyTo(bitmap)
            // Destroy everything to free memory
            allocation.destroy()
            blurredAllocation.destroy()
            blurScript.destroy()
            rs.destroy()
            return bitmap
        }
    }
}