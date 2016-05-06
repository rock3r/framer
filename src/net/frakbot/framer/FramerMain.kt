package net.frakbot.framer

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.Client
import com.android.ddmlib.IDevice
import net.frakbot.framer.device.ScreenOrientation
import net.frakbot.framer.device.obtainScreenshot
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

internal class FramerMain(val args: ArgumentsHolder) {

    val logger = Logger.getInstance()

    fun run() {
        val bridge = connectToAdb()

        val devices = bridge.devices
        val device = devices[0]     // TODO properly select devices

        val screenshot = device.obtainScreenshot(bridge)

        val descriptor = getDeviceArtDescriptorFromArgsOrFallbackUsing(device)
        val painter = DeviceFramePainter(descriptor)

        releaseAdbConnection()

        logger.info("Framing screenshot (${screenshot.width}x${screenshot.height}, ${screenshot.orientation.name})")
        val composite = painter.paint(screenshot, screenshot.orientation, true, true)

        val outputFile = File("/Users/rock3r/Desktop/tmp/test-framed.png")       // TODO specify output
        ImageIO.write(composite, "png", outputFile)
        logger.info("Framed screenshot written to ${outputFile.absolutePath}")
    }

    private fun getDeviceArtDescriptorFromArgsOrFallbackUsing(device: IDevice): DeviceArtDescriptor {
        var descriptorName = normalizeDescriptorName(args.descriptorName)

        if (descriptorName.isEmpty()) {
            descriptorName = normalizeDescriptorName(device.getDeviceName())
        }

        return getDescriptor(descriptorName)
                ?: throw IllegalArgumentException("The device '$descriptorName' doesn't exist")
    }

    private fun IDevice.getDeviceName(): String? {
        return getProperty("ro.product.model")
    }

    private fun getDescriptor(deviceName: String): DeviceArtDescriptor? {
        val descriptors = DeviceArtDescriptor.getDescriptors(null)

        return descriptors.find { it.id.equals(deviceName) }
    }

    private fun connectToAdb(): AndroidDebugBridge {
        val logger = Logger.getInstance()

        logger.info("Connecting to ADB...")
        AndroidDebugBridge.init(false)

        val bridge = AndroidDebugBridge.createBridge("/Users/rock3r/android-sdk-macosx/platform-tools/adb", true)       // TODO properly find ADB
        while (!bridge.isConnected) {
            try {
                TimeUnit.MILLISECONDS.sleep(200)
            } catch (e: InterruptedException) {
                // if cancelled, don't wait for connection and return immediately
                throw RuntimeException("Timed out attempting to connect to adb")
            }
        }

        logger.info("ADB connected. fetching devices list...")

        if (!bridge.hasInitialDeviceList()) {
            val semaphore = Semaphore(0)
            val listener: (Client, Int) -> Unit = { client, i -> semaphore.release() }
            AndroidDebugBridge.addClientChangeListener(listener)

            semaphore.acquire()
            AndroidDebugBridge.removeClientChangeListener(listener)
        }

        return bridge
    }

    private fun releaseAdbConnection() {
        AndroidDebugBridge.disconnectBridge()
        AndroidDebugBridge.terminate()

        Logger.getInstance().info("ADB connection released")
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

}
