package net.frakbot.framer

import net.frakbot.framer.device.obtainScreenshot
import java.io.File
import javax.imageio.ImageIO

private class FramerMain(val args: ArgumentsHolder) {

    val logger = Logger.getInstance()

    fun run() {
        val descriptor = getDescriptor(args.descriptorName.normalize())
        val painter = DeviceFramePainter(descriptor)

        val bridge = connectToAdb()

        val devices = bridge.devices

        val device = devices[0]     // TODO properly select devices
        val screenshot = device.obtainScreenshot(bridge)

        releaseAdbConnection()

        logger.info("Framing screenshot (${screenshot.width}x${screenshot.height}, ${screenshot.orientation.name})")
        val composite = painter.paint(screenshot, screenshot.orientation, true, true)

        val outputFile = File("/Users/rock3r/Desktop/tmp/test-framed.png")       // TODO specify output
        ImageIO.write(composite, "png", outputFile)
        logger.info("Framed screenshot written to ${outputFile.absolutePath}")
    }
}
