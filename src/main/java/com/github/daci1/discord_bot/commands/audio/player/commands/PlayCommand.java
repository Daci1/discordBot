package com.github.daci1.discord_bot.commands.audio.player.commands;


import com.github.daci1.discord_bot.commands.CommandUtils;
import com.github.daci1.discord_bot.services.MembersStateService;
import com.github.daci1.discord_bot.services.PlayerManagerService;
import com.github.daci1.discord_bot.services.DiscordBotService;
import com.github.daci1.discord_bot.commands.ISlashCommand;
import com.github.daci1.discord_bot.commands.SlashCommand;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayCommand extends ListenerAdapter implements ISlashCommand {

    Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    private final DiscordBotService discordBotService;
    private final PlayerManagerService playerManagerService;
    private final MembersStateService membersStateService;
    private static final String youtubeAbChannelRegex = "&ab_channel=.*";

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
        input = input.replaceAll(youtubeAbChannelRegex, "");
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

        AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if (!membersStateService.isMemberInVoiceChannel(self) && !membersStateService.triesConnectingBotToVoiceChannel(event.getHook(), event.getGuild().getAudioManager(), audioChannel)) {
            return;
        }

        InteractionHook interactionHook = event.getHook();
        playerManagerService.loadAndPlay(interactionHook, guild, input);

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member requester = event.getMember();
        Member self = event.getGuild().getMember(this.discordBotService.getBotSelfUser());
        if (membersStateService.replyIfRequesterNotInVoiceChannel(event, requester)) {
            return;
        }

        if (membersStateService.replyIfBotNotInVoiceChannel(event, self)) {
            return;
        }

        if (membersStateService.replyIfBotNotInSameVoiceChannelAsRequester(event, self, requester)) {
            return;
        }

        switch (event.getComponentId()) {
            case "repeat" -> {
                final boolean isRepeatingEnabled = this.playerManagerService.repeatCurrentSong(event.getGuild());
                event.editMessageFormat(":repeat: Repeat %s :repeat:", isRepeatingEnabled ? "Enabled" : "Disabled").queue();
            }
            case "pause" -> {
                final boolean isPaused = this.playerManagerService.pause(event.getGuild());
                if (isPaused) {
                    event.editMessage(":pause_button: **Paused playing current track.**").queue();
                } else {
                    event.editMessageFormat(":arrow_forward: **Resumed playing current track.**").queue();
                }
            }
            case "skip" -> {
                final boolean skippedSuccessful = this.playerManagerService.skipCurrentTrack(event.getGuild());
                if (skippedSuccessful) {
                    event.editMessage(":x: **There is no track playing currently**").queue();
                } else {
                    event.editMessage(":loud_sound: Skipped the current track").queue();
                }
            }
            case "leave" -> {
                event.getMessage().delete().queue();
                this.membersStateService.disconnectBotFromVoiceChannel(event.getGuild().getAudioManager());
                event.reply("Leaving `" + self.getVoiceState().getChannel().getName() + "` :hand_splayed:").queue();
            }
        }
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
