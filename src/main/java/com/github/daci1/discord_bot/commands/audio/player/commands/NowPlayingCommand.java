package com.github.daci1.discord_bot.commands.audio.player.commands;

import com.github.daci1.discord_bot.services.MembersStateService;
import com.github.daci1.discord_bot.services.PlayerManagerService;
import com.github.daci1.discord_bot.services.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NowPlayingCommand extends ListenerAdapter implements ISlashCommand {

    private final DiscordBotService discordBotService;
    private final MembersStateService membersStateService;
    private final PlayerManagerService playerManagerService;

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

        if (!membersStateService.isMemberInVoiceChannel(self)) {
            event.getHook().sendMessage(":x: I am not playing anything at the moment.").queue();
            return;
        }

        this.playerManagerService.nowPlaying(event.getHook(), guild);
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
