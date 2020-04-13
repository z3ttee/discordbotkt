package de.zitzmanncedric.discordbot.command.handler

import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.config.MainConfig
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object CommandHandler {
    private val logger: Logger = LoggerFactory.getLogger(CommandHandler::class.java)

    val commands: HashMap<String, Command> = HashMap()
    var activeThreads = 0

    fun registerCommands(){
        try {
            val reflections = Reflections(Command::class.java.packageName + ".commands", SubTypesScanner(true))
            val classSet = reflections.getSubTypesOf(Command::class.java)

            classSet.forEach { cmdClass ->
                run {
                    val command: Command = cmdClass.getConstructor().newInstance()
                    commands[command.name] = command
                    logger.info("registerCommands(): Registering '${cmdClass.simpleName}'")
                }
            }

            logger.info("registerCommands(): Registered ${commands.size} " + when(commands.size) {
                1 -> "command successfully."
                else -> "commands successfully."
            })

        } catch (ignored: Exception){
            logger.error("registerCommands(): Failed registering existing commands. The bot may not listen to commands.")
            ignored.printStackTrace()
        }
    }

    fun handleCommand(message: String, sender: Sender) {
        val thread = Thread {
            val content: String = message.removePrefix(MainConfig.getString("general/prefix"))
            val query = ArrayList<String>(content.split(" "))
            val cmdName: String = query.removeAt(0)

            try {
                val command: Command? = commands[cmdName]
                if (command == null) {
                    sender.sendError(":mag: Befehl ` $cmdName ` nicht gefunden.").subscribe()
                } else {
                    command.execute(sender, query)
                }
            } catch (ignored: KotlinNullPointerException){ }

            --activeThreads
            Thread.currentThread().interrupt()
        }
        ++activeThreads
        thread.name = "bot-commandhandler-$activeThreads"
        thread.start()
    }

}