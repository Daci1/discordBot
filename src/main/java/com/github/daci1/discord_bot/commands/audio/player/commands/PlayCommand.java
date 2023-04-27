package com.github.daci1.discord_bot.commands.audio.player.commands;


import com.github.daci1.discord_bot.AudioPlayer.PlayerManager;
import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.voice.channel.commands.JoinCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayCommand extends ListenerAdapter implements ISlashCommand {

    Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    @Autowired
    private DiscordBotService discordBotService;

    @Autowired
    private PlayerManager playerManager;

    @Autowired
    private JoinCommand joinCommand;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(SlashCommand.PLAY.getName())) {
            event.deferReply().queue();
            this.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(SlashCommandInteractionEvent event) {
        String input = "";
        try {
            input = event.getOption("input").getAsString();
        } catch (NullPointerException exception) {
            logger.debug("Empty play command received.");
        }

        if (input.trim().equals("")) {
            event.reply(":x:** No **` URL `** was provided **").queue();
            return;
        }

        String https = "https://";
        if (input.startsWith(https)) {
            input = input.substring(https.length());
        }

        if (!isUrl(input)) {
            input = "ytsearch:" + input;
        }

        Guild guild = event.getGuild();
        Member self = guild.getMember(discordBotService.getBotSelfUser());
        GuildVoiceState selfVoiceState = self.getVoiceState();
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessage(":x: **You need to be in a voice channel for this to work.**").queue();
            return;
        }

        if (!selfVoiceState.inAudioChannel()) {
            joinCommand.handleEvent(event);
        } else if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.getHook().sendMessage(":x: **You need to be in the same voice channel as me for this to work.**").queue();
            return;
        }

        InteractionHook interactionHook = event.getHook();
        playerManager.loadAndPlay(interactionHook, input, guild);

    }

    @Override
    @PostConstruct
    public void registerCommand() {
        this.discordBotService.registerSlashCommand(this);
    }

    private boolean isUrl(String url) {
        return !url.contains("playlist") && url.contains("list") && (url.startsWith("https://") || url.startsWith("www."));
    }

}
