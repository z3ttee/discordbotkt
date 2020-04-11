package de.zitzmanncedric.discordbot

import de.zitzmanncedric.discordbot.config.MainConfig
import de.zitzmanncedric.discordbot.listener.MessageEventListener
import de.zitzmanncedric.discordbot.listener.ReadyEventListener
import discord4j.core.DiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(BotCore::class.java)

fun main(args: Array<String>) {
    logger.info("main(): Bot is now starting...")
    var token = ""

    try {
        for ((index, arg) in args.withIndex()) {
            if (arg.equals("-token", true)) {
                token = args[index + 1]
            }
        }
    } catch (ex: ArrayIndexOutOfBoundsException){
        logger.error("main(): Some attributes are missing values. Make sure to use a format like '-ATTR_NAME YOUR_VALUE'.")
    }

    if(token.isEmpty()) {
        logger.error("main(): A token is needed. Otherwise the bot cannot authenticate on Discord.")
        logger.error("main(): So without a token the bot will not work. Bot is shutting down now...")
        return
    }

    BotCore(token)
}

class BotCore(private val token: String) {
    var instance: BotCore? = null
    get() {
        if(field == null) field = BotCore(token)
        return field
    }


    val mainConfig = MainConfig("config.yml", "", 1)

    var discordClient: DiscordClient = DiscordClient.create(token)

    init {
        instance = this

        // registering events
        discordClient.eventDispatcher.on(ReadyEvent::class.java).subscribe { (ReadyEventListener()::onReady)(it) }
        discordClient.eventDispatcher.on(MessageCreateEvent::class.java).subscribe {(MessageEventListener()::onMessage)(it) }

        // Loading configs
        mainConfig.create()
        mainConfig.putValue("path1/default2/test", "Hallo Welt")

        // Setting presence


        discordClient.login().block()
    }
}