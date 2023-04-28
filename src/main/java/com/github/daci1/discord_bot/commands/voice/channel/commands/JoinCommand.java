package com.github.daci1.discord_bot.commands.voice.channel.commands;

import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.CommandUtils;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import com.github.daci1.discord_bot.services.MembersStateService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinCommand extends ListenerAdapter implements ISlashCommand {

    @Autowired
    private DiscordBotService discordBotService;

    @Autowired
    private MembersStateService membersStateService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.JOIN.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        AudioChannel channel = event.getMember().getVoiceState().getChannel();
        Member requester = event.getMember();
        Member self = CommandUtils.getMemberFromGuildBySelfUser(event.getGuild(), discordBotService.getBotSelfUser());

        if (membersStateService.replyIfRequesterNotInVoiceChannel(event, requester)) {
            return;
        }

        if (membersStateService.replyIfBotInVoiceChannel(event, self)) {
            return;
        }

        if (!membersStateService.triesConnectingBotToVoice(event.getHook(), event.getGuild().getAudioManager(), channel)) {
            return;
        }
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
