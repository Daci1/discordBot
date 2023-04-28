package com.github.daci1.discord_bot.commands.audio.player.commands;

import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;

import com.github.daci1.discord_bot.services.PlayerManagerService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueCommand extends ListenerAdapter implements ISlashCommand {

    @Autowired
    private PlayerManagerService playerManagerService;

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
        this.playerManagerService.displayQueue(event.getHook(), event.getGuild());
    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }
}
