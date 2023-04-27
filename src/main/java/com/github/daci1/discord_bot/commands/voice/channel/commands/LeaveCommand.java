package com.github.daci1.discord_bot.commands.voice.channel.commands;

import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveCommand extends ListenerAdapter implements ISlashCommand {

    @Autowired
    private DiscordBotService discordBotService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.LEAVE.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member self = guild.getMember(discordBotService.getBotSelfUser());
        GuildVoiceState selfVoiceState = self.getVoiceState();
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessage(":x: **You need to be in a voice channel for this to work**").queue();
            return;
        }

        if (!selfVoiceState.inAudioChannel()) {
            event.getHook().sendMessage(":x: **I need to be in a voice channel for this to work**").queue();
            return;

        } else if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.getHook().sendMessage(":x: **You need to be in the same voice channel as me for this to work**").queue();
            return;
        }

        final AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();

        event.getHook().sendMessage("Leaving `" + selfVoiceState.getChannel().getName() + "` :hand_splayed:").queue();
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
