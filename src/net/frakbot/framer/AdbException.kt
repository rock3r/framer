package net.frakbot.framer

class AdbException internal constructor(message: String) : RuntimeException(message) {
}


fun failedToConnectToAdb(): AdbException = AdbException("Connection to ADB failed.")

fun adbConnectionTimedOut(): AdbException = AdbException("Timed out attempting to connect to ADB.")
