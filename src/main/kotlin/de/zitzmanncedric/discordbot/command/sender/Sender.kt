package de.zitzmanncedric.discordbot.command.sender

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import reactor.core.publisher.Mono
import java.util.function.Consumer

abstract class Sender(var channel: MessageChannel?) {
    abstract fun sendNormalText(content: String): Mono<Message>
    abstract fun sendText(content: String): Mono<Message>
    abstract fun sendError(content: String): Mono<Message>
    abstract fun sendException(ex: Exception): Mono<Message>
    abstract fun sendTextWitEmbed(content: String, embedSpec: Consumer<in EmbedCreateSpec>): Mono<Message>
}