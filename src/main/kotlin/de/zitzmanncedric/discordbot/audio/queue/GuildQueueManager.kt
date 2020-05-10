package de.zitzmanncedric.discordbot.audio.queue

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import de.zitzmanncedric.discordbot.BotCore
import de.zitzmanncedric.discordbot.audio.AudioProvider
import de.zitzmanncedric.discordbot.audio.VoiceHandler
import de.zitzmanncedric.discordbot.language.Lang
import de.zitzmanncedric.discordbot.message.Messages
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import reactor.core.publisher.Mono
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.properties.Delegates

class GuildQueueManager(val guild: Guild, val audioPlayer: AudioPlayer): AudioEventAdapter(), AudioLoadResultHandler {

    val audioProvider: discord4j.voice.AudioProvider = AudioProvider(audioPlayer)
    //TODO: val requests: HashMap<AudioTrack, Member> = HashMap()
    var queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()
    var lastInfoMessage: Message? = null
    var shuffle: Boolean = false
    var loop: Boolean = false

    init {
        AudioSourceManagers.registerRemoteSources(VoiceHandler.playerManager)
        //TODO: AudioSourceManagers.registerLocalSource(playerManager)
        audioPlayer.addListener(this)
    }

    private fun enqueue(track: AudioTrack): Mono<Void> {
        return Mono.create {
            if (!audioPlayer.startTrack(track, true)) {
                queue.offer(track)
                sendEnqueuedInfo(track)
                it.success()
            }
        }
    }

    private fun enqueuePlaylist(list: AudioPlaylist) {
        list.tracks.forEach { track -> run {
            queue.offer(track)
        }}.also {
            sendEnqueuedListInfo(list.tracks.size)
        }

        if(audioPlayer.playingTrack == null) {
            next()
        }
    }

    fun next(){
        if(queue.isNotEmpty()) {
            if(shuffle) {
                audioPlayer.startTrack(queue.random(), false)
            } else {
                audioPlayer.startTrack(queue.take(), false)
            }
        } else {
            audioPlayer.stopTrack()
        }
    }

    fun skip(amount: Int): Mono<Void> {
        return Mono.create {
            val count = when {
                queue.size <= amount -> queue.size
                else -> amount
            }

            if(count > 1) {
                queue = LinkedBlockingQueue(queue.filterIndexed { index, _ -> index+1 !in 0..amount })
            }

            next()
            Messages.sendText(Lang.getString("audio_skipped"), VoiceHandler.getTextChannel(guild)!!).subscribe()
            it.success()
        }
    }

    fun stop(): Mono<Void> {
        return Mono.create {
            queue.clear()
            audioPlayer.stopTrack()
            it.success()
        }
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        sendInfoMessage(track!!)
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if(track != null && loop) {
            val loopedTrack: AudioTrack = track.makeClone()
            audioPlayer.startTrack(loopedTrack, false)
            return
        }

        if(endReason!!.mayStartNext) {
            next()
        }
        // requests.remove(track)
    }

    override fun onPlayerPause(player: AudioPlayer?) {
        Messages.sendText(Lang.getString("audio_paused"), VoiceHandler.getTextChannel(guild)!!).subscribe()
    }

    override fun onPlayerResume(player: AudioPlayer?) {
        Messages.sendText(Lang.getString("audio_resumed"), VoiceHandler.getTextChannel(guild)!!).subscribe()
    }

    override fun loadFailed(exception: FriendlyException?) {
        if (exception != null) {
            Messages.sendException(exception, VoiceHandler.getTextChannel(guild)!!).subscribe()
        }
    }

    override fun trackLoaded(track: AudioTrack?) {
        VoiceHandler.getQueueManager(guild)!!.enqueue(track!!).subscribe()
    }

    override fun noMatches() {
        Messages.sendError(Lang.getString("error_404"), VoiceHandler.getTextChannel(guild)!!).subscribe()
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {
        VoiceHandler.getQueueManager(guild)!!.enqueuePlaylist(playlist!!)
    }

    private fun sendInfoMessage(track: AudioTrack){
        if(!track.info.isStream) {

            if(lastInfoMessage != null) {
                lastInfoMessage!!.delete().subscribe()
            }

            var millis = track.duration
            var hours = 0
            var min = 0
            var sec = 0

            while(millis >= 1000){
                ++sec
                millis -= 1000
            }
            while(sec >= 60){
                ++min
                sec -= 60
            }
            while(min >= 60){
                ++hours
                min -= 60
            }

            var h: String = hours.toString()
            var m: String = min.toString()
            var s: String = sec.toString()

            if(h.length <= 1) h = "0$h"
            if(m.length <= 1) m = "0$m"
            if(s.length <= 1) s = "0$s"

            val d: String = when (hours) {
                0 -> "${m}:${s}"
                else -> "${h}:${m}:${s}"
            }

            val looped: Boolean = VoiceHandler.getQueueManager(guild)!!.loop
            val shuffled: Boolean = VoiceHandler.getQueueManager(guild)!!.shuffle

            VoiceHandler.getTextChannel(guild)!!.createMessage { message -> run {
                message.setContent("**${Lang.getString("audio_now_playing")}**")
                message.setEmbed { embed -> run {
                    embed.setTitle(track.info.title)
                    embed.setDescription(" ")
                    embed.addField(Lang.getString("audio_channel"), track.info.author, false)
                    embed.addField(Lang.getString("audio_duration"), d, false)

                    if(looped || shuffled) {
                        embed.addField(Lang.getString("audio_settings"), when(looped){
                            true -> Lang.getString("info_looped")+" "
                            else -> ""
                        } + when(shuffled){
                            true -> Lang.getString("info_shuffle")
                            else -> ""
                        }, false)
                    }

                    embed.setUrl(track.info.uri)
                }}
            }}.subscribe { lastInfoMessage = it }
        }
    }

    private fun sendEnqueuedInfo(track: AudioTrack) {
        VoiceHandler.getTextChannel(guild)!!.createMessage { message -> run {
            message.setContent("**${Lang.getString("audio_enqueued_track").replace("%title%", track.info.title).replace("%position%", ""+(queue.indexOf(track)+1)).replace("%amount%", queue.size.toString())}**")
        }}.subscribe()
    }
    private fun sendEnqueuedListInfo(amount: Int) {
        VoiceHandler.getTextChannel(guild)!!.createMessage { message -> run {
            message.setContent("**${Lang.getString("audio_enqueued_list").replace("%amount%", amount.toString())}**")
        }}.subscribe()
    }

}