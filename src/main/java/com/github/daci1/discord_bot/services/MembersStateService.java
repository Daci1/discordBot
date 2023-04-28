package com.github.daci1.discord_bot.services;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.stereotype.Component;

@Component
public class MembersStateService {

    public boolean isMemberInVoiceChannel(Member member) {
        return member.getVoiceState().inAudioChannel();
    }

    public boolean membersInSameVoiceChannel(Member firstMember, Member secondMember) {
        return firstMember.getVoiceState().getChannel().equals(secondMember.getVoiceState().getChannel());
    }

    public boolean replyIfRequesterNotInVoiceChannel(GenericComponentInteractionCreateEvent event, Member requester) {
        if (!isMemberInVoiceChannel(requester)) {
            event.getMessage().delete().queue();
            event.reply(":x: **You need to be in a voice channel for this to work.**").queue();

            return true;
        }
        return false;
    }

    public boolean replyIfRequesterNotInVoiceChannel(GenericCommandInteractionEvent event, Member requester) {
        if (!isMemberInVoiceChannel(requester)) {
            event.getHook().sendMessage(":x: **You need to be in a voice channel for this to work.**").queue();
            return true;
        }
        return false;
    }

    public boolean replyIfBotNotInVoiceChannel(GenericComponentInteractionCreateEvent event, Member bot) {
        if (!isMemberInVoiceChannel(bot)) {
            event.getMessage().delete().queue();
            event.reply(":x: **I need to be in a voice channel for this to work.**").queue();
            return true;
        }
        return false;
    }

    public boolean replyIfBotNotInVoiceChannel(GenericCommandInteractionEvent event, Member bot) {
        if (!isMemberInVoiceChannel(bot)) {
            event.getHook().sendMessage(":x: **I need to be in a voice channel for this to work.**").queue();
            return true;
        }
        return false;
    }

    public boolean replyIfBotNotInSameVoiceChannelAsRequester(GenericComponentInteractionCreateEvent event, Member bot, Member requester) {
        if (!membersInSameVoiceChannel(requester, bot)) {
            event.getMessage().delete().queue();
            event.reply(":x: **You need to be in the same voice channel as me for this to work.**").queue();
            return true;
        }
        return false;
    }

    public boolean replyIfBotNotInSameVoiceChannelAsRequester(GenericCommandInteractionEvent event, Member bot, Member requester) {
        if (!membersInSameVoiceChannel(requester, bot)) {
            event.getHook().sendMessage(":x: **You need to be in the same voice channel as me for this to work.**").queue();
            return true;
        }
        return false;
    }

    public boolean replyIfBotInVoiceChannel(GenericCommandInteractionEvent event, Member bot) {
        if (isMemberInVoiceChannel(bot)) {
            event.getHook().sendMessage(":x: **I am already in a voice channel!**").queue();
            return true;
        }
        return false;
    }

    public boolean triesConnectingBotToVoiceChannel(InteractionHook interactionHook, AudioManager audioManager, AudioChannel audioChannel) {
        boolean successfullyConnected = true;
        try {
            audioManager.openAudioConnection(audioChannel);
            interactionHook.sendMessageFormat(":loud_sound: Joined `%s`.", audioChannel.getName()).queue();
        } catch (InsufficientPermissionException e) {
            successfullyConnected = false;
            interactionHook.sendMessageFormat(":x: **I don't have permission to join the voice channel: `%s`.**", audioChannel.getName()).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return successfullyConnected;
    }

    public void disconnectBotFromVoiceChannel(AudioManager audioManager) {
        audioManager.closeAudioConnection();
    }
}
