package de.zitzmanncedric.discordbot.listener

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageEventListener {
    private val logger: Logger = LoggerFactory.getLogger(MessageEventListener::class.java)

    fun onMessage(event: MessageCreateEvent){
        if(!event.message.content.get().startsWith("ss ", false)) return
        if(event.member.get().isBot) return

    }

    private fun onGuildMessageCreate(event: MessageEvent){

    }
}