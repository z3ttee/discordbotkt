package de.zitzmanncedric.discordbot.command.sender

import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.util.function.Consumer

class ConsoleSender(channel: MessageChannel?) : Sender(channel) {
    private val logger: Logger = LoggerFactory.getLogger(CommandHandler::class.java)

    override fun sendNormalText(content: String): Mono<Message> {
        logger.info(content)
        return Mono.empty()
    }

    override fun sendText(content: String): Mono<Message> {
        logger.info(content)
        return Mono.empty()
    }

    override fun sendError(content: String): Mono<Message> {
        logger.warn(content)
        return Mono.empty()
    }

    override fun sendException(ex: Exception): Mono<Message> {
        logger.error("An error occurred: ")
        ex.printStackTrace()
        return Mono.empty()
    }

    override fun sendTextWitEmbed(content: String, embedSpec: Consumer<in EmbedCreateSpec>): Mono<Message> {
        return sendText(content)
    }

}