package net.frakbot.framer

import com.android.ninepatch.NinePatch
import net.frakbot.framer.device.ScreenOrientation
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class DeviceFramePainter(val descriptor: DeviceArtDescriptor) {

    companion object {
        private val EPSILON = 1e-5f
        private val TRANSPARENT = Color(0, 0, 0, 0)
    }

    fun paint(originalImage: BufferedImage,
              orientation: ScreenOrientation,
              drawShadow: Boolean = true,
              drawGlare: Boolean = true): BufferedImage {

        if (!descriptor.canFrameImage(originalImage, orientation))
            return originalImage

        val shadowFile = descriptor.getDropShadow(orientation)!!
        val frameFile = descriptor.getFrame(orientation)!!
        val glareFile = descriptor.getReflectionOverlay(orientation)!!

        var g2d: Graphics2D? = null
        var screenshotImage: BufferedImage = originalImage

        try {
            var compositeImage = ImageIO.read(frameFile)
            g2d = compositeImage.createGraphics()

            val screen = descriptor.getScreenSize(orientation) // Size of screen in ninepatch; will be stretched
            val frameSize = descriptor.getFrameSize(orientation) // Size of full ninepatch, including stretchable screen area
            val screenPos = descriptor.getScreenPos(orientation)
            val stretchable = descriptor.isStretchable
            if (stretchable) {
                assert(screen != null)
                assert(frameSize != null)
                val newWidth = originalImage.width + frameSize!!.width - screen!!.width
                val newHeight = originalImage.height + frameSize.height - screen.height
                compositeImage = stretchImage(compositeImage, newWidth, newHeight)
            } else if (screen!!.width < originalImage.width) {
                // if the frame isn't stretchable, but is smaller than the image, then scale down the image
                val scale = screen.width.toDouble() / originalImage.width
                if (Math.abs(scale - 1.0) > EPSILON) {
                    screenshotImage = ImageUtils.scale(originalImage, scale, scale)
                }
            }

            if (drawShadow) {
                drawImage(shadowFile, g2d, compositeImage, stretchable)
            }

            // If the device art has a mask, make sure that the image is clipped by the mask
            val maskFile = descriptor.getMask(orientation)
            if (maskFile != null) {
                val mask = ImageIO.read(maskFile)

                // Render the current image on top of the mask using it as the alpha composite
                val maskG2d = mask.createGraphics()
                maskG2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_IN)
                maskG2d.drawImage(screenshotImage, screenPos.x, screenPos.y, null)
                maskG2d.dispose()

                // Render the masked image to the destination
                g2d!!.drawImage(mask, 0, 0, null)
            } else {
                g2d!!.drawImage(originalImage, screenPos.x, screenPos.y, null)
            }

            if (drawGlare) {
                drawImage(glareFile, g2d, compositeImage, stretchable)
            }
            return compositeImage
        } catch (e: IOException) {
            return originalImage
        } finally {
            g2d?.dispose()
        }
    }

    @Throws(IOException::class)
    private fun drawImage(imageFile: File, g2d: Graphics2D, bg: BufferedImage, stretchable: Boolean) {
        var shadowImage = ImageIO.read(imageFile)
        if (stretchable) {
            shadowImage = stretchImage(shadowImage, bg.width, bg.height)
        }
        g2d.drawImage(shadowImage, 0, 0, null, null)
    }

    private fun stretchImage(image: BufferedImage, width: Int, height: Int): BufferedImage {
        val composite = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = composite.createGraphics()
        g.color = TRANSPARENT
        g.fillRect(0, 0, composite.width, composite.height)

        val ninePatch = NinePatch.load(image, true, false)!!
        ninePatch.draw(g, 0, 0, width, height)
        g.dispose()
        return composite
    }

}
