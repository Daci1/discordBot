package com.github.daci1.discord_bot.commands;

public enum SlashCommand {
    JOIN("join"),
    PLAY("play"),
    NOW_PLAYING("now-playing"),
    QUEUE("queue"),
    REPEAT("repeat"),
    SKIP("skip"),
    STOP("stop"),
    LEAVE("leave");


    private final String commandName;

    SlashCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getName() {
        return this.commandName;
    }
}
