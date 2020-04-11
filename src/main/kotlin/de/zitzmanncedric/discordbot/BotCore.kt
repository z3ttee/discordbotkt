package de.zitzmanncedric.discordbot

import de.zitzmanncedric.discordbot.config.MainConfig
import de.zitzmanncedric.discordbot.listener.MessageEventListener
import de.zitzmanncedric.discordbot.listener.ReadyEventListener
import discord4j.core.DiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent

fun main(){
    BotCore("Njk2ODAwNjc0MjIyMTc4MzE1.XpBe-w.MY_QhTf7sbivabCR3q8J2iBspQ4")
}

class BotCore(private val token: String) {
    var instance: BotCore? = null
    get() {
        if(field == null) field =
            BotCore(token)
        return field
    }

    var discordClient: DiscordClient = DiscordClient.create(token)

    init {
        instance = this

        // registering events
        discordClient.eventDispatcher.on(ReadyEvent::class.java).subscribe { (ReadyEventListener()::onReady)(it) }
        discordClient.eventDispatcher.on(MessageCreateEvent::class.java).subscribe {(MessageEventListener()::onMessage)(it) }

        // Loading configs
        MainConfig("config.yml", "", 1)::create

        discordClient.login().block()
    }
}