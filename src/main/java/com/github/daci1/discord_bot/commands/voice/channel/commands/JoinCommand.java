package com.github.daci1.discord_bot.commands.voice.channel.commands;

import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinCommand extends ListenerAdapter implements ISlashCommand {

    @Autowired
    private DiscordBotService discordBotService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.JOIN.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        //TODO already connected here
        AudioChannel channel = event.getMember().getVoiceState().getChannel();
        try {
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(channel);
            event.getHook().sendMessageFormat(":loud_sound: Joined `%s`.", channel.getName()).queue();
        } catch (InsufficientPermissionException e) {
            event.getHook().sendMessageFormat(":x: **I don't have permission to join the voice channel: `%s`.**", channel.getName()).queue();
        } catch (IllegalArgumentException e) {
            event.getHook().sendMessage(":x: **You are not connected to a voice channel.**").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
