package de.zitzmanncedric.discordbot.command.commands

import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.Channel
import discord4j.core.`object`.entity.channel.MessageChannel

class CmdBind: Command("bind", "<#textchannel>", Lang.getString("cmd_bind_description"), Category.MUSIC) {

    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        sender as DiscordSender

        if(args.size != 1) {
            sendUsage(sender)
            return
        }

        if(!VoiceHandler.hasConnection(guild!!)) {
            sender.sendError(Lang.getString("channel_not_connected")).subscribe()
            return
        }

        var mentioned: String = args[0]
        if(mentioned.startsWith("<#") && mentioned.endsWith(">")) {
            mentioned = mentioned.removePrefix("<#").removeSuffix(">")
            val channel = guild.getChannelById(Snowflake.of(mentioned)).block()!!

            if(channel.type == Channel.Type.GUILD_TEXT) {
                VoiceHandler.setTextChannel(guild, channel as MessageChannel)
                sender.sendText(Lang.getString("channel_bound").replace("%channel%", channel.name)).subscribe()
            } else {
                sender.sendError(Lang.getString("error_channel_text_required")).subscribe()
            }
        } else {
            sender.sendError(Lang.getString("error_text_mention_required")).subscribe()
        }
    }
}