package com.github.daci1.discord_bot.commands.audio.player.commands;

import com.github.daci1.discord_bot.commands.CommandUtils;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import com.github.daci1.discord_bot.exceptions.RepeatEmptyQueueException;
import com.github.daci1.discord_bot.services.DiscordBotService;
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
public class RepeatQueueCommand extends ListenerAdapter implements ISlashCommand {
    private final DiscordBotService discordBotService;
    private  final PlayerManagerService playerManagerService;
    private final MembersStateService membersStateService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.REPEAT_QUEUE.getName())) {
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
        try {
            final boolean isRepeatQueueEnabled = playerManagerService.repeatCurrentQueue(guild);
            event.getHook().sendMessageFormat(":repeat: Repeat current Queue `%s` :repeat:", isRepeatQueueEnabled ? "Enabled" : "Disabled").queue();
        } catch (RepeatEmptyQueueException exception) {
            event.getHook().sendMessage(exception.getMessage()).queue();
        }


    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
