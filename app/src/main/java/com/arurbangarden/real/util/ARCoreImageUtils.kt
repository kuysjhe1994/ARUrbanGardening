package com.arurbangarden.real.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import com.google.ar.core.Image
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Utility for converting ARCore Image to Bitmap
 * Handles YUV_420_888 format from ARCore camera
 */
object ARCoreImageUtils {
    
    /**
     * Convert ARCore Image to Bitmap
     * ARCore provides images in YUV_420_888 format
     */
    fun convertImageToBitmap(image: Image): Bitmap? {
        return try {
            // ARCore typically provides YUV_420_888 format
            // Check format and convert accordingly
            when (image.format) {
                ImageFormat.YUV_420_888 -> {
                    convertYuv420888ToBitmap(image)
                }
                ImageFormat.JPEG -> {
                    // Direct JPEG conversion
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
                else -> {
                    // Default: Try YUV conversion (most common for ARCore)
                    // ARCore camera images are typically YUV_420_888
                    if (image.planes.size >= 3) {
                        convertYuv420888ToBitmap(image)
                    } else {
                        // Fallback: try JPEG if only one plane
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun convertYuv420888ToBitmap(image: Image): Bitmap? {
        return try {
            val yBuffer = image.planes[0].buffer
            val uBuffer = image.planes[1].buffer
            val vBuffer = image.planes[2].buffer
            
            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()
            
            val nv21 = ByteArray(ySize + uSize + vSize)
            
            // Y plane
            yBuffer.get(nv21, 0, ySize)
            
            // Interleave U and V
            val uvBuffer = ByteArray(uSize + vSize)
            vBuffer.get(uvBuffer, 0, vSize)
            uBuffer.get(uvBuffer, vSize, uSize)
            
            // Copy UV data to NV21 format (V first, then U)
            System.arraycopy(uvBuffer, 0, nv21, ySize, uvBuffer.size)
            
            val yuvImage = YuvImage(
                nv21,
                ImageFormat.NV21,
                image.width,
                image.height,
                null
            )
            
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(
                Rect(0, 0, image.width, image.height),
                90, // Quality
                out
            )
            
            val jpegArray = out.toByteArray()
            BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Alternative: Convert using RenderScript (if available)
     * More efficient but requires RenderScript support
     */
    fun convertImageToBitmapOptimized(image: Image): Bitmap? {
        // For now, use standard conversion
        // In production, can use RenderScript for better performance
        return convertImageToBitmap(image)
    }
}

