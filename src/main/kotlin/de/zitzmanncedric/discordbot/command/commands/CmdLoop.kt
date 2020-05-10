package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.audio.queue.GuildQueueManager
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message

class CmdLoop: Command("loop", "", "Aktiviert die Wiederholungsschleife", Category.MUSIC) {
    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        val queueManager: GuildQueueManager = VoiceHandler.getQueueManager(guild!!)!!

        queueManager.loop = !queueManager.loop
        sender.sendText(when(queueManager.loop) {
            true -> Lang.getString("audio_looped")
            else -> Lang.getString("audio_loop_deactivated")
        }).subscribe()
    }
}