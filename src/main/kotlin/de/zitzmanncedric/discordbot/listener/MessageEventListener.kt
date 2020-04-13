package de.zitzmanncedric.discordbot.listener

import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageUpdateEvent

class MessageEventListener {

    fun onMessage(event: MessageCreateEvent){
        if(!event.message.content.get().startsWith("ss ", false)) return
        if(event.member.get().isBot) return

        CommandHandler.handleCommand(event.message.content.get(), DiscordSender(event.message.channel.block()!!))
    }

    fun onMessageUpdated(event: MessageUpdateEvent) {

    }
}