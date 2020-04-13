package de.zitzmanncedric.discordbot.command


abstract class Command(val name: String) {
    abstract fun execute(sender: Sender, args: ArrayList<String>)
}