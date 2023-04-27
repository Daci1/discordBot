package com.github.daci1.discord_bot.commands.audio.player.commands;

import com.github.daci1.discord_bot.AudioPlayer.GuildMusicManager;
import com.github.daci1.discord_bot.AudioPlayer.PlayerManager;
import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NowPlayingCommand extends ListenerAdapter implements ISlashCommand {

    @Autowired
    private DiscordBotService discordBotService;

    @Autowired
    private PlayerManager playerManager;


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.NOW_PLAYING.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member self = guild.getMember(discordBotService.getBotSelfUser());
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            event.getHook().sendMessage(":x: I am not playing anything at the moment.").queue();
            return;
        }

        final GuildMusicManager musicManager = playerManager.getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final AudioTrack audioTrack = audioPlayer.getPlayingTrack();

        if (audioTrack == null) {
            event.getHook().sendMessage(":mute: There is no track playing currently.").queue();
            return;
        }
        final AudioTrackInfo info = audioTrack.getInfo();

        event.getHook().sendMessageFormat(":loud_sound: Now playing `%s` by `%s` (Link: <%s>).", info.title, info.author, info.uri).queue();
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
