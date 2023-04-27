package com.github.daci1.discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ISlashCommand {
    void handleEvent(SlashCommandInteractionEvent event);

    void registerCommand();
}
