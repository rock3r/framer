package net.frakbot.framer

import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.Option
import java.util.*

internal class ArgumentsHolder() {

    @Option(name = "-d", usage = "The name of the device frame to use (e.g., 'nexus_6p')")
    var descriptorName: String = "nexus_6p"

    @Argument
    var arguments = ArrayList<String>()

    val normalizedDescriptorName: String
        get() {
            val lowerCase = descriptorName.toLowerCase(Locale.US)
            return lowerCase.replace(Regex.fromLiteral("[^\\w]"), "_")
        }
}
