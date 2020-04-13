package de.zitzmanncedric.discordbot.command.sender

import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import reactor.core.publisher.Mono
import java.util.function.Consumer

class DiscordSender(channel: MessageChannel) : Sender(channel) {
    override fun sendText(content: String): Mono<Message> {
        return Messages().sendText(content, channel!!)
    }

    override fun sendError(content: String): Mono<Message> {
        return Messages().sendError(content, channel!!)
    }

    override fun sendException(ex: Exception): Mono<Message> {
        ex.printStackTrace()
        return Messages().sendException(ex, channel!!)
    }

    override fun sendTextWitEmbed(content: String, embedSpec: Consumer<in EmbedCreateSpec>): Mono<Message> {
        return Messages().sendTextWithEmbed(content, embedSpec, channel!!)
    }
}