package de.zitzmanncedric.discordbot.message

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.entity.TextChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import reactor.core.publisher.Mono
import java.awt.Color
import java.util.function.Consumer

class Messages {

    fun sendText(content: String, channel: MessageChannel): Mono<Message> {
        return channel.createMessage(createText(content))
    }
    fun sendError(content: String, channel: MessageChannel): Mono<Message> {
        return channel.createMessage(createError(content))
    }
    fun sendException(ex: Exception, channel: MessageChannel): Mono<Message> {
        return channel.createMessage(createException(ex))
    }
    fun sendTextWithEmbed(content: String, embedSpec: Consumer<in EmbedCreateSpec>, channel: MessageChannel): Mono<Message> {
        return channel.createMessage { messageSpec -> run {
            messageSpec.setContent("**$content**")
            messageSpec.setEmbed(embedSpec)
        }}
    }

    fun createText(content: String): Consumer<in MessageCreateSpec> {
        return Consumer { it.setContent("**$content**") }
    }
    fun createError(content: String): Consumer<in MessageCreateSpec> {
        return Consumer { it.setContent(":no_entry: **$content**") }
    }
    fun createException(ex: Exception): Consumer<in MessageCreateSpec> {
        return Consumer { message -> run {
            message.setContent(":no_entry: **Es ist ein Fehler aufgetreten: **")
            message.setEmbed { embed -> run {
                embed.setDescription(ex.message!!)
                embed.setColor(Color.RED)
            }}
        }}
    }

}