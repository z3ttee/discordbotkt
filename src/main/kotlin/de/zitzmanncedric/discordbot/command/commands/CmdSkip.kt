package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.audio.queue.GuildQueueManager
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import kotlin.math.abs

class CmdSkip: Command("skip", "(amount)", Lang.getString("cmd_skip_description"), Category.MUSIC) {
    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        sender as DiscordSender

        if(!VoiceHandler.hasConnection(guild!!)) {
            sender.sendError(Lang.getString("channel_not_connected")).subscribe()
            return
        }

        val queueManager: GuildQueueManager = VoiceHandler.getQueueManager(guild)!!

        if(queueManager.queue.isEmpty()) {
            if(queueManager.audioPlayer.playingTrack != null){
                queueManager.skip(1).subscribe()
                return
            }

            sender.sendError(Lang.getString("error_nothing_to_skip")).subscribe()
            return
        }

        when (args.size) {
            0 -> {
                queueManager.skip(1).subscribe()
            }
            1 -> {
                try {
                    var amount: Int = args[0].toInt()

                    if(amount < 0) {
                        amount = abs(amount)
                    }

                    if(amount > 10) {
                        sender.sendError(Lang.getString("error_check_skip_amount").replace("%amount%", 10.toString())).subscribe()
                        return
                    }

                    queueManager.skip(amount).subscribe()
                } catch (ex: NumberFormatException){
                    sender.sendError(Lang.getString("error_check_input_for").replace("%usage%", usage)).subscribe()
                }
            }
            else -> {
                sendUsage(sender)
            }
        }
    }
}