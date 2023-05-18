package com.github.daci1.discord_bot.AudioPlayer;

import com.github.daci1.discord_bot.exceptions.RepeatEmptyQueueException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;

public class GuildMusicManager {
    @Getter private final AudioPlayer audioPlayer;
    @Getter private final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager) {
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    public boolean toggleRepeating() {
        return this.scheduler.toggleRepeating();
    }

    public boolean toggleQueueRepeat() throws RepeatEmptyQueueException {
        return this.scheduler.toggleRepeatQueue();
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
}
