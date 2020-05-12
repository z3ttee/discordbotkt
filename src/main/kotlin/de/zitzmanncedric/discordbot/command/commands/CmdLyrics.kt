package de.zitzmanncedric.discordbot.command.commands

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.audio.lyrics.GeniusAPI
import de.zitzmanncedric.discordbot.audio.lyrics.GeniusLyrics
import de.zitzmanncedric.discordbot.command.Category
import de.zitzmanncedric.discordbot.command.Command
import de.zitzmanncedric.discordbot.command.sender.Sender
import de.zitzmanncedric.discordbot.language.Lang
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message

class CmdLyrics: Command("lyrics", "(query)", "Sucht passende lyrics auf Genius.com", Category.MUSIC) {

    override fun execute(sender: Sender, message: Message?, guild: Guild?, args: ArrayList<String>) {
        val queueManager = VoiceHandler.getQueueManager(guild!!)

        var lyrics: GeniusLyrics? = null

        if(args.size == 0){
            if(queueManager == null) {
                sendUsage(sender)
                return
            }

            val track: AudioTrack = queueManager.audioPlayer.playingTrack
            lyrics = GeniusAPI.getLyricsFor(track.info.title)
        }

        if(args.size > 0){
            lyrics = GeniusAPI.getLyricsFor(args.joinToString(" "))
        }

        if(lyrics != null) {
            when (lyrics.responseCode) {
                200L -> {
                    var chunkedResult = lyrics.text.chunked(1900)
                    val firstLine: String = chunkedResult[0]
                    chunkedResult = chunkedResult.subList(1, chunkedResult.size)

                    sender.sendNormalText("**${Lang.getString("paragraph_genius_success")}**\n\n$firstLine").block()

                    for (msg in chunkedResult) {
                        sender.sendNormalText(msg).block()
                    }
                }
                404L -> {
                    sender.sendException(Exception("404 Not found")).subscribe()
                }
                else -> {
                    sender.sendException(Exception(lyrics.errorMessage)).subscribe()
                }
            }
        }
    }
}