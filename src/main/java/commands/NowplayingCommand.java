package commands;

import com.discord.bot.daci_bot.App;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import AudioPlayer.GuildMusicManager;
import AudioPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowplayingCommand implements ICommand{

private static NowplayingCommand instance;
	
	private NowplayingCommand() {}
	
	@Override
	public void handle(MessageReceivedEvent event) {
		MessageChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		Member self = guild.getMember(App.getBot().getSelfUser());
		GuildVoiceState selfVoiceState = self.getVoiceState();
		Member member = event.getMember();

		if (!selfVoiceState.inAudioChannel()) {
			channel.sendMessage(":mute: There is no track playing currently").queue();
			return;
		}
		
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
		final AudioPlayer audioPlayer = musicManager.audioPlayer;
		final AudioTrack audioTrack = audioPlayer.getPlayingTrack();
		
		if(audioTrack == null) {
			channel.sendMessage(":mute: There is no track playing currently").queue();
			return;
		}
		final AudioTrackInfo info = audioTrack.getInfo();
		
		channel.sendMessageFormat(":loud_sound: Now playing `%s` by `%s` (Link: <%s>)", info.title, info.author, info.uri).queue();
	}
	
	public static NowplayingCommand getInstance() {
		if(instance == null) {
			instance = new NowplayingCommand();
		}
		
		return instance;
	}

}
