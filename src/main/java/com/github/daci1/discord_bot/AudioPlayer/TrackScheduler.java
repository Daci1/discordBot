package com.github.daci1.discord_bot.AudioPlayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.daci1.discord_bot.exceptions.RepeatEmptyQueueException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    @Getter private final BlockingQueue<AudioTrack> queue;
    private final BlockingQueue<AudioTrack> queueToRepeat = new LinkedBlockingQueue<>();
    private AudioTrack currentPlayingTrack;
    private boolean repeating = false;
    private boolean repeatQueue = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (this.currentPlayingTrack != null) {
            this.queue.offer(track);
            if(this.repeatQueue) {
                this.queueToRepeat.offer(track);
            }
        } else {
            this.startTrack(track, false);
        }
    }

    public void pause() {
        if (!this.player.isPaused()) {
            this.player.setPaused(true);
        }
    }

    public boolean isPaused() {
        return this.player.isPaused();
    }

    public void resume() {
        if (this.player.isPaused()) {
            this.player.setPaused(false);
        }
    }

    public void clearQueues() {
        this.queue.clear();
        this.queueToRepeat.clear();
    }

    public void nextTrack() {
        if (this.repeatQueue) {
            startNextTrackFromRepeatingQueue();
        } else {
           this.startTrack(this.queue.poll(), false);
        }
        this.player.setPaused(false);
    }

    private void copyCurrentQueueToRepeatingQueue() {
        this.queue.forEach(audioTrack -> this.queueToRepeat.add(audioTrack.makeClone()));
    }

    public boolean toggleRepeating() {
        this.repeating = !this.repeating;
        return this.repeating;
    }

    public boolean toggleRepeatQueue() throws RepeatEmptyQueueException{
        this.repeatQueue = !this.repeatQueue;
        if (this.repeatQueue) {
            if(this.queue.size() == 0 && this.currentPlayingTrack == null) {
                this.repeatQueue = false;
                throw new RepeatEmptyQueueException(":x: The current queue is `empty`! :x:");
            }
            this.queue.add(currentPlayingTrack.makeClone());
            this.copyCurrentQueueToRepeatingQueue();
        } else {
            queueToRepeat.clear();
        }
        return this.repeatQueue;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (this.repeating) {
                this.player.startTrack(track.makeClone(), false);
            } else {
                if(this.repeatQueue) {
                    this.startNextTrackFromRepeatingQueue();
                } else {
                    nextTrack();
                }
            }
        }
    }

    private void startNextTrackFromRepeatingQueue() {
        if (!this.queueToRepeat.isEmpty()) {
            this.startTrack(this.queueToRepeat.poll(), false);
        } else {
            this.copyCurrentQueueToRepeatingQueue();
            this.startTrack(currentPlayingTrack, false);
        }
    }

    private boolean startTrack(AudioTrack audioTrack, boolean noInterrupt) {
        this.currentPlayingTrack = audioTrack != null ?audioTrack.makeClone() : null;
        return this.player.startTrack(audioTrack, noInterrupt);
    }
}
