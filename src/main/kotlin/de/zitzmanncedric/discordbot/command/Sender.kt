package de.zitzmanncedric.discordbot.command

import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.entity.TextChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

class Sender(private val isConsole: Boolean, private val channel: MessageChannel?){
    private val logger: Logger = LoggerFactory.getLogger(CommandHandler::class.java)

    fun sendText(content: String): Mono<Message>? {
        if(isConsole) {
            logger.info(content)
            return null
        }

        return Messages().sendText(content, channel!!)
    }

    fun sendError(content: String): Mono<Message>? {
        if(isConsole) {
            logger.warn(content)
            return null
        }

        return Messages().sendError(content, channel!!)
    }

    fun sendException(ex: Exception): Mono<Message>? {
        if(isConsole) {
            logger.error("An error occured executing command: ")
            ex.printStackTrace()
            return null
        }

        return Messages().sendException(ex, channel!!)
    }
}