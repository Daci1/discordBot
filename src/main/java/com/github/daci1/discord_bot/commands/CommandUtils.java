package com.github.daci1.discord_bot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;

public class CommandUtils {

    public static Member getMemberFromGuildBySelfUser(Guild guild, SelfUser selfUser) {
        return guild.getMember(selfUser);
    }
}
