package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.ConsoleSender
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message

class CmdLeave: Command("leave", "", Lang.getString("cmd_leave_description"), Category.MUSIC) {

    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        if(sender is ConsoleSender) {
            sender.sendError("This command is not available for console input.").subscribe()
            return
        }

        sender as DiscordSender

        if(!VoiceHandler.hasConnection(guild!!)) {
            sender.sendError(Lang.getString("channel_not_connected")).subscribe()
            return
        }

        VoiceHandler.closeConnection(guild).subscribe {
            if(it) sender.sendText(Lang.getString("channel_left")).subscribe()
        }
    }
}