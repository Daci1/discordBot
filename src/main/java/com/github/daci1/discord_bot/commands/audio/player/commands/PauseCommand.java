package com.github.daci1.discord_bot.commands.audio.player.commands;

import com.github.daci1.discord_bot.services.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import com.github.daci1.discord_bot.services.MembersStateService;
import com.github.daci1.discord_bot.services.PlayerManagerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PauseCommand extends ListenerAdapter implements ISlashCommand {

    private final DiscordBotService discordBotService;
    private final PlayerManagerService playerManagerService;
    private final MembersStateService membersStateService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.PAUSE.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member self = guild.getMember(discordBotService.getBotSelfUser());
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

        final boolean isPaused = this.playerManagerService.pause(event.getGuild());
        if (isPaused) {
            event.getHook().sendMessage(":pause_button: **Paused playing current track.**").queue();
        } else {
            event.getHook().sendMessage(":arrow_forward: **Resumed playing current track.**").queue();
        }
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
