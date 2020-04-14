package de.zitzmanncedric.discordbot.command

import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message

abstract class Command(val name: String, val usage: String, val description: String, val category: Category) {
    abstract fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>)

    fun sendUsage(sender: Sender){
        sender.sendError(Lang.getString("error_cmd_usage").replace("%usage%", "$name $usage")).subscribe()
    }
}