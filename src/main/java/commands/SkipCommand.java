package commands;

import com.discord.bot.daci_bot.App;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import AudioPlayer.GuildMusicManager;
import AudioPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SkipCommand implements ICommand{

	private static SkipCommand instance;
	
	private SkipCommand() {}
	
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
		final AudioPlayer audioPlayer = musicManager.audioPlayer;
		if(audioPlayer.getPlayingTrack() == null) {
			channel.sendMessage(":x: **There is no track playing currently**").queue();
			return;
		}
		musicManager.scheduler.nextTrack();
		channel.sendMessage(":loud_sound: Skipped the current track").queue();
	}
	
	public static SkipCommand getInstance() {
		if(instance == null) {
			instance = new SkipCommand();
		}
		
		return instance;
	}
}
