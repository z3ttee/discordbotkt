package de.zitzmanncedric.discordbot.command

import de.zitzmanncedric.discordbot.command.sender.Sender
import discord4j.core.`object`.entity.Guild

abstract class Command(val name: String, val usage: String, val description: String, val category: Category) {
    abstract fun execute(sender: Sender, guild: Guild, args: ArrayList<String>)
}