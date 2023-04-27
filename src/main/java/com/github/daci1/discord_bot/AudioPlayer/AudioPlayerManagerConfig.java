package com.github.daci1.discord_bot.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AudioPlayerManagerConfig {
    @Bean
    public AudioPlayerManager audioPlayerManager() {
        return new DefaultAudioPlayerManager();
    }
}
