package net.frakbot.framer

import net.frakbot.framer.device.ScreenOrientation
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val descriptor = getDescriptor("nexus_6p")
    var painter = DeviceFramePainter(descriptor)

    val originalImage = ImageIO.read(File("/Users/rock3r/Desktop/tmp/test.png"))
    val composite = painter.paint(originalImage, originalImage.orientation, true, true)

    ImageIO.write(composite, "png", File("/Users/rock3r/Desktop/tmp/test-framed.png"))
}

private fun getDescriptor(deviceName: String): DeviceArtDescriptor {
    val descriptors = DeviceArtDescriptor.getDescriptors(null)

    return descriptors.find { it.id.equals(deviceName) }
            ?: throw IllegalArgumentException("Descriptor with ID '$deviceName' doesn't exist")
}

private val BufferedImage.orientation: ScreenOrientation
    get() {
        if (height > width) {
            return ScreenOrientation.PORTRAIT
        } else if (height == width) {
            return ScreenOrientation.SQUARE
        } else {
            return ScreenOrientation.LANDSCAPE
        }
    }
