package com.github.daci1.discord_bot.commands.audio.player.commands;

import com.github.daci1.discord_bot.commands.CommandUtils;
import com.github.daci1.discord_bot.services.MembersStateService;
import com.github.daci1.discord_bot.services.PlayerManagerService;
import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import com.github.daci1.discord_bot.AudioPlayer.GuildMusicManager;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SkipCommand extends ListenerAdapter implements ISlashCommand {

    @Autowired
    private DiscordBotService discordBotService;

    @Autowired
    private PlayerManagerService playerManager;

    @Autowired
    private MembersStateService membersStateService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.SKIP.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member self = CommandUtils.getMemberFromGuildBySelfUser(guild, discordBotService.getBotSelfUser());
        Member requester = event.getMember();

        if (membersStateService.replyIfRequesterNotInVoiceChannel(event, requester)) {
            return;
        }

        if (membersStateService.replyIfBotNotInVoiceChannel(event, self)) {
            return;
        }

        if (membersStateService.replyIfBotNotInSameVoiceChannelAsRequester(event, self, requester)) {
            return;
        }

        final GuildMusicManager musicManager = playerManager.getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        if (audioPlayer.getPlayingTrack() == null) {
            event.getHook().sendMessage(":x: **There is no track playing currently**").queue();
            return;
        }
        musicManager.scheduler.nextTrack();
        event.getHook().sendMessage(":loud_sound: Skipped the current track").queue();
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
