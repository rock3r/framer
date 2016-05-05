package net.frakbot.framer.device

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.RawImage
import net.frakbot.framer.DeveloperError
import java.awt.image.BufferedImage

fun IDevice.obtainScreenshot(bridge: AndroidDebugBridge): BufferedImage {
    if (!bridge.isConnected) {
        throw disconnectedBridge()
    }
    val rawImage = screenshot

    val image = BufferedImage(rawImage.width, rawImage.height, BufferedImage.TYPE_INT_ARGB)
    rawImage.copyPixelDataTo(image)

    return image
}

private fun RawImage.copyPixelDataTo(image: BufferedImage) {
    ensureSameSizeAs(image)

    var index = 0
    var IndexInc = bpp shr 3

    for (y in 0..height - 1) {
        for (x in 0..width - 1) {
            var value = getARGB(index)
            index += IndexInc
            image.setRGB(x, y, value)
        }
    }
}

private fun RawImage.ensureSameSizeAs(otherImage: BufferedImage) {
    if (width != otherImage.width || height != otherImage.height) {
        throw DeveloperError("The raw image size is different from the buffered image's size")
    }
}

fun disconnectedBridge(): Throwable {
    return AdbStateException("ADB is not connected")
}

class AdbStateException(message: String) : RuntimeException(message)
