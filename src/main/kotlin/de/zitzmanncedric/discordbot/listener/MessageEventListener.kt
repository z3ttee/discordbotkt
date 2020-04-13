package de.zitzmanncedric.discordbot.listener

import de.zitzmanncedric.discordbot.command.CommandHandler
import de.zitzmanncedric.discordbot.command.Sender
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class MessageEventListener {

    fun onMessage(event: MessageCreateEvent){
        if(!event.message.content.get().startsWith("ss ", false)) return
        if(event.member.get().isBot) return

        CommandHandler.handleCommand(event.message.content.get(), Sender(false, event.message.channel.block()!!))
    }

    fun onMessageUpdated(event: MessageUpdateEvent) {

    }
}