package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message

class CmdCloseConnections: Command("closecons", "", "Closes all active voice connections globally", Category.HIDDEN, true) {
    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        if(sender is DiscordSender) return

        if(VoiceHandler.activeConnections.size == 0) {
            sender.sendError("There are currently no active voice connections")
            return
        }

        sender.sendText("Closing all connections...")
        VoiceHandler.activeConnections.keys.forEach { g -> run{
            val connection = VoiceHandler.activeConnections.remove(g)!!
            connection.disconnect()

            VoiceHandler.textChannels.remove(g)

            val queueManager = VoiceHandler.queueManagers.remove(g)!!
            queueManager.stop()
        }}
        sender.sendText("All connections have been closed.")
        // TODO
    }
}