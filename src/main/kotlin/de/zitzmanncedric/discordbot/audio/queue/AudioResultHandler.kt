package de.zitzmanncedric.discordbot.audio.queue

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.language.Lang
import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.`object`.entity.Guild

class AudioResultHandler(val guild: Guild, val player: AudioPlayer): AudioLoadResultHandler {

    override fun loadFailed(exception: FriendlyException?) {
        if (exception != null) {
            Messages.sendException(exception, VoiceHandler.getTextChannel(guild)!!).subscribe()
        }
    }

    override fun trackLoaded(track: AudioTrack?) {
        player.playTrack(track)
    }

    override fun noMatches() {
        Messages.sendError(Lang.getString("error_404"), VoiceHandler.getTextChannel(guild)!!).subscribe()
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {

    }
}