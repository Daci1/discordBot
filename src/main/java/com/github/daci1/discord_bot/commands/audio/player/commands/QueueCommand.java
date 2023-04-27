package com.github.daci1.discord_bot.commands.audio.player.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import com.github.daci1.discord_bot.AudioPlayer.GuildMusicManager;
import com.github.daci1.discord_bot.AudioPlayer.PlayerManager;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueCommand extends ListenerAdapter implements ISlashCommand {

    @Autowired
    private PlayerManager playerManager;

    @Autowired
    private DiscordBotService discordBotService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.QUEUE.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        final GuildMusicManager musicManager = playerManager.getMusicManager(event.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            event.getHook().sendMessage("The queue is currently empty").queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final WebhookMessageCreateAction<Message> messageAction = event.getHook().sendMessage("**Current queue:**\n");
        for (int i = 0; i < trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            //TODO too big message exception :  java.lang.IllegalArgumentException: A message may not exceed 2000 characters. Please limit your input!
            messageAction
                    .addContent("#").addContent(String.valueOf(i + 1)).addContent(" `").addContent(info.title)
                    .addContent(" by ").addContent(info.author)
                    .addContent("` [`").addContent(formatTime(track.getDuration())).addContent("`]\n");
        }

        if (trackList.size() > trackCount) {
            messageAction
                    .addContent("And `")
                    .addContent(String.valueOf(trackList.size() - trackCount))
                    .addContent("` more...");
        }

        messageAction.queue();
    }

    private String formatTime(long timeInMillis) {

        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
