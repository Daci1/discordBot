package com.github.daci1.discord_bot.commands;

public enum SlashCommand {
    JOIN("join"),
    PLAY("play"),
    NOW_PLAYING("now-playing"),
    QUEUE("queue"),
    REPEAT("repeat"),
    REPEAT_QUEUE("repeat-queue"),
    SKIP("skip"),
    STOP("stop"),
    LEAVE("leave"),
    PAUSE("pause");


    private final String commandName;

    SlashCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getName() {
        return this.commandName;
    }
}
