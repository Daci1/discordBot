package commands;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.discord.bot.daci_bot.App;

import AudioPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PlayCommand implements ICommand {

	private static PlayCommand instance;
	
	private PlayCommand() {}

	@Override
	public void handle(GuildMessageReceivedEvent event) {
		List<String> args = new ArrayList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
		TextChannel channel = event.getChannel();
		if (args.size() < 2) {
			channel.sendMessage(":x:** No **` URL `** was provided **").queue();
			return;
		}
		args.remove(0); //remove the command from the message
		
		StringBuilder builder = new StringBuilder();
		args.forEach(string -> builder.append(string + " "));
		String url = builder.toString();
		String https = "https://";
		if(url.startsWith(https)) {
			url = url.substring(https.length());
		}
		if(!isUrl(url)) {
			url = "ytsearch:" + url;
		}

		
		
		Guild guild = event.getGuild();
		Member self = guild.getMember(App.getBot().getSelfUser());
		GuildVoiceState selfVoiceState = self.getVoiceState();
		Member member = event.getMember();
		GuildVoiceState memberVoiceState = member.getVoiceState();
		
		if(!memberVoiceState.inVoiceChannel()) {
			channel.sendMessage(":x: **You need to be in a voice channel for this to work**").queue();
			return;
		}
		
		if (!selfVoiceState.inVoiceChannel()) {
//			channel.sendMessage(":x: **I need to be in a voice channel for this to work**	").queue();
			JoinCommand.getInstance().handle(event);
			
			
//			return;
			
		}else if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
			channel.sendMessage(":x: **You need to be in the same voice channel as me for this to work**").queue();
			return;
			}
//		System.out.println(url);
		PlayerManager.getInstance().loadAndPlay(channel, url);

	}

	public static PlayCommand getInstance() {
		if (instance == null) {
			instance = new PlayCommand();
		}
		return instance;
	}
	
	private boolean isUrl(String url) {
		return  !url.contains("playlist") && url.contains("list") && (url.startsWith("https://") || url.startsWith("http://") || url.startsWith("www."));
	}

}
