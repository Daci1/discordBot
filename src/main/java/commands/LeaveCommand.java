package commands;

import com.discord.bot.daci_bot.App;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class LeaveCommand implements ICommand {

	private static LeaveCommand instance;

	private LeaveCommand() {
	}

	@Override
	public void handle(GuildMessageReceivedEvent event) {
		TextChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		Member self = guild.getMember(App.getBot().getSelfUser());
		GuildVoiceState selfVoiceState = self.getVoiceState();
		Member member = event.getMember();
		GuildVoiceState memberVoiceState = member.getVoiceState();

		if (!memberVoiceState.inVoiceChannel()) {
			channel.sendMessage(":x: **You need to be in a voice channel for this to work**").queue();
			return;
		}

		if (!selfVoiceState.inVoiceChannel()) {
			channel.sendMessage(":x: **I need to be in a voice channel for this to work**	").queue();
			return;

		} else if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
			channel.sendMessage(":x: **You need to be in the same voice channel as me for this to work**").queue();
			return;
		}

		final AudioManager audioManager = guild.getAudioManager();
		audioManager.closeAudioConnection();
		
		channel.sendMessage("Leaving `" + selfVoiceState.getChannel().getName() + "` :hand_splayed:").queue();

	}

	public static LeaveCommand getInstance() {
		if (instance == null) {
			instance = new LeaveCommand();
		}

		return instance;
	}
}
