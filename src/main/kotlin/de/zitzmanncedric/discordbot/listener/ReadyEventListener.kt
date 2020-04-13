package de.zitzmanncedric.discordbot.listener

import de.zitzmanncedric.discordbot.config.MainConfig
import discord4j.core.`object`.presence.Activity
import discord4j.core.`object`.presence.Presence
import discord4j.core.event.domain.lifecycle.ReadyEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ReadyEventListener {
    private val logger: Logger = LoggerFactory.getLogger(ReadyEventListener::class.java)

    fun onReady(event: ReadyEvent) {
        logger.info("onReady(): Updating presence...")
        event.client.updatePresence(Presence.online(Activity.listening(MainConfig.getString("general/prefix")+" help"))).subscribe()
        logger.info("onReady(): Logged in as ${event.self.username}. Bot is now ready")
    }

}