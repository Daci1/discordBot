package com.github.daci1.discord_bot.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.github.daci1.discord_bot.AudioPlayer.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.springframework.stereotype.Component;

@Component
public class PlayerManagerService {
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManagerService(AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;
        this.musicManagers = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public boolean skipCurrentTrack(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        if (audioPlayer.getPlayingTrack() == null) {
            return false;
        }
        musicManager.scheduler.nextTrack();
        return true;
    }

    public void stopAndClearQueue(InteractionHook interactionHook, Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        interactionHook.sendMessage(":loud_sound: The player has been stopped and the queue has been cleared").queue();
    }

    public boolean pause(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        if (!musicManager.scheduler.player.isPaused()) {
            musicManager.scheduler.pause();
            return true;
        } else {
            musicManager.scheduler.resume();
            return false;
        }
    }

    public boolean repeatCurrentSong(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        musicManager.scheduler.repeating = !musicManager.scheduler.repeating;
        return musicManager.scheduler.repeating;
    }

    public void displayQueue(InteractionHook interactionHook, Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            interactionHook.sendMessage("The queue is currently empty").queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final WebhookMessageCreateAction<Message> messageAction = interactionHook.sendMessage("**Current queue:**\n");
        for (int i = 0; i < trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            String content = String.format("#%s `%s by %s` [`%s`]\n", i + 1, info.title, info.author, formatTime(track.getDuration()));

            if (messageAction.getContent().length() + content.length() > 2000) {
                break;
            }

            messageAction.addContent(content);
        }

        if (trackList.size() > trackCount) {
            messageAction
                    .addContent("And `")
                    .addContent(String.valueOf(trackList.size() - trackCount))
                    .addContent("` more...");
        }

        messageAction.queue();
    }

    public void nowPlaying(InteractionHook interactionHook, Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final AudioTrack audioTrack = audioPlayer.getPlayingTrack();
        if (audioTrack == null) {
            interactionHook.sendMessage(":mute: There is no track playing currently.").queue();
            return;
        }
        final AudioTrackInfo info = audioTrack.getInfo();

        interactionHook.sendMessageFormat(":loud_sound: Now playing `%s` by `%s` (Link: <%s>).", info.title, info.author, info.uri).queue();
    }

    private String formatTime(long timeInMillis) {

        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(InteractionHook interactionHook, Guild guild, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {

                musicManager.scheduler.queue(track);
                WebhookMessageCreateAction<Message> trackMessage = interactionHook.sendMessage("Adding to queue: `")
                        .addContent(track.getInfo().title)
                        .addContent("` by `")
                        .addContent(track.getInfo().author)
                        .addContent("`");
                setButtonInteractions(trackMessage).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playList) {
                // TODO add only one song if it is a simple search
                List<AudioTrack> tracks = playList.getTracks();

                String playListName = playList.getName();
                if (playListName.contains("Search results for: ")) {
                    AudioTrack track = playList.getTracks().get(0);
                    musicManager.scheduler.queue(track);
                    WebhookMessageCreateAction<Message> trackMessage = interactionHook.sendMessage("Adding to queue: `")
                            .addContent(track.getInfo().title)
                            .addContent("` by `")
                            .addContent(track.getInfo().author)
                            .addContent("`");
                    setButtonInteractions(trackMessage).queue();
                    return;
                }

                for (AudioTrack track : tracks) {
                    musicManager.scheduler.queue(track);
                }

                WebhookMessageCreateAction<Message> queueMessage = interactionHook.sendMessage("Adding to queue: `")
                        .addContent(String.valueOf(tracks.size()))
                        .addContent("` tracks from playlist `")
                        .addContent(playList.getName())
                        .addContent("`");

                setButtonInteractions(queueMessage).queue();
            }

            @Override
            public void noMatches() {
                interactionHook.sendMessage(":x:**No matches found**").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                interactionHook.sendMessage(":x: **Some error happened** :x:");
            }
        });
    }

    public WebhookMessageCreateAction<Message> setButtonInteractions(WebhookMessageCreateAction<Message> webhookMessageCreateAction) {
        return webhookMessageCreateAction
                .setActionRow(
                        Button.primary("repeat", Emoji.fromUnicode("U+1F501")),
                        Button.primary("pause", Emoji.fromUnicode("U+23EF")),
                        Button.primary("skip", Emoji.fromUnicode("U+23ED")),
                        Button.danger("leave", Emoji.fromUnicode("U+1F44B"))
                );
    }
}
