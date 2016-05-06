package net.frakbot.framer

import org.kohsuke.args4j.CmdLineParser

fun main(args: Array<String>) {
    val argumentsHolder = ArgumentsHolder()
    val argsParser = CmdLineParser(argumentsHolder)

    try {
        argsParser.parse(args)
    } catch(e: Exception) {
        Logger.getInstance().error("Error while parsing the arguments.")
        argsParser.printUsage(System.err)
        System.exit(0)
    }

    FramerMain(argumentsHolder).run()
}

private fun CmdLineParser.parse(args: Array<String>) = parseArgument(args.toMutableList())
