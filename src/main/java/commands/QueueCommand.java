package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import AudioPlayer.GuildMusicManager;
import AudioPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class QueueCommand implements ICommand {
	private static QueueCommand instance;

	private QueueCommand() {
	}

	@Override
	public void handle(GuildMessageReceivedEvent event) {
		final TextChannel channel = event.getChannel();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
		
		if(queue.isEmpty()) {
			channel.sendMessage("The queue is currently empty").queue();
			return;
		}
		
		final int trackCount = Math.min(queue.size(), 20);
		final List<AudioTrack> trackList = new ArrayList(queue);
		final MessageAction messageAction = channel.sendMessage("**Current queue:**\n");
		System.out.println(trackCount);
		for(int i = 0; i < trackCount; i++) {
			final AudioTrack track = trackList.get(i);
			final AudioTrackInfo info = track.getInfo();
			
			messageAction.append("#").append(String.valueOf(i + 1)).append(" `").append(info.title).append(" by ")
					.append(info.author).append("` [`").append(formatTime(track.getDuration())).append("`]\n");
		}
		
		if(trackList.size() > trackCount) {
			messageAction.append("And `").append(String.valueOf(trackList.size() - trackCount)).append("` more...");
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
