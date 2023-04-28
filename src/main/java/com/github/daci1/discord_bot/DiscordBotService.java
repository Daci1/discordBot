package com.github.daci1.discord_bot;

import com.github.daci1.discord_bot.commands.SlashCommand;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.stereotype.Component;

@Component
public class DiscordBotService {
    private JDA discordBot;

    @PostConstruct
    private void initBot() {
        discordBot = JDABuilder
                .createDefault(System.getenv("DISCORD_BOT_TOKEN"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
                .setMemberCachePolicy(MemberCachePolicy.VOICE).build();

        Presence botPresence = discordBot.getPresence();
        botPresence.setStatus(OnlineStatus.ONLINE);

        this.addBotSlashCommands();
    }

    public SelfUser getBotSelfUser() {
        return discordBot.getSelfUser();
    }

    public void registerSlashCommand(ListenerAdapter adapter) {
        discordBot.addEventListener(adapter);
    }

    private void addBotSlashCommands() {
        discordBot.updateCommands().addCommands(
                Commands.slash(SlashCommand.JOIN.getName(), "Tries joining your current voice channel."),
                Commands.slash(SlashCommand.PLAY.getName(), "Plays a song in your voice channel.")
                        .addOption(OptionType.STRING, "input", "Link or name of the track.", true),
                Commands.slash(SlashCommand.NOW_PLAYING.getName(), "Displays the current playing track."),
                Commands.slash(SlashCommand.QUEUE.getName(), "Displays the current queue of tracks."),
                Commands.slash(SlashCommand.REPEAT.getName(), "Toggles repeat for the current playing track."),
                Commands.slash(SlashCommand.SKIP.getName(), "Skips the current playing track."),
                Commands.slash(SlashCommand.STOP.getName(), "Stops and clears the current queue."),
                Commands.slash(SlashCommand.LEAVE.getName(), "Leaves the current voice channel."),
                Commands.slash(SlashCommand.PAUSE.getName(), "Pauses the current playing track.")
        ).queue();
    }
}
