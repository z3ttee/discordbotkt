package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message

class CmdStop: Command("stop", "", Lang.getString("cmd_stop_description"), Category.MUSIC) {
    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        sender as DiscordSender

        if(!VoiceHandler.hasConnection(guild!!)) {
            sender.sendError(Lang.getString("channel_not_connected")).subscribe()
            return
        }

        if(VoiceHandler.getQueueManager(guild)!!.audioPlayer.playingTrack == null) {
            sender.sendError(Lang.getString("audio_not_sending")).subscribe()
            return
        }

        VoiceHandler.getQueueManager(guild)!!.stop().subscribe()
        sender.sendText(Lang.getString("audio_stopped")).subscribe()
    }
}