package commands;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand implements ICommand {

	private static JoinCommand instance;

	private JoinCommand() {
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		//TODO already connected here
		AudioChannel channel = event.getMember().getVoiceState().getChannel();
		MessageChannel textChannel = event.getChannel();
		try {
			AudioManager audioManager = event.getGuild().getAudioManager();
			audioManager.openAudioConnection(channel);
			textChannel.sendMessage(":loud_sound: Joined `" + channel.getName() + "`").queue();
		} catch (InsufficientPermissionException e) {
			textChannel.sendMessage(":x: **I don't have permission to join the voice channel: `" + channel.getName() + "`**")
					.queue();
		} catch (IllegalArgumentException e) {
			textChannel.sendMessage(":x: **You are  not connected to a voice channel**").queue();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static JoinCommand getInstance() {
		if (instance == null) {
			instance = new JoinCommand();
		}
		return instance;
	}

}
