package de.zitzmanncedric.discordbot.listener

import de.zitzmanncedric.discordbot.api.TSMedia
import de.zitzmanncedric.discordbot.command.handler.CommandHandler
import de.zitzmanncedric.discordbot.command.sender.DiscordSender
import de.zitzmanncedric.discordbot.config.MainConfig
import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import discord4j.core.spec.EmbedCreateSpec
import org.json.simple.JSONObject
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

class MessageEventListener {

    fun onMessage(event: MessageCreateEvent){
        if(event.message.attachments.isNotEmpty()) {
            val member = event.member.get()

            // Only upload 3 attachments at the same time
            var attachments = event.message.attachments.toCollection(ArrayList())
            if(event.message.attachments.size >= 3) {
                attachments = attachments.subList(0, 2).toCollection(ArrayList())
            }

            for(attachment in attachments) {
                val extensionParts = attachment.filename.split(".")
                val extension = extensionParts[extensionParts.size-1]
                val allowedExtensions = listOf("mpeg", "mp4", "mov", "webm", "ogg")

                if(!allowedExtensions.contains(extension)) {
                    return
                }

                TSMedia.sendUploadRequest(attachment.url, event.member.get()).subscribe { result ->
                    if(result.status.code == 200) {
                        val data = result.data as JSONObject

                        Messages.sendTextWithEmbed(":calling: :white_check_mark:  Ein neues Video wurde auf TSMedia hochgeladen:", Consumer { embed -> run {
                            embed.setTitle(data.getOrDefault("title", "NaN").toString())
                            embed.setUrl(MainConfig.getString("tsmedia/page")+"/watch/"+data.getOrDefault("id", "NaN").toString())
                            embed.setDescription("Klicke auf den Link und sieh dir das Video an!")
                            embed.setImage(MainConfig.getString("tsmedia/baseURL")+"/uploads/thumbnails/"+data.getOrDefault("id", "NaN")+".jpg")
                            embed.setFooter("Hochgeladen von "+member.displayName+"#"+member.discriminator, member.avatarUrl)
                        }}, event.message.channel.block()!!).block()
                    } else {
                        when(result.status.message) {
                            "video exists" -> Messages.sendError("Der Upload von ``${attachment.filename}`` zu TSMedia ist fehlgeschlagen: ``Das Video existiert bereits``.", event.message.channel.block()!!).block()
                            "unsupported encoding" -> Messages.sendError("Der Upload von ``${attachment.filename}`` zu TSMedia ist fehlgeschlagen: ``Das Videoformat wird nicht unterstützt``.", event.message.channel.block()!!).block()
                            "creator not found" -> Messages.sendError("Der Upload von ``${attachment.filename}`` zu TSMedia ist fehlgeschlagen: ``Das Video konnte keinem Benutzer zugewiesen werden``.", event.message.channel.block()!!).block()
                            else -> Messages.sendError("Der Upload von ``${attachment.filename}`` zu TSMedia ist fehlgeschlagen", event.message.channel.block()!!).block()
                        }
                    }
                }
            }
            return
        }

        try {
            if (!event.message.content.get().startsWith("ss ", false)) return
            if (event.member.get().isBot) return

            val typing = event.message.channel.block()
            typing!!.type().block()

            CommandHandler.handleCommand(event.guild.block(), event.message, event.message.content.get(), DiscordSender(event.member.get(), event.message.channel.block()!!))
        } catch (ignored: Exception){ }
    }

    fun onMessageUpdated(event: MessageUpdateEvent) {

    }
}