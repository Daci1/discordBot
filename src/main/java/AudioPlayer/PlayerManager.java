package AudioPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class PlayerManager {
	private static PlayerManager instance;
	private final Map<Long, GuildMusicManager> musicManagers;
	private final AudioPlayerManager audioPlayerManager;

	private PlayerManager() {
		this.musicManagers = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();

		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

			return guildMusicManager;
		});
	}

	public void loadAndPlay(MessageChannel channel, String trackUrl, Guild guild) {
		final GuildMusicManager musicManager = this.getMusicManager(guild);
		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {

				musicManager.scheduler.queue(track);
				channel.sendMessage("Adding to queue: `").addContent(track.getInfo().title).addContent("` by `")
						.addContent(track.getInfo().author).addContent("`").queue();

			}

			@Override
			public void playlistLoaded(AudioPlaylist playList) {
				// TODO add only one song if it is a simple search
				List<AudioTrack> tracks = playList.getTracks();

				String playListName = playList.getName();
				if (playListName.contains("Search results for: ")) {
					AudioTrack track = playList.getTracks().get(0);
					musicManager.scheduler.queue(track);
					channel.sendMessage("Adding to queue: `").addContent(track.getInfo().title).addContent("` by `")
							.addContent(track.getInfo().author).addContent("`").queue();
					return;
				}

				for (AudioTrack track : tracks) {
					musicManager.scheduler.queue(track);
				}

				channel.sendMessage("Adding to queue: `").addContent(String.valueOf(tracks.size()))
						.addContent("` tracks from playlist `").addContent(playList.getName()).addContent("`").queue();
			}

			@Override
			public void noMatches() {
				channel.sendMessage(":x:**No matches found**").queue();

			}

			@Override
			public void loadFailed(FriendlyException exception) {
				// TODO Auto-generated method stub

			}
		});
	}

	public static PlayerManager getInstance() {
		if (instance == null) {
			instance = new PlayerManager();
		}

		return instance;
	}
}
