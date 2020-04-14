package de.zitzmanncedric.discordbot.command.handler

import de.zitzmanncedric.discordbot.command.sender.ConsoleSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ConsoleHandler: Thread("bot-console-input") {
    private val logger: Logger = LoggerFactory.getLogger(ConsoleHandler::class.java)

    override fun run() {
        while (true) {
            val input = readLine()
            CommandHandler.handleCommand(null, input!!,
                ConsoleSender(null)
            )
        }
    }

}