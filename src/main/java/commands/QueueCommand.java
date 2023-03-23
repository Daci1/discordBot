package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import AudioPlayer.GuildMusicManager;
import AudioPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class QueueCommand implements ICommand {
	private static QueueCommand instance;

	private QueueCommand() {
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		//TODO make a warning when calling the method while not in a voice channel
		final MessageChannel channel = event.getChannel();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
		
		if(queue.isEmpty()) {
			channel.sendMessage("The queue is currently empty").queue();
			return;
		}
		
		final int trackCount = Math.min(queue.size(), 20);
		final List<AudioTrack> trackList = new ArrayList(queue);
		final MessageCreateAction messageAction = channel.sendMessage("**Current queue:**\n");
		for(int i = 0; i < trackCount; i++) {
			final AudioTrack track = trackList.get(i);
			final AudioTrackInfo info = track.getInfo();
			
			//TODO too big message exception :  java.lang.IllegalArgumentException: A message may not exceed 2000 characters. Please limit your input!
			messageAction.addContent("#").addContent(String.valueOf(i + 1)).addContent(" `").addContent(info.title).addContent(" by ")
					.addContent(info.author).addContent("` [`").addContent(formatTime(track.getDuration())).addContent("`]\n");
		}
		
		if(trackList.size() > trackCount) {
			messageAction.addContent("And `").addContent(String.valueOf(trackList.size() - trackCount)).addContent("` more...");
		}
		
		messageAction.queue();
	}

	private String formatTime(long timeInMillis) {
		
		final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
		final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
		final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
		
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static QueueCommand getInstance() {
		if (instance == null) {
			instance = new QueueCommand();
		}

		return instance;
	}
}
