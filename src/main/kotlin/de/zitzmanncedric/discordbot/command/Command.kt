package de.zitzmanncedric.discordbot.command

import de.zitzmanncedric.discordbot.command.sender.Sender

abstract class Command(val name: String, val usage: String, val description: String, val category: Category) {
    abstract fun execute(sender: Sender, args: ArrayList<String>)
}