package commands;

import com.discord.bot.daci_bot.App;

import AudioPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PlayCommand implements ICommand{

	private static PlayCommand instance;
	@Override
	public void handle(GuildMessageReceivedEvent event) {
		String url = event.getMessage().getContentRaw().split(" ")[1];
		TextChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		Member self = guild.getMember(App.getBot().getSelfUser());
		GuildVoiceState selfVoiceState = self.getVoiceState();
		
		if(!selfVoiceState.inVoiceChannel()) {
			channel.sendMessage("I need to be in a voice channel for this to work").queue();
			return;
		}
		
		Member member = event.getMember();
		GuildVoiceState memberVoiceState = member.getVoiceState();
		
		if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
			channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
			return;
		}
		
		PlayerManager.getInstance().loadAndPlay(channel,  url);
		
	}
	
	public static PlayCommand getInstance() {
		if(instance == null) {
			instance = new PlayCommand();
		}
		return instance;
	}

}
