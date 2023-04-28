package com.github.daci1.discord_bot.commands.audio.player.commands;


import com.github.daci1.discord_bot.commands.CommandUtils;
import com.github.daci1.discord_bot.services.MembersStateService;
import com.github.daci1.discord_bot.services.PlayerManagerService;
import com.github.daci1.discord_bot.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
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
    private PlayerManagerService playerManager;

    @Autowired
    private MembersStateService membersStateService;

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
        Member self = CommandUtils.getMemberFromGuildBySelfUser(guild, discordBotService.getBotSelfUser());
        Member requester = event.getMember();

        if (membersStateService.replyIfRequesterNotInVoiceChannel(event, requester)) {
            return;
        }

        if (membersStateService.replyIfBotInVoiceChannel(event, self)) {
            return;
        }

        AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if (!membersStateService.triesConnectingBotToVoice(event.getHook(), event.getGuild().getAudioManager(), audioChannel)) {
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
