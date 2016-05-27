package net.frakbot.framer

import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.Option
import java.util.*

internal class ArgumentsHolder() {

    @Option(name = "-d", usage = "The name of the device frame to use (e.g., 'nexus_6p')")
    var descriptorName: String = ""

    @Option(name = "-noframe", usage = "If true, skips framing (-d is ignored in that case)")
    var noFrame: Boolean = false

    @Argument
    var arguments = ArrayList<String>()

}

internal fun normalizeDescriptorName(descriptorName: String?): String {
    val lowerCase = descriptorName?.toLowerCase(Locale.US) ?: ""
    return Regex("\\W").replace(lowerCase, "_")
}
