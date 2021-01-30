package de.zitzmanncedric.discordbot.message

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Mono
import java.util.function.Consumer

object Messages {

    fun sendNormalText(content: String, channel: MessageChannel): Mono<Message> {
        return channel.createMessage(content)
    }
    fun sendText(content: String, channel: MessageChannel): Mono<Message> {
        return channel.createMessage(createText(content))
    }
    fun sendError(content: String, channel: MessageChannel): Mono<Message> {
        return channel.createMessage(createError(content))
    }
    fun sendException(ex: Exception, channel: MessageChannel): Mono<Message> {
        ex.printStackTrace()
        return channel.createMessage(createException(ex))
    }
    fun sendTextWithEmbed(content: String, embedSpec: Consumer<in EmbedCreateSpec>, channel: MessageChannel): Mono<Message> {
        return channel.createMessage { messageSpec -> run {
            messageSpec.setContent("**$content**")
            messageSpec.setEmbed(embedSpec)
        }}
    }

    private fun createText(content: String): Consumer<in MessageCreateSpec> {
        return Consumer { it.setContent("**$content**") }
    }
    private fun createError(content: String): Consumer<in MessageCreateSpec> {
        return Consumer { it.setContent(":no_entry: **$content**") }
    }
    private fun createException(ex: Exception): Consumer<in MessageCreateSpec> {
        return Consumer { message -> run {
            message.setContent(":no_entry: **Es ist ein Fehler aufgetreten: **")
            message.setEmbed { embed -> run {
                embed.setDescription(ex.message!!)
                embed.setColor(Color.RED)
            }}
        }}
    }

}