package commands;

import com.discord.bot.daci_bot.App;

import AudioPlayer.GuildMusicManager;
import AudioPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RepeatCommand implements ICommand{

private static RepeatCommand instance;
	
	private RepeatCommand() {}
	
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
		
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
		musicManager.scheduler.repeating = !musicManager.scheduler.repeating;
		
		channel.sendMessageFormat(":repeat: %s :repeat:", musicManager.scheduler.repeating ? "Enabled" : "Disabled").queue();
	}
	
	public static RepeatCommand getInstance() {
		if(instance == null) {
			instance = new RepeatCommand();
		}
		
		return instance;
	}

}
