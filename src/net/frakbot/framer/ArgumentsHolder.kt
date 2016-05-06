package net.frakbot.framer

import org.kohsuke.args4j.Option

private class ArgumentsHolder() {

    @Option(name = "-d", usage = "The name of the device frame to use (e.g., 'nexus_6p')")
    var descriptorName: String = "nexus_6p"
}
